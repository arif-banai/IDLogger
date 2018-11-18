package me.arifBanai.idLogger.managers.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import com.huskehhh.bukkitSQL.mysql.MySQL;

import me.arifBanai.idLogger.managers.ConfigManager;

public class MySQLManager extends DatabaseManager {

	public MySQLManager(final JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public void setupDb() throws ClassNotFoundException, SQLException {
		ConfigManager config = new ConfigManager(plugin);

		db = new MySQL(plugin, config.getHost(), config.getPort(), config.getDatabase(), config.getUsername(),
				config.getPassword());
		db.openConnection();
		
		Statement statement = db.getConnection().createStatement();
		
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS players ("
				+ "playerUUID VARCHAR(40) NOT NULL,"
				+ "playerName VARCHAR(40) NOT NULL,"
				+ "PRIMARY KEY(playerUUID),"
				+ "KEY (playerName)"
				+ ");");
		
		statement.close();
	}

	@Override
	public void closeDb() throws SQLException {
		plugin.getLogger().log(Level.INFO, "Disconnecting MySQL");
		db.closeConnection();
	}
}
