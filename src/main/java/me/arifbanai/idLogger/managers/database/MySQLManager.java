package me.arifbanai.idLogger.managers.database;

import me.arifbanai.idLogger.managers.ConfigManager;
import me.huskehhh.bukkitSQL.mysql.MySQL;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

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

		setupPreparedStatements();
	}

	@Override
	public void closeDb() throws SQLException {
		plugin.getLogger().log(Level.INFO, "Disconnecting MySQL");
		db.closeConnection();
	}
}
