package me.arifbanai.idLogger.managers.sql;

import me.arifbanai.easypool.DataSourceManager;
import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.managers.QueryManager;
import me.arifbanai.idLogger.objects.LoggedPlayer;

import java.sql.*;
import java.util.UUID;

public class SqlQueries extends QueryManager {

    public SqlQueries(DataSourceManager dataSourceManager) {
        super(dataSourceManager);
    }

    @Override
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

    // Lookup operations

    @Override
    public String getNameByUUID(String playerUUID) throws SQLException, PlayerNotIDLoggedException {
        String playerName;

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

    @Override
    public String getUUIDByName(String playerName) throws SQLException, PlayerNotIDLoggedException {
        String playerUUID;

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

    // Insertion/removal/update operations

    @Override
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

    @Override
    public void removePlayer(String playerUUID) throws SQLException {
        String deletePlayerSQL = "DELETE FROM players WHERE playerUUID = ?";
        try(Connection connection = dataSourceManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(deletePlayerSQL)
        )  {
            ps.setString(1, playerUUID);
            ps.executeUpdate();
        }
    }

    @Override
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

    // init functions

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
