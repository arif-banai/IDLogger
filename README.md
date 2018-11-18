# IDLogger
Logs Player UUID and Player name to MySQL or SQLite DB for Spigot 1.13

Useful for plugins that need to store player UUIDs and track a player's current name. 
The plugin stores the players UUID and name upon joining the server. 
Dependency for vShop3.0 (aka vShop Remastered) https://github.com/arif-banai/vShop3.0

If the players name is different from the one recorded in the DB, the DB is updated with the new name.
Since player names are not always unique since the introduction of player UUIDs, I created this API to 
store a record for every player that has connected to the server, and keep a mapping of every players UUID to their current name.
This plugin will utilize a MySQL or SQLite DB, as configurable in config.yml, to store these records.
