package me.arifbanai.idLogger.managers;

import me.arifbanai.easypool.DataSourceManager;
import me.arifbanai.easypool.enums.DataSourceType;
import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.interfaces.IDLoggerCallback;
import me.arifbanai.idLogger.objects.LoggedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;

/**
 * <p>Represents a generic query manager for some SQL DB implementation.</p>
 *
 * @since 4/26/2020 3:38AM, EST
 * @author Arif Banai
 */

public class QueryManager {
	
	private final JavaPlugin plugin;
	private final DataSourceManager dataSourceManager;
	private final DataSourceType dataSourceType;

	public QueryManager(final JavaPlugin instance, final DataSourceManager dataSourceManager) {
		plugin = instance;
		this.dataSourceManager = dataSourceManager;
		dataSourceType = dataSourceManager.getDataSourceType();
	}

	public void prepareDB() throws SQLException {
		switch(dataSourceType) {
			case MYSQL:
				setupMySQL();
				break;
			case SQLITE:
				setupSQLite();
				break;
			default:
				throw new SQLException("Error setting up the data source.");
		}
	}

	public void close() {
		plugin.getLogger().info("Disconnecting data source...");
		dataSourceManager.close();
	}

	public String getNameByUUID(String playerUUID) throws SQLException, PlayerNotIDLoggedException {
		String playerName = "";

		String getNameByUUIDSQL = "SELECT playerName FROM players WHERE playerUUID = ?";
		try(Connection connection = dataSourceManager.getConnection();
			PreparedStatement ps = connection.prepareStatement(getNameByUUIDSQL)
		)  {
			ps.setString(1, playerUUID);
		 	try(ResultSet rs = ps.executeQuery()) {
				playerName = LoggedPlayer.getName(rs);
			}
		}

		return playerName;
	}

	public String getUUIDByName(String playerName) throws SQLException, PlayerNotIDLoggedException {
		String playerUUID = "";

		String getUUIDByNameSQL = "SELECT playerUUID FROM players WHERE playerName = ?";
		try(Connection connection = dataSourceManager.getConnection();
			PreparedStatement ps = connection.prepareStatement(getUUIDByNameSQL)
		)  {
			ps.setString(1, playerName);
			try(ResultSet rs = ps.executeQuery()) {
				playerUUID = LoggedPlayer.findUUID(rs);
			}
		}

		return playerUUID;
	}

	public void addPlayer(UUID playerUUID, String playerName) throws SQLException {
		String addPlayerSQL = "INSERT INTO players(playerUUID,playerName) VALUES(?,?)";
		try(Connection connection = dataSourceManager.getConnection();
			PreparedStatement ps = connection.prepareStatement(addPlayerSQL)
		)  {
			ps.setString(1, playerUUID.toString());
			ps.setString(2, playerName);
			ps.executeUpdate();
		}
	}

	public void removePlayer(String playerUUID) throws SQLException {
		String deletePlayerSQL = "DELETE FROM players WHERE playerUUID = ?";
		try(Connection connection = dataSourceManager.getConnection();
			PreparedStatement ps = connection.prepareStatement(deletePlayerSQL)
		)  {
			ps.setString(1, playerUUID);
			ps.executeUpdate();
		}
	}

	public void updatePlayerName(UUID playerUUID, String playerName) throws SQLException {
		String updatePlayerNameSQL = "UPDATE players SET playerName = ? WHERE playerUUID = ?";
		try(Connection connection = dataSourceManager.getConnection();
			PreparedStatement ps = connection.prepareStatement(updatePlayerNameSQL)
		)  {
			ps.setString(1, playerName);
			ps.setString(2, playerUUID.toString());
			ps.executeUpdate();
		}
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
				} catch (SQLException exception) {
					IDLoggerCallback.onFailure(exception);
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
				} catch (SQLException exception) {
					IDLoggerCallback.onFailure(exception);
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

	private void setupMySQL() throws SQLException {
		String createTable = "CREATE TABLE IF NOT EXISTS players ("
				+ "playerUUID VARCHAR(40) NOT NULL,"
				+ "playerName VARCHAR(40) NOT NULL,"
				+ "PRIMARY KEY(playerUUID),"
				+ "KEY (playerName)"
				+ ");";

		try(Connection connection = dataSourceManager.getConnection();
			Statement statement = connection.createStatement();
		) {
			statement.executeUpdate(createTable);
		}
	}

	private void setupSQLite() throws SQLException {
		String createTable = "CREATE TABLE IF NOT EXISTS players ("
				+ "playerUUID VARCHAR(40) NOT NULL,"
				+ "playerName VARCHAR(40) NOT NULL,"
				+ "PRIMARY KEY(playerUUID)"
				+ ");";

		String createIndex = "CREATE INDEX IF NOT EXISTS indexNames on players (playerName);";


		try (Connection connection = dataSourceManager.getConnection();
			Statement statement = connection.createStatement();
		) {
			statement.executeUpdate(createTable);
			statement.executeUpdate(createIndex);
		}
	}
}
