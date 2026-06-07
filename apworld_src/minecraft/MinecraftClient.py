import argparse
import io
import json
import logging
import os
import pkgutil
import re
import shutil
import subprocess
import sys
import threading
import time
import zipfile
from base64 import b64encode, b64decode
from collections import defaultdict

from enum import Enum
from math import floor, log
from queue import Queue
from typing import List, Optional, TYPE_CHECKING, Any, TypedDict

import Utils

from kivy import Config
from kivy.core.window import Window
from kivy.core.image import Image as CoreImage
from kivy.clock import Clock, mainthread
from kivy.lang import Builder
from kivy.properties import StringProperty, NumericProperty, ObjectProperty, ListProperty
from kivy.uix.popup import Popup
from kivy.uix.screenmanager import NoTransition
from kivy.uix.textinput import TextInput
from kivy.utils import escape_markup

from kivymd.app import MDApp
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.gridlayout import MDGridLayout
from kivymd.uix.label import MDLabel
from kivymd.uix.recycleview import MDRecycleView
from kivymd.uix.screenmanager import MDScreenManager
from kivymd.uix.screen import MDScreen
from kivymd.uix.stacklayout import MDStackLayout
from kivymd.uix.widget import MDWidget

from worlds.minecraft.downloader import StepsStep, SyncStep, BytesToStringStep
from worlds.minecraft.downloader.Java import DownloadJava
from worlds.minecraft.downloader.NeoForge import DownloadNeoForge
from worlds.minecraft.downloader.Utilities import DownloadStep, FetchStep


if TYPE_CHECKING:
    from worlds.minecraft import MinecraftSettings

logger = logging.getLogger("MinecraftClient")

version_file_endpoint = "https://raw.githubusercontent.com/qixils/NeoForgeAP/refs/heads/main/versions/minecraft_versions.json"

default_save_name = "Archipelago"

class VersionsJson(TypedDict):
    version: str
    channel: str
    data: int
    java: int
    minecraft: str
    url: str

def load_text(*path: str):
    data_path = 'worlds.minecraft' if __name__ == '__main__' else __name__
    return pkgutil.get_data(data_path, "/".join(path)).decode()


def load_image(*path: str):
    data_path = 'worlds.minecraft' if __name__ == '__main__' else __name__
    data = io.BytesIO(pkgutil.get_data(data_path, "/".join(path)))
    texture = CoreImage(data, ext="png")
    return texture


def format_bytes(size):
    power = 0 if size <= 0 else floor(log(size, 1024))
    return f"{round(size / 1024 ** power, 2)} {['B', 'KB', 'MB', 'GB', 'TB'][int(power)]}"


def get_options() -> 'MinecraftSettings':
    return Utils.get_settings()['minecraft_options']

class ServerStatus(Enum):
    STOPPED = 0
    STARTING = 1
    RUNNING = 2

    def __lt__(self, other):
        return self.value < other.value

    def __gt__(self, other):
        return self.value > other.value

    def __eq__(self, other):
        return self.value == other.value


def get_recent_items(options: 'MinecraftSettings') -> List:
    if not os.path.isdir(options.server_directory):
        os.makedirs(options.server_directory)
    saves = []
    for directory in os.listdir(options.server_directory):
        try:
            if directory.startswith("Archipelago-"):
                world_dir = os.path.join(options.server_directory, directory)

                save = os.path.join(world_dir, "save.apmc")
                if not os.path.isfile(save):
                    continue

                metadata_path = os.path.join(world_dir, "metadata.json")
                if not os.path.exists(metadata_path):
                    # it doesn't really matter if the metadata exists...
                    # *except* it suggests the save.apmc is using the old broken format
                    # so we ignore this entry to force the user to re-import their apmc
                    continue

                description = default_save_name
                with open(metadata_path, "r") as jsonfile:
                    metadata = json.load(jsonfile)
                    if 'description' in metadata:
                        description = metadata["description"]
                saves.append((description, directory))
        except:
            pass
    return saves


class MinecraftClient(MDApp):
    stop = threading.Event()

    def __init__(self, args, **kwargs):
        super().__init__(**kwargs)
        self.index = 0
        self.welcome_window: Optional[WelcomeWindow] = None
        self.window_manager: Optional[WindowManager] = None
        self.server_window: Optional[ServerWindow] = None
        self.minecraft_versions: defaultdict[str, List[VersionsJson]] = defaultdict(lambda: list())
        # release channel to list of versions, I guess
        self.apmc = None
        self.version: VersionsJson = dict()
        self.server = None
        self.java_url = None
        self.status: ServerStatus = ServerStatus.STOPPED
        self.apmc_path = None
        self.mod_info: List[VersionsJson] = list()
        self.release_chanel = None
        self.args = args
        self.closing_at = 0
        Utils.init_logging('MinecraftClient')
        logger.info(f"Client Initialized")

    # Handles (re)loading mod info, whether from a successful HTTP request or not
    def _handle_mod_info(self, context: dict[str, Any], resp: Optional[Any] = None) -> None:
        options = get_options()
        fp = os.path.join(options.server_directory, 'ap-version.json')
        logger.debug(f"Got response: {resp}")
        if resp:
            self.mod_info: List[VersionsJson] = json.loads(resp)
            os.makedirs(options.server_directory, exist_ok=True)
            with open(fp, 'w') as f:
                json.dump(self.mod_info, f)
            self._init_mc_versions()
            return

        if not os.path.exists(fp):
            return

        try:
            with open(fp, 'r') as f:
                self.mod_info: List[VersionsJson] = json.load(f)
            self._init_mc_versions()
        except:
            logger.error("Failed to parse ap-version JSON", exc_info=True)


    def _init_mc_versions(self):
        self.minecraft_versions.clear()
        for data in self.mod_info:
            self.minecraft_versions[data['channel']].append(data)

    # Handles initializing the mod info fetching process
    def _init_mod_info(self):
        StepsStep(
            "Download Versions",
            SyncStep(self._handle_mod_info), # Load the cached JSON file
            FetchStep(url=version_file_endpoint), # Download the latest version
            BytesToStringStep(),
            SyncStep(self._handle_mod_info), # Save the latest version (if available)
            SyncStep(self.auto_start_server),
        ).run(dict(), error_ok=True)

    def build(self):
        logger.info(f"building client")
        # TODO: Rewrite minecraft.kv to work with KivyMD. Look under the data file.
        Builder.load_string(load_text("layouts", "minecraft.kv"))
        self.window_manager = WindowManager(transition=NoTransition())
        self.welcome_window = WelcomeWindow(self)
        self.server_window = ServerWindow(self)
        self.window_manager.add_widget(self.welcome_window)
        self.window_manager.add_widget(self.server_window)
        logger.info(f"client built")

        logger.info(f"binding on close request")
        Window.bind(on_request_close=self.on_request_close)

        logger.info(f"returning window manager")
        return self.window_manager

    def on_start(self):
        # send our request out to fetch the versions file
        logger.info(f"fetching versions file")
        self._init_mod_info()

    def on_request_close(self, *arg):
        # if the server is stopped already then we don't need to intervene
        if self.stop.is_set():
            return
        
        # likewise if there is no server, nothing to worry about
        if not self.server:
            return
        
        # if this is the first request to close the server, then let's send the shutdown signals
        if self.closing_at == 0:
            # save when shutdown started using increasing clock
            # (`close` function's `dt` arg does not work like you might expect from the docs description; it is just a delta, so it isn't useful)
            self.closing_at = time.monotonic()

            # send `/stop` to start server shutdown
            self.send_command("stop")

            # check every tenth second for the server to be closed
            Clock.schedule_interval(self.close, 0.1)
        
        # cancel the close request (we'll close it ourselves when we're ready)
        return True


    def close(self, dt):
        # determine if X seconds have elapsed since starting
        elapsed = time.monotonic() - self.closing_at
        force_close = elapsed >= 15
        if force_close:
            # server is not responding
            try:
                # attempt somewhat graceful shutdown
                self.server.terminate()
                self.server.wait(timeout=2)
            except:
                try:
                    # attempt less graceful shutdown
                    self.server.kill()
                    self.server.wait(timeout=2)
                except Exception as e:
                    logger.warning("Failed to kill Minecraft server", exc_info=e)
                    pass

        # once server is stopped, close kivy window (has to be done manually since the close request was cancelled)
        if self.stop.is_set() or force_close:
            super().stop()

    def get_application_icon(self):
        return load_image("assets", "icon.png")

    def init(self, dt=None):
        options = get_options()
        layout: MDWidget = self.welcome_window.ids.saves
        layout.clear_widgets()
        saves = get_recent_items(options)
        if len(saves) == 0:
            layout.add_widget(MDLabel(text="No saves"))
        else:
            for name, path in saves:
                layout.add_widget(RecentItem(name=name, path=path, client=self))

        ids: MDStackLayout = self.welcome_window.ids
        ids.path.value = options.server_directory
        ids.max_memory.value = options.max_heap_size
        ids.min_memory.value = options.min_heap_size
        ids.release_option.value = options.release_channel
        ids.release_option.mc_options = self.minecraft_versions.keys()

    def auto_start_server(self, context: dict[str, Any], *ignore):
        Clock.schedule_once(self.init, 1)
        self.apmc_path = os.path.abspath(self.args.apmc_file) if type(self.args.apmc_file) is str else None
        if self.apmc_path:
            self.open_apmc(path=self.apmc_path)

    def read_possible_b64(self, data: str) -> dict[str, Any]:
        if data.startswith("e"):
            return json.loads(b64decode(data))
        elif data.startswith("{"):
            return json.loads(data)

    def open_apmc(self, path=None):
        options = get_options()
        logger.info(self.mod_info)
        self.apmc_path = path
        if self.apmc_path is None:
            self.apmc_path = Utils.open_filename(title="Choose AP Minecraft file",
                                filetypes=(("Archipelago Minecraft", ["*.apmc"]),))
        if self.apmc_path is None or self.apmc_path == "" or not os.path.isfile(self.apmc_path):
            return

        # APContainer makes zips
        try:
            if zipfile.is_zipfile(self.apmc_path):
                with zipfile.ZipFile(self.apmc_path, 'r') as zf:
                    embedded_apmc = None
                    for entry in zf.infolist():
                        if entry.filename.endswith(".apmc") or entry.filename.endswith(".apmcmeta"):
                            embedded_apmc = entry.filename
                            break
                    if embedded_apmc:
                        with zf.open(embedded_apmc, 'r' ) as f:
                            data = f.read()
                            data = data.decode('utf-8')
                            apmc = self.read_possible_b64(data)
                    else:
                        logger.error(f"unable to find metadata file in zip")
            else:
                with open(self.apmc_path, 'r') as f:
                    data = f.read()
                    apmc = self.read_possible_b64(data)
        except Exception as e:
            logger.error("failed to load apmc file", exc_info=e)

        if apmc is None:
            info_dialog("Invalid APMC", content="""
            An error occurred while parsing the APMC.
            Please check the logs.
            """)
            return

        try:
            self.apmc = apmc
            # TODO(cang) Not sure if this is supposed to handle multiple valid MC versions
            self.version = next(filter(lambda entry: entry['data'] == self.apmc["client_version"],
                                        self.minecraft_versions[options.release_channel]))
            self.server_window.status.text = f"Initializing {self.version['minecraft']}"

            self.window_manager.current = "Server"
            self.start_server()
        except KeyError:
            logger.error(f"unable to find version {self.apmc['client_version']} on {options.release_channel}")
            self.log_error(f"unable to find version {self.apmc['client_version']} on {options.release_channel}")
            self.apmc_path = None

    def update_progress(self, value: float, content: str):
        self.server_window.update_progress(value, content)

    def set_description(self, text):
        self.apmc["description"] = text
        self.start_server()

    @mainthread
    def start_server(self) -> None:
        options = get_options()
        self.server_window.show_progress_bar_dialog("Installing Dependencies", "", 100)
        context: dict[str, Any] = dict()
        StepsStep(
            "Download Dependencies",
            DownloadJava(options.server_directory, self.version['java']),
            DownloadNeoForge(options.server_directory, confirm_prompt, self.version["minecraft"]),
            SyncStep(lambda context, data: (None, os.path.join(data.mods_dir, "Archipelago.jar"), self.version['version'])),
            DownloadStep(self.version["url"]),
            SyncStep(self.copy_apmc),
            SyncStep(lambda *args: self.server_window.close_progress_bar_dialog()),
            SyncStep(lambda *args: threading.Thread(target=self.server_thread, args=(context,)).start())
        ).run(
            context,
            on_failure=self.handle_server_start_failure,
            on_progress=self.update_progress,
        )

    def handle_server_start_failure(self, *args):
        logger.error(f"Dependency Downloads failed {args}", exc_info=True)
        self.server_window.close_progress_bar_dialog()
        self.window_manager.current = "Welcome"
        info_dialog("Server Start Failure", content="""
        An error occurred while starting the server.
        Please check the logs.
        """)

    def copy_apmc(self, context: dict[str, Any], *arg):
        if self.apmc_path is None:
            return
        neo_dir = context['neoforge_dir']
        apdata_dir = os.path.join(neo_dir, 'APData')
        os.makedirs(apdata_dir, exist_ok=True)

        neo_apmc = os.path.join(apdata_dir, 'save.apmc')
        shutil.copy2(self.apmc_path, neo_apmc)

    def server_thread(self, context: dict[str, Any]):
        options = get_options()

        self.status = ServerStatus.STOPPED
        self.server_window.background_color = (.5, .1, .1, 1)
        world_name = f"Archipelago-{self.apmc['seed_name']}-P{self.apmc['player_id']}"
        self.server_window.status.text = f"Archipelago-{self.apmc['seed_name']}-{self.apmc['player_name']}"
        world_dir = os.path.join(options.server_directory, world_name)
        if not os.path.isdir(world_dir):
            os.makedirs(world_dir)

        save_path = os.path.join(world_dir, "save.apmc")
        # we might consider skipping this if save_path exists
        # but for now we want to repair broken installs
        if self.apmc_path != save_path:
            shutil.copy2(self.apmc_path, save_path)

        metadata_path = os.path.join(world_dir, "metadata.json")
        if not os.path.isfile(metadata_path):
            metadata = {"description": "Archipelago"}
            with open(metadata_path, "w") as meta_file:
                json.dump(metadata, meta_file)

        os.environ["JAVA_OPTS"] = ""
        neo_run = context['neoforge_run_args']
        neo_dir = context['neoforge_dir']

        cmd = (*neo_run, "--nogui", "--world", world_name)
        logger.info(f"Invoking: {cmd}")
        logger.info(f"With working dir: {neo_dir}")
        self.server = subprocess.Popen(cmd,
                                       stderr=subprocess.PIPE,
                                       stdout=subprocess.PIPE,
                                       stdin=subprocess.PIPE,
                                       encoding="utf-8",
                                       text=True,
                                       cwd=neo_dir,
                                       )

        server_queue = Queue()
        stream_server_output(self.server.stdout, server_queue, self.server)
        stream_server_output(self.server.stderr, server_queue, self.server)

        while not self.stop.is_set():
            if self.server.poll() is not None:
                self.log_raw("[color=FFFF00]Minecraft server has exited.[/color]")
                self.stop.set()
                self.server_window.status.text = "Server Stopped"
                self.server_window.background_color = (.5, .1, .1, 1)
                self.status = ServerStatus.STOPPED

            while not server_queue.empty():
                raw_message: str = server_queue.get()

                match = re.match(r"^\[[0-9:]+] \[.+/(WARN|INFO|ERROR)] \[.+]: (.*)", raw_message)
                if match:
                    level = match.group(1)
                    msg = escape_markup(match.group(2))

                    if level == "WARN":
                        self.log_warn(msg)
                    elif level == "ERROR":
                        self.log_error(msg)
                    elif level == "INFO":
                        self.log_info(msg)
                else:
                    self.log_info(raw_message)

                if self.status < ServerStatus.RUNNING:

                    server_starting_match = re.match(r"\[[0-9:]+] (?:\[[A-Za-z0-9 /]+] ?){1,2}: Starting minecraft server version ([0-9.]+)", raw_message)
                    if server_starting_match:
                        self.log_info(f"Starting Minecraft {server_starting_match.group(1)}")
                        self.server_window.status.text = f"Starting Server for {server_starting_match.group(1)}"
                        self.server_window.background_color = (.5, .5, .0, 1)
                        self.version["minecraft"] = server_starting_match.group(1)
                        self.status = ServerStatus.STARTING

                    server_started_match = re.match(r"\[[0-9:]+] (?:\[[A-Za-z0-9 /]+] ?){1,2}: Done", raw_message)
                    if server_started_match:
                        self.server_window.status.text = f"Server Running. Connect to `127.0.0.1` in Minecraft {self.version['minecraft']}"
                        self.server_window.background_color = (.1, .5, .1, 1)
                        self.status = ServerStatus.RUNNING

                server_queue.task_done()
            time.sleep(0.01)

    def send_command(self, cmd):
        try:
            self.server.stdin.write(f'{cmd}\n')
            self.server.stdin.flush()
        except Exception:
            pass

    @mainthread
    def log_info(self, msg):
        self.server_window.log.on_message_markup(f"[b][INFO][/b] {escape_markup(msg)}")

    @mainthread
    def log_warn(self, msg):
        self.server_window.log.on_message_markup(f"[color=FFFF00][b][WARN][/b][/color] {escape_markup(msg)}")

    @mainthread
    def log_error(self, msg):
        self.server_window.log.on_message_markup(f"[color=FFFF00][b][ERROR][/b][/color] {escape_markup(msg)}")

    @mainthread
    def log_raw(self, msg):
        self.server_window.log.on_message_markup(msg)


def stream_server_output(pipe, queue, process):
    def queuer():
        while process.poll() is None:
            text = pipe.readline().rstrip().expandtabs()
            if text:
                queue.put_nowait(text)

    thread = threading.Thread(target=queuer, name="Minecraft Output Queue", daemon=True)
    thread.start()
    return thread


class TextOption(MDGridLayout):
    value = StringProperty()
    label = StringProperty()
    button_label = StringProperty()


class DropdownOption(MDGridLayout):
    value = StringProperty()
    label = StringProperty()
    options = ListProperty()


class FolderOption(TextOption):

    def button_press(self):
        new_dir = Utils.open_directory(title="Choose Server Directory")
        if new_dir:
            self.value = new_dir


class RecentItem(MDBoxLayout):
    name = StringProperty()
    path = StringProperty()
    client: MinecraftClient = ObjectProperty()

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        icon_delete = load_image("assets", "delete.png")
        icon_edit = load_image("assets", "edit.png")
        self.ids.delete_icon.texture = icon_delete.texture
        self.ids.rename_icon.texture = icon_edit.texture

    def load(self):
        options = get_options()
        save_path = os.path.join(options.server_directory, self.path, "save.apmc")
        if os.path.isfile(save_path):
            self.client.open_apmc(save_path)
        else:
            info_dialog(title="Error", content=f"Unable to find save file for world {self.path}")

    def delete(self):
        self.client.welcome_window.confirm_delete(target=self.path, title="Confirm Delete",
                                                  content=f"Delete {self.name}?\nThis Action is permanent.")

    def rename(self):
        edit_prompt(title="Confirm Edit", content=f"Rename {self.name}", default=self.name,
                    confirm=lambda text: self.set_name(text))

    def set_name(self, name):
        options = get_options()
        self.name = name
        try:
            with open(os.path.join(options.server_directory, self.path, "metadata.json"), "r+") as file:
                data = json.load(file)
                data["description"] = name
                file.seek(0)
                file.truncate()
                json.dump(data, file)
        except Exception as e:
            info_dialog(title="Error", content=f"Error renaming world: {e}")


class ConfirmDialog(Popup):
    text = StringProperty()
    confirm_text = StringProperty()
    cancel_text = StringProperty()


class InfoDialog(Popup):
    text = StringProperty()
    button_text = StringProperty()


class ProgressBarDialog(Popup):
    text = StringProperty("")
    progress_text = StringProperty("")
    progress = NumericProperty(0)
    max = NumericProperty(100)

    def __init__(self, max, **kwargs):
        super().__init__(**kwargs)
        self.max = max


# TODO: migrate to Steps
def confirm_prompt(confirm=None, title="Prompt", content="Are you sure?", cancel=None, confirm_text="Yes",
                   cancel_text="No"):
    popup = ConfirmDialog(title=title, text=content, confirm_text=confirm_text, cancel_text=cancel_text)
    popup.open()

    if cancel is not None:
        popup.ids.cancel.bind(on_press=cancel)

    if confirm is not None:
        popup.ids.confirm.bind(on_press=confirm)


def info_dialog(title="Prompt", content="Are you sure?", cancel=None):
    popup = InfoDialog(title=title, text=content, button_text="OK")
    popup.open()


def edit_prompt(confirm, title="Prompt", content="Are you sure?", cancel=None, default=""):
    popup = ConfirmDialog(title=title, text=content, confirm_text="Confirm", cancel_text="Cancel")
    popup.open()

    content: MDWidget = popup.ids.content

    textinput = TextInput(text=default,
                          size_hint=(1, None),
                          height=30,
                          multiline=False,
                          )
    content.add_widget(textinput)
    textinput.bind(on_text_validate=lambda _: confirm(textinput.text))
    textinput.bind(on_text_validate=popup.dismiss)

    if cancel is not None:
        popup.ids.cancel.bind(on_press=cancel)

    popup.ids.confirm.bind(on_press=lambda _: confirm(textinput.text))


class WindowManager(MDScreenManager):
    pass


class LogEntry(MDLabel):
    pass


class ServerWindow(MDScreen):

    def __init__(self, client, **kw):
        super().__init__(**kw)
        self.client = client
        self.log: ServerLog = self.ids.log
        self.status: MDLabel = self.ids.status
        self.cmd: TextInput = self.ids.cmd
        self.progress_popup: Optional[ProgressBarDialog] = None
        self.background_color = (.5, .1, .1, 1)

    def send_command(self, value):
        self.client.send_command(value)
        self.cmd.text = ""
        Clock.schedule_once(self.focus_cmd, 0)

    def focus_cmd(self, dv):
        self.cmd.focus = True

    def show_progress_bar_dialog(self, title, content, max):
        self.progress_popup = ProgressBarDialog(title=title, text=content, max=max)
        self.progress_popup.open()

    def update_progress(self, progress: float, content: str):
        if self.progress_popup is None:
            return
        self.progress_popup.progress = progress * 100.0
        self.progress_popup.progress_text = content

    def close_progress_bar_dialog(self):
        if self.progress_popup is not None:
            self.progress_popup.dismiss()
        self.progress_popup = None


class ServerLog(MDRecycleView):

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.data = []

    def on_log(self, record: str):
        self.data.append({"text": escape_markup(record)})
        self.clean_old()

    def on_message_markup(self, text):
        self.data.append({"text": text})
        self.clean_old()

    def clean_old(self):
        if len(self.data) > self.messages:
            self.data.pop(0)


class WelcomeWindow(MDScreen):
    version = StringProperty()

    def __init__(self, client: MinecraftClient, **kwargs):
        super().__init__(**kwargs)
        self.client = client
        self.apmc = None
        options = get_options()
        Window.minimum_width, Window.minimum_height = (400, 300)
        self.ids['path'].value = options.server_directory
        self.ids['max_memory'].value = options.max_heap_size
        self.ids['min_memory'].value = options.min_heap_size
        self.ids['release_option'].value = options.release_channel

    def do_delete(self, target):
        options = get_options()
        world_path = os.path.join(options.server_directory, target)
        if options.server_directory in world_path and os.path.isdir(world_path):
            shutil.rmtree(world_path)
            self.client.init()

    def confirm_delete(self, target, title="Confirm Delete", content="This Action is permanent."):
        confirm_prompt(title=title, content=content, confirm=lambda _: self.do_delete(target))

    def save_options(self):
        options = get_options()
        options.server_directory = self.ids.path.value
        options.max_heap_size = self.ids.max_memory.value
        options.min_heap_size = self.ids.min_memory.value
        options.release_channel = self.ids.release_option.value
        Utils.get_settings().save()
        self.client.init()

def launch_subprocess(*args):
    from worlds.LauncherComponents import launch
    launch(mc_launch, "Minecraft Client", args)

def mc_launch(*arguments):
    parser = argparse.ArgumentParser()
    parser.add_argument("apmc_file", default=None, nargs='?', help="Path to an Archipelago Minecraft data file (.apmc)")
    args = parser.parse_args(arguments)
    Config.set("network", "implementation", "requests")
    MinecraftClient(args).run()

if __name__ == "__main__":
    mc_launch(sys.argv)