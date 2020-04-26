package me.arifbanai.idLogger;

import me.arifbanai.idLogger.datasource.DataSourceManager;
import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.interfaces.IDLoggerCallback;
import me.arifbanai.idLogger.managers.ConfigManager;
import me.arifbanai.idLogger.managers.QueryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class IDLogger extends JavaPlugin implements Listener {

	private QueryManager db;
	private DataSourceManager dataSourceManager;
	private ConfigManager config;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		config = new ConfigManager(this);
		dataSourceManager = new DataSourceManager(this);
		db =  new QueryManager(this, dataSourceManager);

		try {
			db.prepareDB();
		} catch (SQLException exception) {
			handleSqlError(exception, "onEnable");
		}

		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		Bukkit.getLogger().log(Level.INFO, "[IDLogger] Disabling plugin...");

		try {
			db.closeDb();
		} catch (SQLException e) {
			//Nothing, the plugin is being disabled
		}

		HandlerList.unregisterAll((JavaPlugin) this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		doAsyncNameLookup(player.getUniqueId().toString(), new IDLoggerCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String name = result;

				if (!name.equals(player.getName())) {
					db.doAsyncUpdatePlayerName(player.getUniqueId(), player.getName(), new IDLoggerCallback<Void>() {
						@Override
						public void onSuccess(Void result) {

						}

						@Override
						public void onFailure(Throwable cause) {
							handleSqlError((Exception) cause, "AsyncUpdatePlayer callback");
						}
					});
				}
			}

			@Override
			public void onFailure(Throwable cause) {
				if(cause instanceof PlayerNotIDLoggedException) {
					db.doAsyncAddPlayer(player.getUniqueId(), player.getName(), new IDLoggerCallback<Void>() {
						@Override
						public void onSuccess(Void result) {

						}

						@Override
						public void onFailure(Throwable cause) {
							handleSqlError((Exception) cause, "AsyncAddPlayer callback");
						}
					});
					return;
				}

				handleSqlError((Exception) cause, "AsyncNameLookup callback");
			}
		});
	}

	public ConfigManager getConfigManager() {
		return config;
	}

	public void doAsyncNameLookup(String playerUUID, final IDLoggerCallback<String> IDLoggerCallback) {
		db.doAsyncNameLookup(playerUUID, IDLoggerCallback);
	}

	public void doAsyncUUIDLookup(String playerName, final IDLoggerCallback<String> IDLoggerCallback) {
		db.doAsyncUUIDLookup(playerName, IDLoggerCallback);
	}

	protected void handleSqlError(Exception e, String methodOccurred) {
		this.getLogger().severe("An SQLException occurred in IDLogger::" + methodOccurred);
		e.printStackTrace();
		this.getServer().getPluginManager().disablePlugin(this);
	}
}
