package me.arifbanai.idLogger;

import me.arifbanai.easypool.EasyPool;
import me.arifbanai.easypool.MariaDB;
import me.arifbanai.easypool.MySQL;
import me.arifbanai.easypool.SQLite;
import me.arifbanai.idLogger.exceptions.PlayerNotIDLoggedException;
import me.arifbanai.idLogger.managers.QueryManager;
import me.arifbanai.idLogger.managers.sql.SqlQueries;
import me.arifbanai.idLogger.objects.LoggedPlayer;
import me.arifbanai.idLogger.utils.Config;
import org.junit.jupiter.api.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class IDLoggerTests {

    private static EasyPool pool;
    private static QueryManager queries;

    private static List<LoggedPlayer> loggedPlayers;

    private static final String RESOURCES_DIR = "src/test/resources/";
    private static final String CONFIG_FILENAME = "config.yml";
    private static final String UUIDS_FILENAME = "playerUUIDs.txt";
    private static final String NAMES_FILENAME = "names.txt";

    /**
     * Setup EasyPool
     */
    @BeforeAll
    @DisplayName("Start a connection to the DB using EasyPool")
    public static void setupEasyPool() throws Exception {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        File configFile = new File(RESOURCES_DIR + CONFIG_FILENAME);
        InputStream inputStream = new FileInputStream(configFile);

        Config config = yaml.load(inputStream);

        String dialect = config.getDialect();
        String host = config.getHost();
        String port = config.getPort();
        String schema = config.getSchema();
        String user = config.getUsername();
        String password = config.getPassword();

        switch (dialect) {
            case "sqlite" -> {
                pool = new SQLite(RESOURCES_DIR, "IDLoggerTest");
                queries = new SqlQueries(pool);
                queries.prepareDB();
            }
            case "mysql" -> {
                pool = new MySQL(host, port, schema, user, password);
                queries = new SqlQueries(pool);
                queries.prepareDB();
            }
            case "mariadb" -> {
                pool = new MariaDB(host, port, schema, user, password);
                queries = new SqlQueries(pool);
                queries.prepareDB();
            }
            default -> throw new IOException("Unable to resolve DB dialect");
        }
    }

    @BeforeEach
    public void getLoggedPlayers() throws Exception {
        loggedPlayers = queries.getAllLoggedPlayers();
    }

    @Test
    @DisplayName("Add a new player to the system")
    public void addPlayer() throws Exception {

        LoggedPlayer randomPlayer = generatePlayers(1).get(0);

        UUID playerUUID = randomPlayer.getUuid();
        String playerName = randomPlayer.getName();

        System.out.println(playerUUID);
        System.out.println(playerName);

        queries.addPlayer(playerUUID, playerName);

        Assertions.assertFalse(queries.getUUIDByName(playerName).isEmpty());
    }

    @Test
    @DisplayName("Perform UUID and Name lookup on a random logged player (or create a player if needed)")
    public void lookupPlayer() throws Exception {

        LoggedPlayer randomPlayer;

        if(loggedPlayers.isEmpty()) {
            randomPlayer = generatePlayers(1).get(0);
            queries.addPlayer(randomPlayer.getUuid(), randomPlayer.getName());
        } else {
            randomPlayer = getRandomPlayer(loggedPlayers);
        }

        UUID playerUUID = randomPlayer.getUuid();
        String playerName = randomPlayer.getName();

        System.out.println(playerUUID);
        System.out.println(playerName);

        Assertions.assertEquals(playerName, queries.getNameByUUID(playerUUID.toString()));
        Assertions.assertEquals(playerUUID, UUID.fromString(queries.getUUIDByName(playerName)));
    }

    /**
     * Removes a player from the log (creates a player and adds them first if none existed)
     * @throws PlayerNotIDLoggedException
     * @throws Exception
     */
    @Test
    @DisplayName("Remove a player from the log")
    public void removePlayer() throws Exception {

        LoggedPlayer randomPlayer;

        if(loggedPlayers.isEmpty()) {
            randomPlayer = generatePlayers(1).get(0);
            queries.addPlayer(randomPlayer.getUuid(), randomPlayer.getName());
        } else {
            randomPlayer = getRandomPlayer(loggedPlayers);
        }

        UUID playerUUID = randomPlayer.getUuid();
        String playerName = randomPlayer.getName();

        System.out.println(playerUUID);
        System.out.println(playerName);

        Assertions.assertFalse(queries.getUUIDByName(playerName).isEmpty());
        System.out.println("This player definitely exists");

        queries.removePlayer(playerUUID.toString());

        Assertions.assertThrows(PlayerNotIDLoggedException.class, () -> {
            queries.getNameByUUID(playerUUID.toString());
        });
        System.out.println("This player definitely doesn't exist");

    }

    @Test
    public void insertPlayers() throws IOException {
        List<LoggedPlayer> randomPlayers = generatePlayers(1000);

        randomPlayers.parallelStream().forEach(loggedPlayer -> {
            try {
                queries.addPlayer(loggedPlayer.getUuid(), loggedPlayer.getName());
            } catch (Exception sqlException) {
                sqlException.printStackTrace();
            }
        });

        System.out.println("Inserted " + randomPlayers.size() + " random players.");
    }

    private List<LoggedPlayer> generatePlayers(int number) throws IOException {

        List<LoggedPlayer> randomLoggedPlayers = new ArrayList<>(number);

        List<String> names = Files.lines(Paths.get(RESOURCES_DIR + NAMES_FILENAME)).toList();

        Files.lines(Paths.get(RESOURCES_DIR + NAMES_FILENAME))
                .limit(number)
                .forEach(name ->
            randomLoggedPlayers.add(new LoggedPlayer(UUID.randomUUID(), getRandomName(names)))
        );

        Assertions.assertEquals(number, randomLoggedPlayers.size());

        return randomLoggedPlayers;
    }

    private String getRandomName(List<String> names) {
        Random random = new Random();
        return names.get(random.nextInt(names.size()));
    }

    private LoggedPlayer getRandomPlayer(List<LoggedPlayer> players) {
        Random random = new Random();
        return players.get(random.nextInt(players.size()));
    }
}
