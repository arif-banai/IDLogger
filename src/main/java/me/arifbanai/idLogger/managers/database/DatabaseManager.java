package me.arifbanai.idLogger.managers.database;

import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.interfaces.IDLoggerCallback;
import me.arifbanai.idLogger.objects.LoggedPlayer;
import me.huskehhh.bukkitSQL.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

//TODO Initialize PreparedStatement(s) inside constructor and declare as field.

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

	public String getNameByUUID(String playerUUID) throws SQLException, PlayerNotIDLoggedException {
		PreparedStatement safeStatement;
		safeStatement = db.getConnection().prepareStatement("SELECT playerName FROM players WHERE "
				+ "playerUUID = ? ");

		safeStatement.setString(1, playerUUID);

		return LoggedPlayer.getName(safeStatement.executeQuery());
	}

	public String getUUIDByName(String playerName) throws SQLException, PlayerNotIDLoggedException {
		
		PreparedStatement safeStatement;
		safeStatement = db.getConnection().prepareStatement("SELECT playerUUID FROM players WHERE "
				+ "playerName = ? ");
		
		safeStatement.setString(1, playerName);
		
		return LoggedPlayer.findUUID(safeStatement.executeQuery());
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

	public void doAsyncUUIDLookup(final String playerName, final IDLoggerCallback<String> IDLoggerCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					final String playerUUID = getUUIDByName(playerName);
					Bukkit.getScheduler().runTask(plugin, new Runnable() {
						@Override
						public void run() {
							IDLoggerCallback.onSuccess(playerUUID);
						}
					});
				} catch (PlayerNotIDLoggedException | SQLException e) {
					IDLoggerCallback.onFailure(e);
				}
			}
		});
	}

	public void doAsyncNameLookup(final String playerUUID, final IDLoggerCallback<String> IDLoggerCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					final String playerName = getNameByUUID(playerUUID);
					Bukkit.getScheduler().runTask(plugin, new Runnable() {
						@Override
						public void run() {
							IDLoggerCallback.onSuccess(playerName);
						}
					});
				} catch (SQLException | PlayerNotIDLoggedException e) {
					IDLoggerCallback.onFailure(e);
				}
			}
		});
	}
	
}
