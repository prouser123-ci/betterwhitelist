# BetterWhitelist

Fork of [this](https://github.com/Dumb-Dog-Diner-Development/betterwhitelister) plugin by Jaquewolfee, rewritten to be compatible with BungeeCord.

## Overview

BetterWhitelist is a Bungee-compatible plugin for managing global bans shared between a BungeeCord network and any attached Discord servers.

**Everything is currently broken!!!** I'm working on moving the old code into two separate plugins:

- `BetterWhitelisterClient` - lives in the server instances
- `BetterWhitelisterBungee` - lives on the bungee proxy instance

The Bungee plugin will do most of the heavy lifting, managing the global ban list, informing server instances of new bans, etc. 
Each instance of the client plugin will request updates be made to the ban database, which will be propagated to all online server instances.
Similarly, config updates made on one server will be shared between all instances (unless you specify otherwise).

## To-Do List
- [x] ~~Separate the Bungee-related logic from the client logic~~
- [x] ~~Implement client-side messaging to Bungee + event handling for received messages~~ **- might need some work**
- [ ] Tidy up old Discord logic
- [ ] Implement Bungee messaging logic/event handling
- [ ] Fix gradle build configuration

## Storage

The global banlist and player whitelist will be stored in a SQL database on the Bungee instance. Data can be retrieved from the DB by
server instances using the `BungeeCord` plugin messaging channel.
