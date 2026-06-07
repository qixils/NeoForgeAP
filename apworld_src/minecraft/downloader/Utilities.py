import logging
import os
import subprocess
import threading

import certifi
import requests
from typing import Any, Callable, Optional
from . import Step
from kivy.network.urlrequest import UrlRequest, UrlRequestRequests

ua = "qixils/minecraft-crowdcontrol/1.0.0"
ua_header = {"User-Agent": ua}

jre_paths: dict[int, str] = {
    8: "jdk-8",
    21: "jdk-21",
}

# TODO: headers
# TODO: redirects

class DownloadStep(Step):
    def __init__(self, url: str | None = None, filepath: str | None = None):
        super().__init__()
        self.filepath = filepath
        self.url = url
        self.logger = logging.getLogger("MinecraftClient")
    
    def run(self,
            context: dict[str, Any],
            *args,
            on_success: Callable | None = None,
            on_failure: Callable | None = None,
            on_progress: Callable[[float, str], None] | None = None,
            error_ok: bool = False):
        url = self.url
        filepath = self.filepath
        version = None
        self.logger.info(f"Got arguments {args}")
        if len(args) > 0:
            url = args[0] or url
        if len(args) > 1:
            filepath = args[1] or filepath
        if len(args) > 2:
            version = args[2] or version

        if callable(url):
            url = url()
        if callable(filepath):
            filepath = filepath()
        if callable(version):
            version = version()

        self.filepath = filepath
        # If we were passed a blank URL then we assume this skip should be skipped for already being downloaded
        if not url or not filepath:
            if on_success is not None:
                on_success(False)
            return

        # Optionally check if a file with a matching version is already downloaded
        version_path = filepath + ".version"
        if version is not None:
            if os.path.exists(version_path):
                with open(version_path, 'r') as f:
                    if f.read().strip() == version:
                        on_success(False)
                        return
        self.logger.info(f"Sending request to {url}; downloading to {filepath}")
        # Using requests over urllib, cause redirects
        UrlRequestRequests(url,
                   file_path=filepath,
                   on_progress=lambda req, current_size, total_size: on_progress is not None and on_progress(current_size / total_size if total_size > 0 else 0, f"Downloading {os.path.basename(filepath)}..."),
                   on_success=lambda req, res: self._on_success(res, version=version, on_success=on_success),
                   on_error=on_failure,
                   on_redirect= lambda req, res: self.logger.info(f"{req}, {res}, {req.resp_status} {req.resp_headers}"),
                   chunk_size=1024000,
                   # ca_file=certifi.where()
                   )

    def _on_success(self, res, version: str | None = None, on_success: Callable | None = None):
        with open(self.filepath + ".version", 'w') as f:
            f.write(version)
        if on_success is not None:
            on_success(res or True)

class FetchStep(Step):
    def __init__(self, url: str | None = None):
        super().__init__()
        self.url = url
        self.logger = logging.getLogger("MinecraftClient")

    def run(self,
            context: dict[str, Any],
            *previous: Any,
            on_success: Callable | None = None,
            on_failure: Callable | None = None,
            on_progress: Callable[[float, str], None] | None = None,
            error_ok: bool = False):
        if previous:
            url = previous[0] if previous[0] is not None and type(previous[0]) is str else self.url
        self.logger.info(f"Requesting to url: {url}")
        payload_lambda = lambda req, resp: on_success(resp)
        UrlRequest(url,
                   on_progress=lambda req, current_size, total_size: on_progress is not None and on_progress(current_size / total_size, "Loading data..."),
                   on_success=payload_lambda,
                   on_error=on_failure,
                   ca_file=certifi.where())

class SubprocessStep(Step):
    def __init__(self, name: str, *args):
        super().__init__()
        self.args = args
        self.logger = logging.getLogger("MinecraftClient")
        self.name = name
    
    def run(self,
            context: dict[str, Any],
            *previous,
            on_success: Callable | None = None,
            on_failure: Callable | None = None,
            on_progress: Callable | None = None,
            error_ok: bool = False):
        args = previous if len(previous) > 0 else self.args

        if args is None or len(args) == 0 or not args[0]:
            on_success(False)
            return
        self.logger.info(f"Arguments: {args}")
        thread = threading.Thread(target=self._run_in_thread, args=(on_success, args))
        thread.start()
    
    @staticmethod
    def _run_in_thread(on_exit: Callable, popen_args: tuple):
        logger = logging.getLogger("MinecraftClient")
        kwargs = dict()
        logger.info(f"Arguments: {popen_args}")
        if type(popen_args[-1]) is dict:
            kwargs = popen_args[-1]
            popen_args = popen_args[:-1]
        logger.info(f"Arguments: {popen_args} {kwargs}")
        proc = subprocess.Popen(*popen_args, **kwargs)
        proc.wait()
        on_exit(True)
        

def is_semver_ge(new_semver: str, old_semver: str) -> bool:
    new_parts = new_semver.split(".")
    old_parts = old_semver.split(".")
    parts = max(len(new_parts), len(old_parts))
    
    for i in range(parts):
        new_part = int(new_parts[i]) if i < len(new_parts) else 0
        old_part = int(old_parts[i]) if i < len(old_parts) else 0
        
        if new_part > old_part:
            return True
        elif new_part < old_part:
            return False
            
    return True

def semver_sort(a: str, b: str) -> int:
    if a == b:
        return 0
    if is_semver_ge(a, b):
        return -1
    if is_semver_ge(b, a):
        return 1
    return 0  # fallback

def download_file(path: str, url: str, version: Optional[str] = None) -> None:
    # Optionally check if a file with a matching version is already downloaded
    version_path = path + ".version"
    if version is not None:
        if os.path.exists(version_path):
            with open(version_path, 'r') as f:
                if f.read().strip() == version:
                    return

    response = requests.get(url, stream=True)
    if response.status_code != 200:
        raise Exception(f"Failure while retrieving remote data (source: {url})")

    with open(path, 'wb') as file:
        for chunk in response.iter_content(chunk_size=8192):
            file.write(chunk)
    
    if version is not None:
        with open(version_path, 'w') as f:
            f.write(version)

