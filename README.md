Werewolf
======

[![Build Status](https://travis-ci.com/DoggyCraftDK/Werewolf.svg?branch=master)](https://travis-ci.com/DoggyCraftDK/Werewolf)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=DogOnFire_Werewolf&metric=alert_status)](https://sonarcloud.io/dashboard?id=DogOnFire_Werewolf)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=DogOnFire_Werewolf&metric=ncloc)](https://sonarcloud.io/dashboard?id=DogOnFire_Werewolf)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=DogOnFire_Werewolf&metric=bugs)](https://sonarcloud.io/dashboard?id=DogOnFire_Werewolf)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8296799b90684dbe8745823d38e26bf0)](https://www.codacy.com/app/Fido2603/Werewolf?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DoggyCraftDK/Werewolf&amp;utm_campaign=Badge_Grade)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=DogOnFire_Werewolf&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=DogOnFire_Werewolf)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=DogOnFire_Werewolf&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=DogOnFire_Werewolf)

![Werewolf](https://raw.githubusercontent.com/DoggyCraftDK/Werewolf/master/img/Werewolf.jpg)

The plugin for adding Werewolves into Minecraft!

Background
---------
To become a Werewolf, you have to get bitten (attacked) by a wolf or Werewolf at night and contract the Werewolf infection. This infection will turn the player into a Werewolf every night, starting the next night, if the player is under a open sky. To prevent turning, the player can stay inside during night.

*	When in wolf-form, character skin changes to a Werewolf skin
*	When in wolf-form, players will become identifiable only as their werewolf name in the playerlist and chat (Adding a element of mystery)
*	When in wolf-form, werewolves speak in a language which appears as growling noises to normal players.
*	A Werewolf automatically turns into his Wolf-form during a full moon, when under a open sky.
*	A Werewolf automatically turns into his Human-form at dawn.
*	A Werewolf only eats meat!
*	A Werewolf gains health from killing mobs (Hunting)
*	A Werewolf does high unarmed damage
*	A Werewolf moves faster
*	A Werewolf jumps higher
*	A Werewolf has night vision.
*	A Werewolf cannot wield Armor while in Wolf-form, but has a high natural defense
*	A Werewolf can growl with /growl
*	A Werewolf can howl with /howl
*	~~Vampires cannot be infected with the Werewolf infection~~ **_Vampire integration removed, for now_**
*	When a Werewolf gets killed, the player re-spawns in human form.
*	Werewolves are very vulnerable to silver weapons.
*	Werewolves are flock creatures and live in clans
*	Werewolves are the natural leaders of wild wolves
*	The Werewolf infection can be cured by drinking the werewolf cure potion

Installation
---------
*	[Install a Spigot server](https://github.com/DogOnFire/Werewolf/#obtain-a-build-of-spigot)
*	[Download Werewolf](https://github.com/DogOnFire/Werewolf/#download)
*	Drop the Werewolf.jar into the plugins folder.
*	Drop either [MySkin.jar](https://www.spigotmc.org/resources/myskin-1-8-1-14-4.52303/) or [LibsDisguises](https://www.spigotmc.org/resources/libs-disguises.32453/) into the plugins folder.
*	Start your Spigot/CraftBukkit server. (Using /reload can have unwanted side effects with players still online, and with complex plugins dependencies, so it's not recommended.)

Links
---------

###### Project
*	[Werewolf at SpigotMC](https://www.spigotmc.org/resources/werewolf.7442/)
*	[Werewolf at BukkitDev](https://dev.bukkit.org/bukkit-plugins/werewolf/)

###### Download
*	[BukkitDev](https://dev.bukkit.org/projects/werewolf/files/)
*	[SpigotMC](https://www.spigotmc.org/resources/werewolf.7442/updates)

###### Support and Documentation
*	[Issues/Tickets](https://github.com/DogOnFire/Issues/issues)
*	[Wiki](https://github.com/DogOnFire/Docs/wiki)
*	[Configuration](https://github.com/DogOnFire/Werewolf/wiki/Configuration)
*	[Permissions](https://github.com/DogOnFire/Werewolf/wiki/Permissions)
*	[Commands](https://github.com/DogOnFire/Werewolf/wiki/Commands)

###### Developers
*	[License (GPLv3)](https://github.com/DogOnFire/Werewolf/blob/master/LICENSE.txt)
*	[API](https://github.com/DogOnFire/Docs/wiki/API)
*	[Contribute](https://github.com/DogOnFire/Werewolf/blob/master/CONTRIBUTING.md)

###### Related Plugins
*	[ProtocolLib at BukkitDev](https://dev.bukkit.org/bukkit-plugins/protocollib)

Compiling Werewolf.jar
---------
*	Werewolf must be compiled with Java 8 compliance. Make sure you have a JDK 8 installed.
*	Werewolf has been developed using Eclipse (But any other IDE should work as well)
*	Checkout the Werewolf repo using [git](https://git-scm.com/downloads) in a Command Line: `git clone https://github.com/DogOnFire/Werewolf.git`
*	Open the Werewolf project folder in Eclipse (or another IDE of choice)
*	Right click the Werewolf project folder in Eclipse and select "Run as maven build..."
*	Click the "Run" button in the configuration window that appears
*	The Werewolf jar-file will be compiled and available in the /target folder under your project folder
