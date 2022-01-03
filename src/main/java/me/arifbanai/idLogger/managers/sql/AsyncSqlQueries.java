package me.arifbanai.idLogger.managers.sql;

import me.arifbanai.easypool.EasyPool;
import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.interfaces.IDLoggerCallback;
import me.arifbanai.idLogger.objects.LoggedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AsyncSqlQueries extends SqlQueries {

    private final JavaPlugin plugin;

    public AsyncSqlQueries(JavaPlugin plugin, EasyPool dataSourceManager) throws SQLException {
        super(dataSourceManager);
        this.plugin = plugin;
    }

    @Override
    public void doAsyncGetAllLoggedPlayers(IDLoggerCallback<List<LoggedPlayer>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final List<LoggedPlayer> loggedPlayers = getAllLoggedPlayers();
                Bukkit.getScheduler().runTask(plugin, () -> callback.onSuccess(loggedPlayers));
            } catch (SQLException e) {
                callback.onFailure(e);
            }
        });
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
