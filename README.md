# Minecraft Randomizer Setup Guide

NeoForge Rewrite by qixils (Lexi), coehlrich, MrRedstone54 (Red)

Original (Forge) Implementation by KonoTyran.

Todo:
- 1.21.9 -> 1.21.11/26.1 Advancements and Items
- Gifting API Support

## Required Software

- Minecraft Java Edition from
  the [Minecraft Java Edition Store Page](https://www.minecraft.net/en-us/store/minecraft-java-edition)
- Archipelago from the [Archipelago Releases Page](https://github.com/ArchipelagoMW/Archipelago/releases)
- [Minecraft APWorld File.](https://github.com/qixils/NeoForgeAP/releases)

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

**Only one YAML file needs to be submitted per Minecraft world regardless of how many players play on it.**

When you join a multiworld game, you will be asked to provide your YAML file to whoever is hosting. The host also needs
to install the Minecraft APWorld into their Archipelago Instance, or else it will not generate.  Once that is done,
the host will provide you with either a link to download your data file, or with a zip file containing everyone's data
files. Your data file should have a `.apmc` extension.

### Running the Minecraft Server

Open the Minecraft Client, and click on "Open APMC File". This will allow you to select the APMC file from your directory.
Once opened, it will start the server. If this is your first time running the server, it will ask for EULA agreements,
as well as download the required Java and NeoForge Versions. Once the server is running **you must keep the console open**,
or else the server will shut down.

If you close the server, or the server does shut down unexpectedly, you can simply reopen the Minecraft Client
and select your Minecraft World from the list to restart the server. You can also rename the world to identify it from
other Minecraft Archipelago Games you may be running.

When you are finished with the Minecraft World, you may delete it from the list.

### Connect to the MultiServer

Using Minecraft 1.21.8, connect to the server `localhost` in direct connections.

Once you are in game type `/connect <AP-Address> (Port) (Password)` where `<AP-Address>` is the address of the
Archipelago server. `(Port)` is only required if the Archipelago server is not using the default port of
38281. `(Password)` is only required if the Archipelago server you are using has a password set.

### Play the game

When the console tells you that you have joined the room, you're all set. Congratulations on successfully joining a
multiworld game! At this point any additional Minecraft players may connect to your NeoForge server. To start the game once
everyone is ready use the command `/start`.

## Manual Installation

It is highly recommended to use the Archipelago installer to handle the installation of the NeoForge server for you.
Support will not be given for those wishing to manually install NeoForge. For those of you who know how, and wish to do so,
the following links are the versions of the software we use.

### Manual install Software links

- [Minecraft NeoForge Download Page](https://projects.neoforged.net/neoforged/neoforge)
- [Minecraft Archipelago Randomizer Mod Releases Page](https://github.com/qixils/NeoForgeAP/releases)
   - **DO NOT INSTALL THIS ON YOUR CLIENT**
- [Java 21 Download Page](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)

