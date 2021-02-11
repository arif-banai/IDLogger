package me.arifbanai.idLogger.managers.sql;

import me.arifbanai.easypool.DataSourceManager;
import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.interfaces.IDLoggerCallback;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.UUID;

public class SqlQueryManager extends SqlQueries {

    private final JavaPlugin plugin;

    public SqlQueryManager(JavaPlugin plugin, DataSourceManager dataSourceManager) {
        super(dataSourceManager);
        this.plugin = plugin;
    }

    @Override
    public void doAsyncNameLookup(String playerUUID, IDLoggerCallback<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final String playerName = getNameByUUID(playerUUID);
                Bukkit.getScheduler().runTask(plugin, () -> callback.onSuccess(playerName));
            } catch (SQLException | PlayerNotIDLoggedException e) {
                callback.onFailure(e);
            }
        });
    }

    @Override
    public void doAsyncUUIDLookup(String playerName, IDLoggerCallback<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final String playerUUID = getUUIDByName(playerName);
                Bukkit.getScheduler().runTask(plugin, () -> callback.onSuccess(playerUUID));
            } catch (PlayerNotIDLoggedException | SQLException e) {
                callback.onFailure(e);
            }
        });
    }

    @Override
    public void doAsyncAddPlayer(UUID playerUUID, String playerName, IDLoggerCallback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                addPlayer(playerUUID, playerName);
                Bukkit.getScheduler().runTask(plugin, () -> callback.onSuccess(null));
            } catch (SQLException exception) {
                callback.onFailure(exception);
            }
        });
    }

    @Override
    public void doAsyncRemovePlayer(String playerUUID, IDLoggerCallback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                removePlayer(playerUUID);
                Bukkit.getScheduler().runTask(plugin, () -> callback.onSuccess(null));
            } catch (SQLException exception) {
                callback.onFailure(exception);
            }
        });
    }

    @Override
    public void doAsyncUpdatePlayerName(UUID playerUUID, String newPlayerName, IDLoggerCallback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                updatePlayerName(playerUUID, newPlayerName);
                Bukkit.getScheduler().runTask(plugin, () -> callback.onSuccess(null));
            } catch (SQLException exception) {
                callback.onFailure(exception);
            }
        });
    }
}
