package me.arifbanai.idLogger.objects;

import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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

	public static String findUUID(ResultSet result) throws SQLException, PlayerNotIDLoggedException {
		if (result.next()) {
			String playerUUID = result.getString("playerUUID");
			if(playerUUID == null) {
				throw new PlayerNotIDLoggedException();
			}
			return playerUUID;
		}

		throw new PlayerNotIDLoggedException();
	}
	
	public static String getName(ResultSet result) throws SQLException, PlayerNotIDLoggedException {
		if (result.next()) {
			String playerName = result.getString("playerName");

			if(playerName == null) {
				throw new PlayerNotIDLoggedException();
			}

			return playerName;
		}

		throw new PlayerNotIDLoggedException();
	}

}
