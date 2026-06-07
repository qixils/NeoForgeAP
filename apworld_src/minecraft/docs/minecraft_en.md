# Minecraft Randomizer Setup Guide

## Required Software

- Minecraft Java Edition from
  the [Minecraft Java Edition Store Page](https://www.minecraft.net/en-us/store/minecraft-java-edition)
- Archipelago from the [Archipelago Releases Page](https://github.com/ArchipelagoMW/Archipelago/releases)

## Configuring your YAML file

### What is a YAML file and why do I need one?

See the guide on setting up a basic YAML at the Archipelago setup
guide: [Basic Multiworld Setup Guide](/tutorial/Archipelago/setup/en)

### Where do I get a YAML file?

You can customize your options by visiting the [Minecraft Player Options Page](/games/Minecraft/player-options)

## Joining a MultiWorld Game

### Obtain Your Minecraft Data File

**Only one YAML file needs to be submitted per Minecraft world regardless of how many players play on it.**
All players on the world will work on progression together.

When you join a multiworld game, you will be asked to provide your YAML file to whoever is hosting. Once that is done,
the host will provide you with either a link to download your data file, or with a zip file containing everyone's data
files. Your data file should have a `.apmc` extension.

Double-click on your `.apmc` file to have the Minecraft client auto-launch the installed NeoForge server. Make sure to
leave this window open as this is your server console.

### Connect to the MultiServer

Open Minecraft, go to `Multiplayer > Direct Connection`, and join the `localhost` server address.

If you are using the website to host the game then it should auto-connect to the AP server without the need to `/connect`.

Otherwise, once you are in-game, type `/connect <AP-Address> (Port) (Password)`, where `<AP-Address>` is the address of the
Archipelago server. `(Port)` is only required if the Archipelago server is not using the default port of 38281.
Note that there is no colon between `<AP-Address>` and `(Port)`.
`(Password)` is only required if the Archipelago server you are using has a password set.

_Example usage: `/connect archipelago.gg 38281 password123`_

### Play the game

When the console tells you that you have joined the room, you're all set. Congratulations on successfully joining a
multiworld game! At this point any additional minecraft players may connect to your NeoForge server. To start the game once
everyone is ready use the command `/start`.

## Non-Windows Installation

The Minecraft Client will install NeoForge and the Archipelago mod.
On supported operating systems, it will additionally download a local copy of the Temurin JRE.

For other operating systems, you will need to have a Java installation available on your system path to install and launch the server.
Head to [minecraft_versions.json]
to see which Java version is required.
When in doubt, the latest release of Java should almost always work on modern (1.17+) releases of Minecraft.

- Install the matching Java JRE
    - see [Manual Installation Software Links](#manual-installation-software-links)
    - or package manager provided by your OS / distribution
- For non-installed downloads, open your `host.yaml` and add the path to your Java below the `minecraft_options` key
    - `java: "path/to/jdk-XX.X.X+X-jre/bin/java"`
- Run the Minecraft Client and select your .apmc file

## Full Manual Installation

If you're crafty, the Archipelago client is fully optional.
The following steps may help you setup an Archipelago on a remotely hosted server.
Exact version numbers are omitted; refer to [minecraft_versions.json] for specifics.

- Download and install a compatible version of Java
- Download and install a local NeoForge server
- Download the NeoForgeAP mod and copy it to your `mods` folder
- Download your randomized .apmc file and copy it to a new `APData` folder

### Manual Installation Software Links

- [Minecraft NeoForge Download Page](https://neoforged.net/)
- [Minecraft Archipelago Randomizer Mod Releases Page](https://github.com/qixils/NeoForgeAP/releases)
    - Please note that the mod runs only on the server side and is not intended to be installed to your Minecraft client.
- [Java JRE (Temurin)](https://adoptium.net/temurin/releases?arch=x64&package=jre)
    - Download the "JRE" package for your operating system


<!-- common markdown links -->

[minecraft_version.json](https://raw.githubusercontent.com/qixils/NeoForgeAP/main/versions/minecraft_versions.json)
