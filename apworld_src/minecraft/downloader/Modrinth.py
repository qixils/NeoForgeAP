from .Utilities import DownloadStep, FetchStep, download_file, ua_header
from . import StepsStep, SyncStep
from typing import TypedDict
import os
import requests

class ModrinthFile(TypedDict):
    url: str
    primary: bool

class ModrinthVersion(TypedDict):
    id: str
    game_versions: list[str]
    files: list[ModrinthFile]

class DownloadMod(StepsStep):
    def __init__(self, destination_folder: str, platform: str, game_version: str, project: str, filename: str):
        self.destination_folder = destination_folder
        self.platform = platform
        self.game_version = game_version
        self.project = project
        self.filename = filename
        self.jar_path = os.path.join(destination_folder, filename)

        super().__init__(
            SyncStep(lambda: print(f"Downloading mod {project} for {platform} {game_version}")),
            FetchStep(f"https://api.modrinth.com/v2/project/{project}/version?loaders={platform}"),
            SyncStep(self._process_versions),
            DownloadStep(filepath=self.jar_path),
            SyncStep(lambda: self.version),
        )
    
    def _process_versions(self, versions: list[ModrinthVersion]) -> str:
        self.version = next((ver for ver in versions if self.game_version in ver["game_versions"]), None)
        if not self.version:
            raise Exception(f"No version found for {self.platform} {self.game_version}")

        return self.version["files"][0]["url"]

def download_mod(destination_folder: str, platform: str, game_version: str, project: str, filename: str) -> ModrinthVersion:
    print(f"Downloading mod {project} for {platform} {game_version}")

    versions = requests.get(
        f"https://api.modrinth.com/v2/project/{project}/version?loaders={platform}",
        headers=ua_header,
    ).json()
    
    version = next((ver for ver in versions if game_version in ver["game_versions"]), None)
    if not version:
        raise Exception(f"No version found for {platform} {game_version}")

    url = version["files"][0]["url"]
    jar_path = os.path.join(destination_folder, filename)
    download_file(jar_path, url, version=version["id"])

    return version
