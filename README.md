# BetterWhitelister
BetterWhitelist makes whitelisting new players way easier for private servers! This plugin uses <a href="https://github.com/DV8FromTheWorld/JDA">JDA</a> to enable automatic whitelisting via Discord. The command and messages are completly customizable.
<b>Important:</b> A server restart is required whenever you set a new bot token in the ```config.yml```.
You can find the latest releases <a href="https://github.com/Dumb-Dog-Diner-Development/betterwhitelister/releases">here</a>

Commands | Permission | Description
------------ | ------------- | -------------
```/betterwhitelist``` | ```betterwhitelist.command``` | Main command, displays available arguments if no argument is given
```/betterwhitelist help``` | ```betterwhitelist.command``` | Displays available arguments
```/betterwhitelist reload``` | ```betterwhitelist.command.reload``` | Reloads the plugin config
```/betterwhitelist whois <Minecraft User>``` | ```betterwhitelist.command.whois``` | Displays information about the Discord account <br>connected to the given Minecrat account
```/betterwhitelist enable``` | ```betterwhitelist.command``` | Enable automatic whitelisting
```/betterwhitelist disable``` | ```betterwhitelist.command``` | Disable automatic whitelisting

This plugin also adds a custom command to Discord. It is ```!minecraft <Minecraft Username>``` by default, prefix and command name can be changed though!

# The Config File

```yaml
# Enable ban syncing. Users will be banned on both Discord and Minecraft
# Bot will require permissions to ban users on Discord!!
enableBanSync: true

# Restrict Discord users to one Minecraft account. If set to false, users can connect 
# as many Minecraft accounts to their Discord account as they want.
oneAccountPerUser: true

# Store user data either locally (flatfile) or on a database (mysql).
filetype: flatfile

# MySQL login information goes here if filetype is set to mysql! 
# The bot requires SELECT, INSERT, CREATE and DELETE privileges.
mysql:
  host: "127.0.0.1"
  database: "database"
  port: "3306"
  username: "root"
  password: "password"


discord:
  
  # Disable/Enable the whitelisting command. Can be done ingame with /betterwhitelist disable/enable
  enableAutoWhitelisting: true

  # Bot token to login into Discord. Visit https://discordapp.com/developers/applications/
  # to create a new bot. 
  token: "TOKEN"

  # Guild ID of the Discord server the bot should run on.
  guildid: "GUILD_ID"

  # Prefix of the discord command. Helpful if you want to
  # connect an existing bot and use the same prefix.
  prefix: "!"
  
  # The command name
  validationCommandName: "minecraft"
  
  # Minimum role requirement to run the Discord command
  # Requires role id!
  reqRole:
    enabled: false
    roleid: "ROLE_ID"
    
  # Give a role to your Discord members after they have been whitelisted!
  # Requires role id, "silent" prevents the bot from announcing that someone
  # received the role. Change lang.giveRoleMessage if "silent" is set to true!!!
  giveRole:
    enabled: false
    roleid: "ROLE_ID"
    silent: true
    
# Language... Feel free to change everything to fit
# your server!
lang:
  userAlreadyWhitelisted: "You are already whitelisted!"
  validationSuccess: "You've been whitelisted!"
  validationError: "Something went wrong. Does the provided username exist?"
  missingRoleError: "You don't have the required role to do this"
  whitelistingDisabled: "Automatic whitelisting is currently disabled"
  giveRoleMessage: "You've also been given the <@&ROLE_ID> role! Make sure to check <#channel-id>, before joining the server"
```
