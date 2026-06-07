import os
import json
import settings
import typing
from base64 import b64encode, b64decode
from typing import Dict, Any

from BaseClasses import Region, Entrance, Item, Tutorial, ItemClassification, Location
from worlds.AutoWorld import World, WebWorld
from worlds.LauncherComponents import Component, components, Type, SuffixIdentifier

from . import Constants
from .Container import MinecraftContainer
from .Options import MinecraftOptions
from .Structures import shuffle_structures
from .ItemPool import build_item_pool, get_junk_item_names
from .Rules import set_rules
from ..LauncherComponents import icon_paths

client_version = 10

icon_paths['mcicon'] = f"ap:{__name__}/assets/mcicon.png"

# register client
def launch_client(*args):
    from .MinecraftClient import launch_subprocess
    launch_subprocess(*args)

components.append(
    Component(
        "Minecraft Client",
        icon="mcicon",
        func=launch_client,
        component_type=Type.CLIENT,
        file_identifier=SuffixIdentifier('.apmc'),
    )
)


class MinecraftSettings(settings.Group):
    class ForgeDirectory(settings.OptionalUserFolderPath):
        pass

    class ReleaseChannel(str):
        """
        release channel, currently "release", or "beta"
        any games played on the "beta" channel have a high likelihood of no longer working on the "release" channel.
        """

    class JavaExecutable(settings.OptionalUserFilePath):
        """
        Path to Java executable. If not set, will attempt to fall back to Java system installation.
        """

    class ServerDirectory(settings.OptionalUserFolderPath):
        """
        Path to local directory to install Java, Neo Forge, etc.
        """
        @classmethod
        def validate(cls, path: str):
            if os.path.exists(path) and not os.path.isdir(path):
                raise ValueError(f"'{path}' must be a folder")

    server_directory: ServerDirectory = ServerDirectory("Minecraft AP Server Directory")
    max_heap_size: str = "2G"
    min_heap_size: str = "1G"
    release_channel: ReleaseChannel = ReleaseChannel("release")
    java: JavaExecutable = JavaExecutable("")


class MinecraftWebWorld(WebWorld):
    theme = "jungle"
    bug_report_page = "https://github.com/qixils/NeoForgeAP/issues/new?assignees=&labels=bug&template=bug_report.yaml&title=%5BBug%5D%3A+Brief+Description+of+bug+here"

    setup = Tutorial(
        "Multiworld Setup Guide",
        "A guide to setting up the Archipelago Minecraft software on your computer. This guide covers"
        "single-player, multiworld, and related software.",
        "English",
        "minecraft_en.md",
        "minecraft/en",
        ["qixils"]
    )

    setup_es = Tutorial(
        setup.tutorial_name,
        setup.description,
        "Español",
        "minecraft_es.md",
        "minecraft/es",
        ["Edos"]
    )

    setup_sv = Tutorial(
        setup.tutorial_name,
        setup.description,
        "Swedish",
        "minecraft_sv.md",
        "minecraft/sv",
        ["Albinum"]
    )

    setup_fr = Tutorial(
        setup.tutorial_name,
        setup.description,
        "Français",
        "minecraft_fr.md",
        "minecraft/fr",
        ["TheLynk"]
    )

    tutorials = [setup, setup_es, setup_sv, setup_fr]


class MinecraftWorld(World):
    """
    Minecraft is a game about creativity. In a world made entirely of cubes, you explore, discover, mine,
    craft, and try not to explode. Delve deep into the earth and discover abandoned mines, ancient
    structures, and materials to create a portal to another world. Defeat the Ender Dragon, and claim
    victory!
    """
    game = "Minecraft"
    options_dataclass = MinecraftOptions
    options: MinecraftOptions
    settings: typing.ClassVar[MinecraftSettings]
    topology_present = True
    web = MinecraftWebWorld()

    item_name_to_id = Constants.item_name_to_id
    location_name_to_id = Constants.location_name_to_id

    using_ut: bool
    passthrough: dict[str, Any]
    ut_can_gen_without_yaml = True

    def _get_mc_data(self) -> Dict[str, Any]:
        exits = [connection[0] for connection in Constants.region_info["default_connections"]]
        return {
            # Mod data
            'world_seed': self.random.getrandbits(32),
            'seed_name': self.multiworld.seed_name,
            'player_name': self.player_name,
            'player_id': self.player,
            'client_version': client_version,
            'structures': {exit: self.multiworld.get_entrance(exit, self.player).connected_region.name for exit in exits},
            'advancement_goal': self.options.advancement_goal.value,
            'egg_shards_required': min(self.options.egg_shards_required.value,
                                       self.options.egg_shards_available.value),
            'egg_shards_available': self.options.egg_shards_available.value,
            'required_bosses': self.options.required_bosses.current_key,
            'MC35': bool(self.options.send_defeated_mobs.value),
            'death_link': bool(self.options.death_link.value),
            'unlockable_hearts': bool(self.options.unlockable_hearts.value),
            'starting_items': json.dumps(self.options.starting_items.value),
            'race': self.multiworld.is_race,

            # Universal Tracker data
            'bosses_to_defeat': self.options.required_bosses.value,
            'shuffle_structures': self.options.shuffle_structures.value,
            'structure_compasses': self.options.structure_compasses.value,
            'combat_difficulty': self.options.combat_difficulty.value,
            'include_hard_advancements': self.options.include_hard_advancements.value,
            'include_unreasonable_advancements': self.options.include_unreasonable_advancements.value,
            'include_postgame_advancements': self.options.include_postgame_advancements.value,
        }

    def generate_early(self: "MinecraftWorld") -> None:
        re_gen_passthrough = getattr(self.multiworld, "re_gen_passthrough", {})
        if re_gen_passthrough and self.game in re_gen_passthrough:
            self.using_ut = True
            self.passthrough = re_gen_passthrough["Minecraft"]
            self.options.advancement_goal.value = self.passthrough["advancement_goal"]
            self.options.egg_shards_required.value = self.passthrough["egg_shards_required"]
            self.options.egg_shards_available.value = self.passthrough["egg_shards_available"]
            self.options.required_bosses.value = self.passthrough["bosses_to_defeat"]
            self.options.shuffle_structures.value = self.passthrough["shuffle_structures"]
            self.options.structure_compasses.value = self.passthrough["structure_compasses"]
            self.options.combat_difficulty.value = self.passthrough["combat_difficulty"]
            self.options.include_hard_advancements.value = self.passthrough["include_hard_advancements"]
            self.options.include_unreasonable_advancements.value = self.passthrough["include_unreasonable_advancements"]
            self.options.include_postgame_advancements.value = self.passthrough["include_postgame_advancements"]
            self.options.unlockable_hearts.value = self.passthrough.get("unlockable_hearts", 0)
            self.options.death_link.value = self.passthrough["death_link"]
        else:
            self.using_ut = False

    def create_item(self, name: str) -> Item:
        item_class = ItemClassification.filler
        if name in Constants.item_info["progression_items"]:
            item_class |= ItemClassification.progression
        if name in Constants.item_info["useful_items"]:
            item_class |= ItemClassification.useful
        if name in Constants.item_info["trap_items"]:
            item_class |= ItemClassification.trap

        return MinecraftItem(name, item_class, self.item_name_to_id.get(name, None), self.player)

    def create_event(self, region_name: str, event_name: str) -> None:
        region = self.multiworld.get_region(region_name, self.player)
        loc = MinecraftLocation(self.player, event_name, None, region)
        loc.place_locked_item(self.create_event_item(event_name))
        region.locations.append(loc)

    def create_event_item(self, name: str) -> Item:
        item = self.create_item(name)
        item.classification = ItemClassification.progression
        return item

    def create_regions(self) -> None:
        # Create regions
        for region_name, exits in Constants.region_info["regions"]:
            r = Region(region_name, self.player, self.multiworld)
            for exit_name in exits:
                r.exits.append(Entrance(self.player, exit_name, r))
            self.multiworld.regions.append(r)

        # Bind mandatory connections
        for entr_name, region_name in Constants.region_info["mandatory_connections"]:
            e = self.multiworld.get_entrance(entr_name, self.player)
            r = self.multiworld.get_region(region_name, self.player)
            e.connect(r)

        # Add locations
        for region_name, locations in Constants.location_info["locations_by_region"].items():
            region = self.multiworld.get_region(region_name, self.player)
            for loc_name in locations:
                loc = MinecraftLocation(self.player, loc_name,
                    self.location_name_to_id.get(loc_name, None), region)
                region.locations.append(loc)

        # Add events
        self.create_event("Nether Fortress", "Blaze Rods")
        self.create_event("The End", "Ender Dragon")
        self.create_event("Nether Fortress", "Wither")

        # Shuffle the connections
        shuffle_structures(self)

    def create_items(self) -> None:
        self.multiworld.itempool += build_item_pool(self)

    set_rules = set_rules

    def generate_output(self, output_directory: str) -> None:
        data = self._get_mc_data()
        filename = self.multiworld.get_out_file_name_base(self.player) + MinecraftContainer.patch_file_ending

        container = MinecraftContainer(data,
                                       filename,
                                       os.path.join(output_directory, filename),
                                       self.player,
                                       self.multiworld.get_file_safe_player_name(self.player),
                                       )
        container.write()

    def fill_slot_data(self) -> dict:
        return self._get_mc_data()

    def get_filler_item_name(self) -> str:
        return get_junk_item_names(self.random, 1)[0]

    # For UT
    @staticmethod
    def interpret_slot_data(slot_data: dict[str, Any]) -> dict[str, Any]:
        return slot_data


class MinecraftLocation(Location):
    game = "Minecraft"

class MinecraftItem(Item):
    game = "Minecraft"


def mc_update_output(raw_data, server, port):
    data = json.loads(b64decode(raw_data))
    data['server'] = server
    data['port'] = port
    return b64encode(bytes(json.dumps(data), 'utf-8'))
