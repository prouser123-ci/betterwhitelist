# BetterWhitelistBungee

`BetterWhitelistBungee` is the bungee-side plugin for BetterWhitelist, handling the database management and Discord bot self whitelisting.

## Configuration

The default configuration is shown below - comments are removed by the build process, so your `config.yml` won't look like this!

```yaml
# Whether to connect to the SQL server. Enable this if you want the bot to
# be able to store whitelisted users.
enableSql: false

# MySQL Database Details
# The bot requires SELECT, INSERT, CREATE and DELETE privileges.
mysql:
  host: "127.0.0.1"
  database: "database"
  port: "3306"
  username: "root"
  password: "password"

discord:
  # Bot token the plugin should use.
  token: 'discord token here'

  # The ID of the guild you want the bot to listen in.
  guildId: 'guild id here'

  # The prefix the bot should use.
  prefix: '-'

  # Whether users are able to run the `-minecraft <username>` command
  # and verify themselves.
  enableSelfWhitelisting: true

  # Enable ban syncing. Users will be banned on both Discord and Minecraft.
  # Bot will require permissions to ban users on Discord!!
  enableBanSync: true

  # Restrict Discord users to one Minecraft account. If set to false, users can connect
  # as many Minecraft accounts to their Discord account as they want.
  oneAccountPerUser: true

  roles:
    # The role users are required to have to be able to be whitelisted.
    requiredRole:
      enabled: false
      roleId: 'required role id here'

    # The role users are granted once whitelisted.
    grantedRole:
      enabled: false
      silent: false
      roleId: ''
```

## Discord Commands

- `-whitelist <username>` - add a user to the whitelist. Throws an error if the user could not be found on Mojang's servers. If a user already has an account registered, and `oneAccountPerUser` is enabled, the user will not be able to add another username.
- `-unwhitelist` - remove a user from the whitelist.
- `-help` - display a help message.
- `-status` - retrieve brief bot status information.
## Bungee Console Commands

As of yet, there aren't any built-in commands to the bungee console itself. The client-side plugins on each server instance handle those.