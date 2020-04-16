package me.arifbanai.idLogger;

import me.arifbanai.idLogger.exceptions.PlayerNotFoundException;
import me.arifbanai.idLogger.interfaces.IDLoggerCallback;
import me.arifbanai.idLogger.managers.ConfigManager;
import me.arifbanai.idLogger.managers.database.DatabaseManager;
import me.arifbanai.idLogger.managers.database.MySQLManager;
import me.arifbanai.idLogger.managers.database.SQLiteManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class IDLogger extends JavaPlugin implements Listener {

	private DatabaseManager db;
	private ConfigManager config;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		config = new ConfigManager(this);

		if (config.usingMySQL()) {
			db = new MySQLManager(this);
		} else {
			db = new SQLiteManager(this);
		}

		if (config.usingMySQL()) {
			System.out.println("MYSQL");
			db = new MySQLManager(this);
			try {
				db.setupDb();
			} catch (ClassNotFoundException | SQLException e) {
				this.getLogger().log(Level.SEVERE, "Unable to connect to MySQL. Displaying stack-trace.");
				e.printStackTrace();
				this.getServer().getPluginManager().disablePlugin(this);
			}
		} else {
			db = new SQLiteManager(this);

			try {
				System.out.println("SQLite");
				db.setupDb();
			} catch (ClassNotFoundException | SQLException e) {
				this.getLogger().log(Level.SEVERE, "Unable to use SQLite. Displaying stack-trace.");
				e.printStackTrace();
				this.getServer().getPluginManager().disablePlugin(this);
			}
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

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		try {
			String name = db.getNameByUUID(player.getUniqueId().toString());

			if (!name.equals(player.getName())) {
				db.updatePlayerName(player);
			}
		} catch (PlayerNotFoundException e) {
			try {
				db.addPlayer(player);
			} catch (SQLException | ClassNotFoundException throwables) {
				this.getLogger().severe("Unable to addPlayer.");
				e.printStackTrace();
				this.getServer().getPluginManager().disablePlugin(this);
			}
		} catch (ClassNotFoundException | SQLException e) {
			this.getLogger().severe("Unable to getNameByUUID or updatePlayerName");
			e.printStackTrace();
			this.getServer().getPluginManager().disablePlugin(this);
		}

	}

	public void doAsyncNameLookup(String playerUUID, final IDLoggerCallback<String> IDLoggerCallback) {
		db.doAsyncNameLookup(playerUUID, IDLoggerCallback);
	}

	public void doAsyncUUIDLookup(String playerName, final IDLoggerCallback<String> IDLoggerCallback) {
		db.doAsyncUUIDLookup(playerName, IDLoggerCallback);
	}
}
