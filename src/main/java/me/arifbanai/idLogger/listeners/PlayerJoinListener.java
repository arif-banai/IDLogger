package me.arifbanai.idLogger.listeners;

import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.interfaces.IDLoggerCallback;
import me.arifbanai.idLogger.managers.QueryManager;
import me.arifbanai.idLogger.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

import static me.arifbanai.idLogger.IDLogger.playersByUUID;

/**
 * EventHandler for PlayerJoinEvent
 * <p>
 * We must ensure any player that joins is logged, and that the logged information is accurate
 * <p>
 * This will ensure that any player joining
 * is in the cache {@link me.arifbanai.idLogger.IDLogger#playersByUUID}
 */
public class PlayerJoinListener implements Listener {

    private final JavaPlugin plugin;
    private final QueryManager queryManager;

    public PlayerJoinListener(JavaPlugin plugin, QueryManager queryManager) {
        this.plugin = plugin;
        this.queryManager = queryManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();

        //Check if player is already in the list
        if(playersByUUID.containsKey(playerUUID)) {
            if(!playersByUUID.get(playerUUID).equals(playerName)) {
                queryManager.doAsyncUpdatePlayerName(playerUUID, playerName, new IDLoggerCallback<>() {
                    @Override
                    public void onSuccess(Void result) {
                        playersByUUID.replace(playerUUID, playerName);
                    }

                    @Override
                    public void onFailure(Exception cause) {
                        handleUnexpectedException(cause);
                    }
                });
            }

            return;
        }

        //Player is not in the map, check if they are in the DB
        queryManager.doAsyncNameLookup(playerUUID.toString(), new IDLoggerCallback<>() {
            @Override
            public void onSuccess(String result) {

                //If in the DB, but the names don't match, update the name
                if (!result.equals(player.getName())) {
                    queryManager.doAsyncUpdatePlayerName(player.getUniqueId(), player.getName(), new IDLoggerCallback<>() {
                        @Override
                        public void onSuccess(Void result) {
                        }

                        @Override
                        public void onFailure(Exception cause) {
                            handleUnexpectedException(cause);
                        }
                    });
                }

                //Add the player to the Map
                playersByUUID.put(player.getUniqueId(), player.getName());
            }

            @Override
            public void onFailure(Exception cause) {
                if (cause instanceof PlayerNotIDLoggedException) {
                    queryManager.doAsyncAddPlayer(player.getUniqueId(), player.getName(), new IDLoggerCallback<>() {
                        @Override
                        public void onSuccess(Void result) {
                            ChatUtils.sendSuccess(player, "Your UUID and username have been logged!");
                        }

                        @Override
                        public void onFailure(Exception cause) {
                            handleUnexpectedException(cause);
                        }
                    });

                    playersByUUID.put(player.getUniqueId(), player.getName());
                    return;
                }

                handleUnexpectedException(cause);
            }
        });
    }

    private void handleUnexpectedException(Exception e) {
        plugin.getLogger().severe(e.toString());
        e.printStackTrace();
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }
}
