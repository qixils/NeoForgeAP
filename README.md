# Minecraft Neoforge Randomizer Setup Guide
Original (Forge) Implementation by KonoTyran. 

NeoForge Rewrite by qixils (Lexi), coehlrich.
## THIS PROJECT IS IN ALPHA. DO NOT USE AT THIS TIME

Todo:
- Refactor APRandomizer functions
- Implement new APJavaClient library

## Required Software

- Minecraft Java Edition from
  the [Minecraft Java Edition Store Page](https://www.minecraft.net/en-us/store/minecraft-java-edition)
- Archipelago from the [Archipelago Releases Page](https://github.com/ArchipelagoMW/Archipelago/releases)

## Configuring your YAML file

### What is a YAML file and why do I need one?

See the guide on setting up a basic YAML at the Archipelago setup
guide: [Basic Multiworld Setup Guide](https://archipelago.gg/tutorial/Archipelago/setup/en)

### Where do I get a YAML file?

You can customize your settings by visiting the [Minecraft Player Settings Page](https://archipelago.gg/games/Minecraft/player-options)

## Joining a MultiWorld Game

### Obtain Your Minecraft Data File

**Only one yaml file needs to be submitted per minecraft world regardless of how many players play on it.**

When you join a multiworld game, you will be asked to provide your YAML file to whoever is hosting. Once that is done,
the host will provide you with either a link to download your data file, or with a zip file containing everyone's data
files. Your data file should have a `.apmc` extension.

Double-click on your `.apmc` file to have the Minecraft client auto-launch the installed NeoForge server. Make sure to
leave this window open as this is your server console.

### Connect to the MultiServer

Using minecraft 1.19 connect to the server `localhost`.

Once you are in game type `/connect <AP-Address> (Port) (Password)` where `<AP-Address>` is the address of the
Archipelago server. `(Port)` is only required if the Archipelago server is not using the default port of

38281. `(Password)` is only required if the Archipelago server you are using has a password set.

### Play the game

When the console tells you that you have joined the room, you're all set. Congratulations on successfully joining a
multiworld game! At this point any additional minecraft players may connect to your NeoForge server. To start the game once
everyone is ready use the command `/start`.

## Manual Installation

It is highly recommended to ues the Archipelago installer to handle the installation of the NeoForge server for you.
support will not be given for those wishing to manually install NeoForge. For those of you who know how, and wish to do so,
the following links are the versions of the software we use.

### Manual install Software links

- [Minecraft NeoForge Download Page](https://projects.neoforged.net/neoforged/neoforge)
- [Minecraft Archipelago Randomizer Mod Releases Page](https://github.com/qixils/NeoForgeAP/releases)
   - **DO NOT INSTALL THIS ON YOUR CLIENT**
- [Java 21 Download Page](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)

