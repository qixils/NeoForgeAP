import logging

from .Utilities import DownloadStep, FetchStep, SubprocessStep
from .Java import get_java_path
from . import ServerInstallData, StepsStep, SyncStep, Step
from typing import TypedDict, Any, Callable
import Utils
import os
import subprocess

class NeoVersions(TypedDict):
    isSnapshot: bool
    versions: list[str]

class DownloadNeoForge(StepsStep):
    def __init__(self, to: str,
                 eula_popup: Callable,
                 force_version: str | None = None,
                 heap: str = "2048M"):
        self.name = f"Downloading {force_version if force_version else 'latest'} NeoForge..."

        self.to = to
        self.force_version = (force_version.split(".", 1)[1] + ".") if force_version else None
        self.heap = heap
        self.installer_jar = os.path.join(to, f"neoforge-installer.jar")
        self.logger = logging.getLogger("MinecraftClient")

        super().__init__(
            "Installing Neo Forge",
            SyncStep(lambda *args: print(f"Downloading NeoForge index")),
            FetchStep("https://maven.neoforged.net/api/maven/versions/releases/net/neoforged/neoforge"),
            SyncStep(self._process_versions),
            ConfirmEula(eula_popup),
            DownloadStep(filepath=self.installer_jar),
            SyncStep(self._process_server),
            SubprocessStep("Installing NeoForge"),
            SyncStep(self._process_cleanup),
        )
    
    def _process_versions(self,context: dict[str, Any], neo_versions: NeoVersions) -> str:
        self.neo_latest = next(
            (ver for ver in reversed(neo_versions["versions"]) if self.force_version is None or ver.startswith(self.force_version)),
            None
        )
        
        if not self.neo_latest:
            raise Exception("No suitable NeoForge version found")

        self.minecraft = f"1.{'.'.join(self.neo_latest.split('.', 2)[:2])}"

        self.root = os.path.join(self.to, f"NeoForge {self.minecraft}")
        os.makedirs(self.root, exist_ok=True)

        self.java_path = get_java_path(self.to, 21)
        self.java_path_relative = os.path.relpath(self.java_path, self.root)
        self.logger.info(f"Using Java at {self.java_path}")

        self.all_run = [self.java_path, f"-Xmx{self.heap}", f"-Xms{self.heap}", "@user_jvm_args.txt"]
        self.windows_run = self.all_run + [f"@libraries/net/neoforged/neoforge/{self.neo_latest}/win_args.txt"]
        self.unix_run = self.all_run + [f"@libraries/net/neoforged/neoforge/{self.neo_latest}/unix_args.txt"]
        self.system_run = self.windows_run if Utils.is_windows else (self.unix_run if Utils.is_linux else None)

        self.mods = os.path.join(self.root, "mods")
        os.makedirs(self.mods, exist_ok=True)

        self.version_path = os.path.join(self.root, "neo_version")
        context['neoforge_dir'] = self.root
        context['neoforge_mod_dir'] = self.mods
        context['neoforge_run_args'] = self.system_run

        if os.path.exists(self.version_path):
            with open(self.version_path, 'r') as f:
                if f.read().strip() == self.neo_latest:
                    self.logger.info(f"NeoForge {self.neo_latest} is already installed, skipping download")
                    return False

        self.logger.info(f"Downloading NeoForge {self.neo_latest} installer")
        return f"https://maven.neoforged.net/releases/net/neoforged/neoforge/{self.neo_latest}/neoforge-{self.neo_latest}-installer.jar", None, self.neo_latest

    def _process_server(self, context: dict[str, Any], req):
        if req is False:
            # install is skipped
            return
        
        self.logger.info(f"Running NeoForge {self.neo_latest} installer")
        java_path = get_java_path(self.to, 21)
        return (java_path, "-jar", self.installer_jar, "--installServer"), {"cwd": self.root, "stderr": subprocess.DEVNULL, "stdout": subprocess.DEVNULL}

    def _process_cleanup(self, context: dict[str, Any], req):
        bat_file = os.path.join(self.root, "run.bat")
        sh_file = os.path.join(self.root, "run.sh")
        context['neoforge_run'] = bat_file if Utils.is_windows else (sh_file if Utils.is_linux else None)

        if req is False:
            # install is skipped
            return ServerInstallData(root_dir=self.root, mods_dir=self.mods, run_args=self.system_run)
        
        os.unlink(self.installer_jar)
        os.unlink(self.installer_jar + '.version')

        # we don't actually use these ourselves but they're nice to have
        # (although they are not very well tested)
        with open(bat_file, 'w') as f:
            windows_run = list(self.windows_run)
            windows_run[0] = f'&"{windows_run[0]}"'
            f.write(f"@echo off\nstart {' '.join(windows_run)} %* > log.txt 2> errorlog.txt")
        
        with open(sh_file, 'w') as f:
            unix_run = list(self.unix_run)
            unix_run[0] = unix_run[0].replace(' ', '\\ ')
            f.write(f"#!/bin/bash\nexec {' '.join(self.unix_run)} \"$@\" > log.txt 2> errorlog.txt")

        with open(self.version_path, 'w') as f:
            f.write(self.neo_latest)

        return ServerInstallData(root_dir=self.root, mods_dir=self.mods, run_args=self.system_run)

class ConfirmEula(Step):

    def __init__(self, confirm_prompt: Callable):
        super().__init__()
        self.logger = logging.Logger("MinecraftClient")
        self.confirm_prompt = confirm_prompt
        self.outdir: str | None = None

    def run(self,
            context: dict[str, Any],
            *previous: Any,
            on_success: Callable | None = None,
            on_failure: Callable | None = None,
            on_progress: Callable | None = None,
            error_ok: bool = False):

        if not previous or not previous[0]:
            on_success(False)
            return

        if 'neoforge_dir' not in context:
            on_success(False)
            return

        self.outdir = context['neoforge_dir']
        file = os.path.join(self.outdir, "eula.txt")
        if os.path.exists(file):
            with open(file, 'r') as f:
                if 'eula=true' in f.read():
                    on_success(*previous)
                    return

        success_fn = lambda *args: self.confirmed(previous, on_success)
        self.confirm_prompt(
            confirm=success_fn,
            title="Minecraft Eula",
            content="""
            Please note that by running a Minecraft server, 
            you are indicating your agreement to Minecraft's 
            EULA (https://aka.ms/MinecraftEULA).""",
            cancel=on_failure,
        )

    def confirmed(self, previous, on_success: Callable | None = None):
        self.logger.info(f"confirmed {previous}")
        contents = "eula=true"
        with open(os.path.join(self.outdir, 'eula.txt'), 'w') as f:
            f.write(contents)
        on_success(*previous)
