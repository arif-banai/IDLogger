package me.arifbanai.idLogger.managers.database;

import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.interfaces.IDLoggerCallback;
import me.arifbanai.idLogger.objects.LoggedPlayer;
import me.huskehhh.bukkitSQL.Database;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

//TODO Initialize PreparedStatement(s) inside constructor and declare as field.

public abstract class DatabaseManager {
	
	protected JavaPlugin plugin;
	protected Database db;

	PreparedStatement getNameByUUIDStatement;
	PreparedStatement getUUIDByNameStatement;
	PreparedStatement addPlayerStatement;
	PreparedStatement deletePlayerStatement;
	PreparedStatement updatePlayerNameStatement;

	public DatabaseManager(final JavaPlugin instance) {
		plugin = instance;
	}

	public abstract void setupDb() throws ClassNotFoundException, SQLException;

	public abstract void closeDb() throws SQLException;

	public boolean checkConnection() {
		return db.getConnection() != null;
	}

	public String getNameByUUID(String playerUUID) throws SQLException, PlayerNotIDLoggedException {
		getNameByUUIDStatement.setString(1, playerUUID);
		return LoggedPlayer.getName(getNameByUUIDStatement.executeQuery());
	}

	public String getUUIDByName(String playerName) throws SQLException, PlayerNotIDLoggedException {
		getUUIDByNameStatement.setString(1, playerName);
		return LoggedPlayer.findUUID(getUUIDByNameStatement.executeQuery());
	}

	public void addPlayer(UUID playerUUID, String playerName) throws SQLException {

		addPlayerStatement.setString(1, playerUUID.toString());
		addPlayerStatement.setString(2, playerName);
		
		addPlayerStatement.executeUpdate();
	}

	public void removePlayer(String playerUUID) throws ClassNotFoundException, SQLException {
		deletePlayerStatement.setString(1, playerUUID);
		deletePlayerStatement.executeUpdate();
	}

	public void updatePlayerName(UUID playerUUID, String playerName) throws SQLException, ClassNotFoundException {
		updatePlayerNameStatement.setString(1, playerName);
		updatePlayerNameStatement.setString(2, playerUUID.toString());
		
		updatePlayerNameStatement.executeUpdate();
	}

	public void doAsyncAddPlayer(final UUID playerUUID, final String playerName, final IDLoggerCallback<Void> IDLoggerCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					addPlayer(playerUUID, playerName);

					Bukkit.getScheduler().runTask(plugin, new Runnable() {
						@Override
						public void run() {
							IDLoggerCallback.onSuccess(null);
						}
					});
				} catch (SQLException e) {
					IDLoggerCallback.onFailure(e);
				}
			}
		});
	}

	public void doAsyncUpdatePlayerName(final UUID playerUUID, final String playerName, final IDLoggerCallback<Void> IDLoggerCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					updatePlayerName(playerUUID, playerName);

					Bukkit.getScheduler().runTask(plugin, new Runnable() {
						@Override
						public void run() {
							IDLoggerCallback.onSuccess(null);
						}
					});
				} catch (SQLException | ClassNotFoundException e) {
					IDLoggerCallback.onFailure(e);
				}
			}
		});
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

	protected void setupPreparedStatements() throws SQLException {
		getNameByUUIDStatement = db.getConnection().prepareStatement("SELECT playerName FROM players WHERE "
				+ "playerUUID = ? ");
		getUUIDByNameStatement = db.getConnection().prepareStatement("SELECT playerUUID FROM players WHERE "
				+ "playerName = ? ");
		addPlayerStatement = db.getConnection().prepareStatement("INSERT INTO " + "players(playerUUID,playerName)"
				+ "VALUES(?,?)");
		deletePlayerStatement = db.getConnection().prepareStatement("DELETE FROM players WHERE "
				+ "playerUUID = ?");
		updatePlayerNameStatement = db.getConnection().prepareStatement("UPDATE players SET "
				+ "playerName = ? WHERE "
				+ "playerUUID = ?");
	}
}
