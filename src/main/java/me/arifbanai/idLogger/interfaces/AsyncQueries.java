package me.arifbanai.idLogger.interfaces;

import java.util.UUID;

/**
 * Async wrapper methods for {@link Queries}
 */
public interface AsyncQueries extends Queries {

    default void doAsyncNameLookup(final String playerUUID, IDLoggerCallback<String> callback) {
        try {
            final String name = getNameByUUID(playerUUID);

            callback.onSuccess(name);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    default void doAsyncUUIDLookup(final String playerName, IDLoggerCallback<String> callback) {
        try {
            final String uuid = getUUIDByName(playerName);

            callback.onSuccess(uuid);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    default void doAsyncAddPlayer(final UUID playerUUID, final String playerName, IDLoggerCallback<Void> callback) {
        try {
            addPlayer(playerUUID, playerName);

            callback.onSuccess(null);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    default void doAsyncRemovePlayer(final String playerUUID, IDLoggerCallback<Void> callback) {
        try {
            removePlayer(playerUUID);

            callback.onSuccess(null);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    default void doAsyncUpdatePlayerName(final UUID playerUUID, final String newPlayerName, IDLoggerCallback<Void> callback) {
        try {
            updatePlayerName(playerUUID, newPlayerName);

            callback.onSuccess(null);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }
}
