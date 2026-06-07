import json
import zipfile
from base64 import b64encode
from typing import Any, Optional

from worlds.Files import APPlayerContainer


class MinecraftContainer(APPlayerContainer):
    """
    Generates the apmc file
    """
    game = "Minecraft"
    patch_file_ending = ".apmc"

    def __init__(self,
                 patch_data: dict[str, Any],
                 patch_name: str,
                 path: Optional[str] = None,
                 player: Optional[int] = None,
                 player_name: str = "",
                 server: str = ""):
        super().__init__(path, player, player_name, server)
        self.patch_data = patch_data
        self.patch_name = patch_name

    def write_contents(self, opened_zipfile: zipfile.ZipFile) -> None:
        super().write_contents(opened_zipfile)
        filename = f"{self.patch_name}_json.apmcmeta"
        opened_zipfile.writestr(filename,json.dumps(self.patch_data))

