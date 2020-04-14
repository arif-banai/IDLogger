package me.arifbanai.idLogger.managers;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
	public JavaPlugin plugin;

	public ConfigManager(final JavaPlugin instance) {
		plugin = instance;
	}

	public boolean usingMySQL() {
		return plugin.getConfig().getBoolean("using-mysql", false);
	}

	public String getHost() {
		return plugin.getConfig().getString("MySQL.host", "localhost");
	}

	public String getPort() {
		return plugin.getConfig().getString("MySQL.port", "3306");
	}

	public String getDatabase() {
		return plugin.getConfig().getString("MySQL.database", "minecraft");
	}

	public String getUsername() {
		return plugin.getConfig().getString("MySQL.username", "root");
	}

	public String getPassword() {
		return plugin.getConfig().getString("MySQL.password", "password");
	}
}
