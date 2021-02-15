package me.arifbanai.idLogger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.arifbanai.easypool.DataSourceManager;
import me.arifbanai.easypool.MySQLDataSourceManager;
import me.arifbanai.easypool.SQLiteDataSourceManager;
import me.arifbanai.easypool.enums.DataSourceType;
import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.interfaces.IDLoggerCallback;
import me.arifbanai.idLogger.listeners.PlayerJoinListener;
import me.arifbanai.idLogger.managers.QueryManager;
import me.arifbanai.idLogger.managers.sql.SqlQueryManager;
import me.arifbanai.idLogger.objects.LoggedPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The main class for this plugin. Also the listener for all events processed by this plugin.
 * <p>
 * Two players are not allowed to have the same username.
 * However, a player is allowed to change their username.
 * Therefore, there is no need to worry about two players having the same username.
 * We must however check if a player has changed their username, and if so, update their name
 *
 * @since 5/1/2020 1:54AM
 * @author Arif Banai
 */
public class IDLogger extends JavaPlugin {

	private DataSourceManager dataSourceManager;
	private QueryManager queryManager;

	//TODO Add some "lastOnline" field to the DB to reduce the initial size of {@link #playersByUUID}
	public static BiMap<UUID, String> playersByUUID;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.saveResource("hikari.properties", false);

		// Set location of hikari properties file to System property
		// When HikariConfig default constructor is called, the properties file is loaded
		// see https://github.com/brettwooldridge/HikariCP (ctrl+F system property)
		String path = getDataFolder().toPath().toString();
		System.setProperty("hikaricp.configurationFile", path + "/hikari.properties");

		try {
			setupDsmAndQueryManager();
		} catch (SQLException | IOException exception) {
			handleUnexpectedException(exception);
		}

		initMap();

		getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, queryManager), this);
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabling plugin...");
		dataSourceManager.close();
		HandlerList.unregisterAll(this);
	}

	/**
	 * Get a player's name given their UUID. Utilizes an in-memory cache using {@link BiMap}.
	 * @param playerUUID the UUID of the player
	 * @return the name associated with playerUUID
	 * @throws PlayerNotIDLoggedException if their is no such player
	 */
	public String doNameLookup(UUID playerUUID) throws PlayerNotIDLoggedException {
		if(playersByUUID.containsKey(playerUUID)) {
			return playersByUUID.get(playerUUID);
		}

		final String[] name = new String[1];
		queryManager.doAsyncNameLookup(playerUUID.toString(), new IDLoggerCallback<String>() {
			@Override
			public void onSuccess(String result) {
				name[0] = result;
			}

			@Override
			public void onFailure(Exception cause) {
				if(cause instanceof PlayerNotIDLoggedException) {
					name[0] = "";
				} else {
					handleUnexpectedException(cause);
				}

			}
		});

		if(name[0].isEmpty()) {
			throw new PlayerNotIDLoggedException();
		}

		return playersByUUID.put(playerUUID, name[0]);
	}

	/**
	 * Get a player's UUID given their name. Utilizes an in-memory cache using {@link BiMap}.
	 * @param playerName the name of the player
	 * @return the UUID associated with playerName
	 * @throws PlayerNotIDLoggedException if their is no such player
	 */
	public UUID doUUIDLookup(String playerName) throws PlayerNotIDLoggedException {
		BiMap<String, UUID> playersByName = playersByUUID.inverse();
		if(playersByName.containsKey(playerName)) {
			return playersByName.get(playerName);
		}

		final String[] uuidString = new String[1];
		queryManager.doAsyncUUIDLookup(playerName, new IDLoggerCallback<String>() {
			@Override
			public void onSuccess(String result) {
				uuidString[0] = result;
			}

			@Override
			public void onFailure(Exception cause) {
				if(cause instanceof PlayerNotIDLoggedException) {
					uuidString[0] = "";
				} else {
					handleUnexpectedException(cause);
				}
			}
		});

		if(uuidString[0].isEmpty()) {
			throw new PlayerNotIDLoggedException();
		}

		return playersByName.put(playerName, UUID.fromString(uuidString[0]));
	}

	/**
	 * Setup the DSM with some RDBMS specified in config
	 * Initialize the {@link QueryManager} with the right DSM implementation
	 */
	private void setupDsmAndQueryManager() throws SQLException, IOException {
		FileConfiguration config = getConfig();
		dataSourceManager = null;

		if(config.getBoolean("using-sqlite", true)) {
			String path = getDataFolder().toPath().toString();
			dataSourceManager = new SQLiteDataSourceManager(path, this.getName());
			queryManager = new SqlQueryManager(this, dataSourceManager);
		} else {
			String host = config.getString("db.host");
			String port = config.getString("db.port");
			String schema = config.getString("db.schema");
			String user = config.getString("db.username");
			String password = config.getString("db.password");
			String dialect = config.getString("db.dialect");

			if (dialect == null)  {
				handleUnexpectedException(new Exception("DB dialect is null"));
			}

			// Use a switch to handle multiple sql dialects
			switch(DataSourceType.matchDialect(dialect)) {
				case MYSQL:
					dataSourceManager = new MySQLDataSourceManager(host, port, schema, user, password);
					queryManager = new SqlQueryManager(this, dataSourceManager);
					break;
				default:
					handleUnexpectedException(new Exception("Unable to resolve DB dialect"));
					break;
			}
		}
	}

	private void initMap() {
		queryManager.doAsyncGetAllLoggedPlayers(new IDLoggerCallback<List<LoggedPlayer>>() {
			@Override
			public void onSuccess(List<LoggedPlayer> result) {
				if(result.size() == 0) {
					playersByUUID = HashBiMap.create();
					return;
				}

				playersByUUID = HashBiMap.create(result.size());
				playersByUUID.putAll(result.stream().collect(Collectors.toMap(LoggedPlayer::getUuid,
						LoggedPlayer::getName)));
			}

			@Override
			public void onFailure(Exception cause) {
				handleUnexpectedException(cause);
			}
		});
	}

	private void handleUnexpectedException(Exception e) {
		this.getLogger().severe(e.toString());
		e.printStackTrace();
		this.getServer().getPluginManager().disablePlugin(this);
	}
}
