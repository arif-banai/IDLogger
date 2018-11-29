package main.java.arifbanai.idLogger.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LoggedPlayer {

	String playerName;
	String playerUUID;

	public LoggedPlayer(String name, String uuid) {
		this.playerName = name;
		this.playerUUID = uuid;
	}

	public Player getPlayer() {
		return (Player) Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));
	}
	
	public String getName() {
		return playerName;
	}

	public String getUuid() {
		return playerUUID;
	}

	public static String findUUID(ResultSet result) throws SQLException {
		while (result.next()) {
			String playerUUID = result.getString("playerUUID");
			return playerUUID;
		}

		return "NaN";
	}
	
	public static String getName(ResultSet result) throws SQLException {
		while (result.next()) {
			String playerName = result.getString("playerName");
			return playerName;
		}

		return "NaN";
	}

}
