package me.arifbanai.idLogger.managers.database;

import me.arifbanai.idLogger.objects.LoggedPlayer;
import me.huskehhh.bukkitSQL.Database;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public abstract class DatabaseManager {
	
	protected JavaPlugin plugin;
	protected Database db;

	public DatabaseManager(final JavaPlugin instance) {
		plugin = instance;
	}

	public abstract void setupDb() throws ClassNotFoundException, SQLException;

	public abstract void closeDb() throws SQLException;

	public boolean checkConnection() {
		return db.getConnection() != null;
	}

	public String getNameByUUID(String playerUUID) throws SQLException {

		PreparedStatement safeStatement;
		safeStatement = db.getConnection().prepareStatement("SELECT playerName FROM players WHERE "
				+ "playerUUID = ? ");
		
		safeStatement.setString(1, playerUUID);
		
		return LoggedPlayer.getName(safeStatement.executeQuery());
	}

	public String getUUIDByName(String playerName) throws SQLException {
		
		PreparedStatement safeStatement;
		safeStatement = db.getConnection().prepareStatement("SELECT playerUUID FROM players WHERE "
				+ "playerName = ? ");
		
		safeStatement.setString(1, playerName);
		
		String targetUUID = "";
		targetUUID = LoggedPlayer.findUUID(safeStatement.executeQuery());
		
		if(targetUUID.equals("NaN")) {
			return null;
		}

		return targetUUID;
	}

	public void addPlayer(Player player) throws SQLException, ClassNotFoundException {
		
		PreparedStatement safeStatement;
		safeStatement = db.getConnection().prepareStatement("INSERT INTO "
				+ "players(playerUUID,playerName)"
				+ "VALUES(?,?)");
		
		safeStatement.setString(1, player.getUniqueId().toString());
		safeStatement.setString(2, player.getName());
		
		safeStatement.executeUpdate();
	}

	public void removePlayer(String playerUUID) throws ClassNotFoundException, SQLException {

		PreparedStatement safeStatement;
		safeStatement = db.getConnection().prepareStatement("DELETE FROM players WHERE "
				+ "playerUUID = ?");
		
		safeStatement.setString(1, playerUUID);
		
		safeStatement.executeUpdate();
	}

	public void updatePlayerName(Player player) throws SQLException, ClassNotFoundException {
		
		PreparedStatement safeStatement;
		safeStatement = db.getConnection().prepareStatement("UPDATE players SET "
				+ "playerName = ? WHERE "
				+ "playerUUID = ?");
		
		safeStatement.setString(1, player.getName());
		safeStatement.setString(2, player.getUniqueId().toString());
		
		safeStatement.executeUpdate();
	}
	
}
