package me.arifbanai.idLogger.managers.database;

import me.huskehhh.bukkitSQL.sqlite.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteManager extends DatabaseManager {

	public SQLiteManager(final JavaPlugin instance) {
		super(instance);
	}

	@Override
	public void setupDb() throws ClassNotFoundException, SQLException {
		System.out.println(plugin.getDataFolder().toPath().toString());

		db = new SQLite(plugin, "IDLogger.db");
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
		db.closeConnection();
	}
}
