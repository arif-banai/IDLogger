package me.arifbanai.idLogger.interfaces;

import java.util.UUID;

public interface Queries {

    /**
     * Prepare the database connection/pool
     * @throws Exception some error setting up the DB/pool
     */
    void prepareDB() throws Exception;

    /**
     * The name of the player, given their UUID
     * @param playerUUID the UUID of the player, as a String
     * @return the name mapped to the UUID
     * @throws Exception some problem getting the name
     */
    String getNameByUUID(String playerUUID) throws Exception;

    /**
     * The UUID of the player, given their name
     * @param playerName the player's in-game name
     * @return the UUID mapped to the player's name
     * @throws Exception some problem getting the UUID
     */
    String getUUIDByName(String playerName) throws Exception;

    /**
     * Add a new player-name entry into the database
     * @param playerUUID the player's UUID
     * @param playerName the player's name
     * @throws Exception some problem adding the player
     */
    void addPlayer(UUID playerUUID, String playerName) throws Exception;

    /**
     * Remove a player UUID-name entry from the database
     * @param playerUUID the player's UUID
     * @throws Exception some problem removing the player
     */
    void removePlayer(String playerUUID) throws Exception;

    /**
     * Update the name mapped to some UUID
     * @param playerUUID the player's UUID
     * @param newPlayerName the player's new name
     * @throws Exception some problem updating the player's name
     */
    void updatePlayerName(UUID playerUUID, String newPlayerName) throws Exception;
}
