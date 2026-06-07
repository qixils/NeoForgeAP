import logging
import shutil
import tempfile

from . import StepsStep, SyncStep
from .Utilities import DownloadStep, FetchStep, jre_paths
from Utils import is_windows, is_linux
import os
import zipfile
import platform
import tarfile
from typing import TypedDict, Any


class Download(TypedDict):
    checksum: str
    checksum_link: str
    download_count: int
    link: str
    metadata_link: str
    name: str
    signature_link: str
    size: int

class Binary(TypedDict):
    architecture: str
    download_count: int
    heap_size: str
    image_type: str
    installer: Download
    jvm_impl: str
    os: str
    package: Download
    project: str
    scm_ref: str
    updated_at: str

class Version(TypedDict):
    build: int
    major: int
    minor: int
    openjdk_version: str
    optional: str
    security: int
    semver: str

class Asset(TypedDict):
    binary: Binary
    release_link: str
    release_name: str
    vendor: str
    version: Version

class DownloadJava(StepsStep):
    def __init__(self, to: str, version: int):
        self.to = to
        self.version = version
        self.outpath = os.path.join(to, "java", jre_paths[self.version])
        self.zip_path = os.path.join(self.outpath, "jre.zip")
        self.logger = logging.getLogger("MinecraftClient")
        super().__init__(
            f"Downloading Java {version}...",
            SyncStep(self._get_api_url),
            FetchStep(),
            SyncStep(self._process_assets),
            DownloadStep(filepath=self.zip_path),
            SyncStep(self._process_extract),
        )

    def run(self, context, *args, **kwargs):
        context['java_dir'] = self.outpath
        super().run(context, *args, **kwargs)

    def _get_api_url(self, context: dict[str, Any]) -> str:
        self.logger.info(f"Fetching Java {self.version} versions")

        system = "windows" if is_windows else "linux" if is_linux else None
        if not system:
            raise Exception("Unsupported operating system for Java download")
        
        arch = "aarch64" if platform.machine() in ["aarch64", "arm64"] else "x64"

        return f"https://api.adoptium.net/v3/assets/latest/{self.version}/hotspot?architecture={arch}&image_type=jre&os={system}&vendor=eclipse"
    
    def _process_assets(self, context: dict[str, Any], assets: list[Asset]) -> tuple:
        data: Asset = assets[0]
        os.makedirs(self.outpath, exist_ok=True)
        release_path = os.path.join(self.outpath, "release")
        semver = None

        if os.path.exists(release_path):
            with open(release_path, 'r') as file:
                info = file.read()
                semver = info.split('SEMANTIC_VERSION="')[1].split('"')[0]
        self.logger.info(f"Received semver: {data['version']['semver']} local ver: {semver}")
        if data["version"]["semver"] == semver:
            self.logger.info("Already up-to-date, skipping download")
            return False

        self.logger.info(f"Downloading Java {data['version']['semver']}")
        context['java_semver'] = data['version']['semver']
        return data["binary"]["package"]["link"], None, data['version']['semver']
    
    def _process_extract(self,context: dict[str, Any], res):
        if not res:
            # download is skipped
            return

        self.logger.info(f"Extracting Java {self.version}")
        # TODO: this probably also needs a mac flow, for .pkg files
        if is_linux:
            with tempfile.TemporaryDirectory() as temp_dir:
                with tarfile.open(self.zip_path, 'r') as tarball:
                    tarball.extractall(temp_dir, filter='tar')
                    items = os.listdir(temp_dir)
                    if len(items) == 1 and os.path.isdir(os.path.join(temp_dir, items[0])):
                        original_dir = os.path.join(temp_dir, items[0])
                    else:
                        original_dir = temp_dir
                shutil.copytree(original_dir, self.outpath, dirs_exist_ok=True)
        else:
            with zipfile.ZipFile(self.zip_path, 'r') as zip_ref:
                subfolder = zip_ref.namelist()[0]
                for entry in zip_ref.infolist()[1:]:
                    if entry.is_dir():
                        continue
                    relative = entry.filename[len(subfolder):]
                    filepath = os.path.join(self.outpath, *relative.split("/"))
                    dirpath = os.path.dirname(filepath)
                    os.makedirs(dirpath, exist_ok=True)
                    with open(filepath, 'wb') as file:
                        file.write(zip_ref.read(entry.filename))

        os.remove(self.zip_path)

def get_java_path(to: str, version: int) -> str:
    jre = jre_paths[version]

    bin = "javaw.exe" if is_windows else "java" if is_linux else None
    if not bin:
        raise Exception("Unsupported operating system for Java path retrieval")

    java_path = os.path.join(to, "java", jre, "bin", bin)

    if not os.path.exists(java_path):
        raise Exception(f"Java {version} not found at {java_path}")
    
    return java_path
