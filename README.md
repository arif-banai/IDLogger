# IDLogger

This is a plugin that uses a configurable data source (such as a database) to store a player's UUID and name.

The intention is that this be used as a dependency for other plugins that need to lookup a player's UUID given their name. 

Player UUID's and names are persisted in some supported DB (Utilizes [EasyPool](https://github.com/arif-banai/EasyPool)) and utilizes a [BiMap](https://guava.dev/releases/19.0/api/docs/com/google/common/collect/BiMap.html) as an in-memory cache to eliminate unnecessary DB calls.

##How to use
Build and install to your local maven repo, then add the dependency
```xml
<dependency>
    <groupId>me.arifbanai</groupId>
    <artifactId>idlogger</artifactId>
    <version>2.2</version>
</dependency>
```

Then, get a reference to the IDLogger plugin using the plugin manager.

```java
IDLogger idLogger = (IDLogger) Bukkit.getPluginManager().getPlugin("IDLogger");
```

You can then make use of two important methods
```java
// Get a player's name given their UUID
String playerName = idLogger.doNameLookup(UUID playerUUID)

// Get a player's UUID given their name
UUID playerUUID = idLogger.doUUIDLookup(String playerName);
```

I made this for my own use in another plugin when I was updating my plugin to use UUIDs for the 1.8 update, which no longer made a username unique, instead being replaced by a Universally Unique Identifier, or UUID.

The current version was compiled using Java 17 and targets spigot 1.18.1

**Dependency for:** [vShop3.0 (aka vShop Remastered)](https://github.com/arif-banai/vShop3.0)

The plugin stores the player's UUID and name upon joining the server. 
If the player's name is different from the one recorded in the DB, the DB is updated with the new name.

This plugin can utilize a MySQL or SQLite DB, as configurable in config.yml, to store these records.

## TODO
* Improve the cache system

**Changelog**

- **1/02/2022**
  - Happy new year!
  - Refactoring and updating api versions

- **2/11/2021**
  - Added comment explaining usage of "hikari.configurationFile" system property
  - Updated .gitignore and removed ignored files
  - Updated EasyPool dependency
  - Abstracted query logic using interfaces and abstract parent class
  - Grouped initialization of QueryManager and DataSourceManager together, allows for 
    future additions to supported data sources (postgres, oracle, etc.)
  - Trimmed private exception handler method
- **2/15/2021**
  - Added BiMap (bi-directional map) to reduce DB calls
  - Added a query to retrieve all logged players, used to fill BiMap on startup
  - Edited lookup methods to first check the BiMap
  - Extracted Listener away from main class
  - Added JUnit to test the functionality, independent of Bukkit dependency
  
  