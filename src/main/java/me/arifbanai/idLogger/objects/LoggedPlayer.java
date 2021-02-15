package me.arifbanai.idLogger.objects;

import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoggedPlayer {

	final UUID playerUUID;
	final String playerName;

	public LoggedPlayer(UUID uuid, String name) {
		this.playerUUID = uuid;
		this.playerName = name;
	}

	public UUID getUuid() {
		return playerUUID;
	}

	public String getName() {
		return playerName;
	}

	public Player getOfflinePlayer() {
		return (Player) Bukkit.getServer().getOfflinePlayer(playerUUID);
	}

	public static List<LoggedPlayer> listPlayers(ResultSet result) {
		List<LoggedPlayer> playerList = new ArrayList<>();
		try {
			while (result.next()) {
				LoggedPlayer p = new LoggedPlayer(UUID.fromString(result.getString("playerUUID")), result.getString("playerName"));

				playerList.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("An SQLException occurred processing list of players");
			return null;
		}

		return playerList;
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
