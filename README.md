# IDLogger

This is a plugin that uses a configurable data source (such as a database) to store a player's UUID and name. 

The intention is that this be used as a dependency for other plugins that need persistent UUID/name storage across servers.
I made this for my own use in another plugin when I was updating my plugin to use UUIDs for the 1.8 update, which 
no longer made a username unique, instead being replaced by a Universally Unique IDentifier, or UUID.


The current version officially supports 1.15, and has been updated to use BukkitScheduler to perform async tasks, which 
should help improve performance on any server that makes use of multiple threads.

**Dependency for:** [vShop3.0 (aka vShop Remastered)](https://github.com/arif-banai/vShop3.0)

The plugin stores the players UUID and name upon joining the server. 
If the player's name is different from the one recorded in the DB, the DB is updated with the new name.

This plugin can utilize a MySQL or SQLite DB, as configurable in config.yml, to store these records.

## TODO
* Implement caching of UUID/name pair for player's who have recently played to reduce DB calls.

**Changelog**

- **2/11/2021**
    - Added comment explaining usage of "hikari.configurationFile" system property
    - Updated .gitignore and removed ignored files
    - Updated EasyPool dependency