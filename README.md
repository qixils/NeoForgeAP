# Minecraft Randomizer Setup Guide

NeoForge Rewrite by qixils (Lexi), coehlrich, MrRedstone54 (Red)

Original (Forge) Implementation by KonoTyran.

## Required Software

- [Minecraft Java Edition](https://www.minecraft.net/en-us/store/minecraft-java-edition)
- [Archipelago](https://github.com/ArchipelagoMW/Archipelago/releases)
- [minecraft.apworld](https://github.com/qixils/NeoForgeAP/releases)

## Configuring your YAML file

### What is a YAML file and why do I need one?

See the guide on setting up a basic YAML at the Archipelago setup
guide: [Basic Multiworld Setup Guide](https://archipelago.gg/tutorial/Archipelago/setup/en)

### Where do I get a YAML file?

You can generate an YAML file in the Archipelago Launcher. Simply open the Options Creator from the launcher,
and select Minecraft from the bar on the left. Once you have made your YAML file, you can click "Export Options" to
select where you want the YAML file to go.

## Installing the Minecraft APWorld

When you first download the APWorld, it will not be installed in the Archipelago Launcher. You must open the Archipelago
Launcher, and click "Install APWorld". From there, select the downloaded Minecraft APWorld, and it should install itself.
Restart the Archipelago Launcher, and it will appear under the Client Tab. It looks like a Minecraft book with gold text on the cover.

### Updating the Minecraft APWorld

Download the new APWorld, and reinstall it using the instructions above.

## Joining a MultiWorld Game

### Obtain Your Minecraft Data File

When you join a multiworld game, you will be asked to provide your YAML file to whoever is hosting. The host also needs
to install the Minecraft APWorld into their Archipelago Instance, or else it will not generate.
**Only one YAML file needs to be submitted to the generator per Minecraft world regardless of how many players play on it.**
You can have 10 people working together on the same world or just 1, but regardless you just need to submit one YAML file (per group).

If you are the host, you can learn about _Generating a single player game_ or _Generating a multiplayer game_
[here](https://archipelago.gg/tutorial/Archipelago/setup_en#generating-a-single-player-game).
Note that since the game is not core-verified, you will need to follow the instructions for
_On your local installation_, **not** _On the website_.
Once the seed is generated, you can continue to
[_Hosting an archipelago server_](https://archipelago.gg/tutorial/Archipelago/setup_en#hosting-an-archipelago-server).
You can host from your PC or on the website; the latter is recommended for multiplayer games and generally easier setup.

Once that is done, the host will provide you with either a link to download your data file,
or with a zip file containing everyone's data files. Your data file should have a `.apmc` extension.

### Running the Minecraft Server

_If you're using a server hosting service, please see [**Manual Installation**](#manual-installation) instead._

Open the Minecraft Client from the Archipelago Launcher, and click on "Open APMC File".
This will allow you to select the APMC file you downloaded. Once opened, it will start the server.
If this is your first time running the server, it is required to ask if you accept Minecraft's EULA,
then it will automatically download the required Java and NeoForge Versions.
Once the server is running **you must keep the console open**, or else the server will shut down.

If you close the server, or the server does shut down unexpectedly, you can simply reopen the Minecraft Client
and select your Minecraft World from the list to restart the server. You can also rename the world to identify it from
other Minecraft Archipelago Games you may be running.

When you are finished with the Minecraft World, you may delete it from the list.

### Connect to the MultiServer

Using Minecraft 26.1, connect to the server. The IP address is:
- `localhost` if you're hosting
- provided to you by the server hosting service, if using one
- your friend's public IP address, if they're port forwarding

Once you are in game, the mod should auto connect to your Archipelago room. If it doesn't,
type `/connect <AP-Address> (Port) (Password)` where `<AP-Address>` is the address of the
Archipelago server, i.e. `archipelago.gg`.
`(Port)` is only required if the Archipelago server is not using the default port of 38281.
`(Password)` is only required if the Archipelago server you are using has a password set.

### Play the game

When the console tells you that you have joined the room, you're all set. Congratulations on successfully joining a
multiworld game! At this point any additional Minecraft players may connect to your NeoForge server.
To start the game once everyone is ready, use the command `/start`.

## Manual Installation

This is an alternative to the "Running the Minecraft Server" step; all other instructions must still be followed.
Please note that support for manual installations may be limited.
Additionally, these steps do not allow you to play a Singleplayer World; singleplayer is not supported at this time.

1. Download and install the latest version of [Java](https://adoptium.net/temurin/releases)
2. Download the NeoForgeAP mod from the [releases](https://github.com/qixils/NeoForgeAP/releases)
   and note the Minecraft version listed at the end of the filename.
3. Download the matching NeoForge [installer](https://neoforged.net/) and follow the prompts to Install a Server.
    - On a remote hosting service, you would instead use their configuration panel to do this automatically for you.
    - For further information, including how to install from a command line, see the [NeoForge guide](https://docs.neoforged.net/user/docs/server/)
4. In the root of the generated server, create a `mods` folder add the downloaded mod to it.
    - On a remote hosting service, look for a "Files" or "FTP" tab to do this.
5. In the root of the generated server, create an `APData` folder and add your .apmc to it.
6. Start the server.
   Note on the first launch it will generate a eula.txt file you have to edit to approve then relaunch the server.
