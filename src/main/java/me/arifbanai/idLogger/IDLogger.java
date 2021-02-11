package me.arifbanai.idLogger;

import me.arifbanai.easypool.DataSourceManager;
import me.arifbanai.easypool.MySQLDataSourceManager;
import me.arifbanai.easypool.SQLiteDataSourceManager;
import me.arifbanai.easypool.enums.DataSourceType;
import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.interfaces.IDLoggerCallback;
import me.arifbanai.idLogger.managers.QueryManager;
import me.arifbanai.idLogger.managers.sql.SqlQueryManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

/**
 * The main class for this plugin. Also the listener for all events processed by this plugin.
 * <p>
 * TODO Implement caching for players who have recently played, in order to reduce DB calls.
 *
 * TODO ^^^ Add some Map to reduce DB calls ^^^
 *
 * @since 5/1/2020 1:54AM
 * @author Arif Banai
 */
public class IDLogger extends JavaPlugin implements Listener {

	private DataSourceManager dataSourceManager;
	private QueryManager queryManager;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.saveResource("hikari.properties", false);

		// Set location of hikari properties file to System property
		// When HikariConfig default constructor is called, the properties file is loaded
		// see https://github.com/brettwooldridge/HikariCP (ctrl+F system property)
		String path = getDataFolder().toPath().toString();
		System.setProperty("hikaricp.configurationFile", path + "/hikari.properties");

		if(!setupDsmAndQueryManager()) {
			handleUnexpectedException(new Exception("Failed to initialize DataSourceManager."));
		}

		try {
			queryManager.prepareDB();
		} catch (Exception exception) {
			handleUnexpectedException(exception);
		}

		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabling plugin...");
		dataSourceManager.close();
		HandlerList.unregisterAll((JavaPlugin) this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		doAsyncNameLookup(player.getUniqueId().toString(), new IDLoggerCallback<String>() {
			@Override
			public void onSuccess(String result) {

				if (!result.equals(player.getName())) {
					queryManager.doAsyncUpdatePlayerName(player.getUniqueId(), player.getName(), new IDLoggerCallback<Void>() {
						@Override
						public void onSuccess(Void result) {

						}

						@Override
						public void onFailure(Exception cause) {
							handleUnexpectedException(cause);
						}
					});
				}
			}

			@Override
			public void onFailure(Exception cause) {
				if(cause instanceof PlayerNotIDLoggedException) {
					queryManager.doAsyncAddPlayer(player.getUniqueId(), player.getName(), new IDLoggerCallback<Void>() {
						@Override
						public void onSuccess(Void result) {

						}

						@Override
						public void onFailure(Exception cause) {
							handleUnexpectedException(cause);
						}
					});
					return;
				}

				handleUnexpectedException(cause);
			}
		});
	}

	public void doAsyncNameLookup(String playerUUID, final IDLoggerCallback<String> IDLoggerCallback) {
		queryManager.doAsyncNameLookup(playerUUID, IDLoggerCallback);
	}

	public void doAsyncUUIDLookup(String playerName, final IDLoggerCallback<String> IDLoggerCallback) {
		queryManager.doAsyncUUIDLookup(playerName, IDLoggerCallback);
	}

	protected void handleUnexpectedException(Exception e) {
		this.getLogger().severe(e.toString());
		e.printStackTrace();
		this.getServer().getPluginManager().disablePlugin(this);
	}

	/**
	 * Setup the DSM with some RDBMS specified in config
	 * Initialize the {@link QueryManager} with the right DSM implementation
	 * @return true if setup was successful, false if failed
	 */
	private boolean setupDsmAndQueryManager() {
		FileConfiguration config = getConfig();
		dataSourceManager = null;

		if(config.getBoolean("using-sqlite", true)) {
			String path = getDataFolder().toPath().toString();
			try {
				dataSourceManager = new SQLiteDataSourceManager(path, this.getName());
				queryManager = new SqlQueryManager(this, dataSourceManager);
			} catch (IOException e) {
				handleUnexpectedException(e);
			}
		} else {
			String host = config.getString("db.host");
			String port = config.getString("db.port");
			String schema = config.getString("db.schema");
			String user = config.getString("db.username");
			String password = config.getString("db.password");
			String dialect = config.getString("db.dialect");

			if (dialect == null)  {
				handleUnexpectedException(new Exception("DB dialect is null"));
				return false;
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

		return (dataSourceManager != null && queryManager != null);
	}
}
