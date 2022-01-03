package me.arifbanai.idLogger;

//TODO Just some old code, check to see if there is anything useful in here
public class IDLoggerTestDeprecated {
//
//    private static EasyPool pool;
//    private static SqlQueries sqlQueries;
//
//    public static BiMap<UUID, String> playersByUUID;
//
//    private static final String RESOURCES_DIR = "src/test/resources/";
//    private static final String CONFIG_FILENAME = "config.yml";
//    private static final String NAMES_FILENAME = "names.txt";
//
//
//    public IDLoggerTestDeprecated() throws IOException, SQLException {
//        // Set location of hikari properties file to System property
//        // When HikariConfig default constructor is called, the properties file is loaded
//        // see https://github.com/brettwooldridge/HikariCP (ctrl+F system property)
//        System.setProperty("hikaricp.configurationFile", RESOURCES_DIR + "/hikari.properties");
//
//        pool = setupDataSourceManager(System.getProperty("password"));
//        sqlQueries = new SqlQueries(pool);
//
//        sqlQueries.prepareDB();
//
//        Assertions.assertTrue(initMap());
//    }
//
//    @AfterAll
//    static void onDisable() {
//        pool.close();
//    }
//
//    @Test
//    public void findUUIDsFromNamesFile() throws IOException {
//        int expected = 1000;
//
//        AtomicInteger foundUUIDs = new AtomicInteger();
//
//        Files.lines(Paths.get(RESOURCES_DIR + NAMES_FILENAME))
//            .forEach(name -> {
//                try {
//                    UUID uuid = doUUIDLookup(name);
//                    System.out.println("UUID for " + name + " is " + uuid.toString());
//                    foundUUIDs.getAndIncrement();
//                } catch (SQLException sqlException) {
//                    sqlException.printStackTrace();
//                } catch (PlayerNotIDLoggedException e) {
//                    System.out.println("UUID for " + name + " not found");
//                }
//
//            });
//
//        Assertions.assertEquals(expected, foundUUIDs.get());
//
//    }
//
//    @Test
//    public void getNameFromUUID() throws SQLException, PlayerNotIDLoggedException {
//        UUID playerUUID = UUID.fromString("cddf31f9-d3c4-4681-9b1a-2b4df9645645");
//        String expectedPlayerName = "arif240";
//
//        String actualPlayerName = doNameLookup(playerUUID);
//
//        Assertions.assertEquals(expectedPlayerName, actualPlayerName);
//        System.out.println("UUID: " + playerUUID);
//        System.out.println("Name: " + actualPlayerName);
//
//        System.out.println("Expected Name: " + expectedPlayerName);
//    }
//
//    @Test
//    public void getUUIDFromName() throws SQLException, PlayerNotIDLoggedException {
//        String playerName = "arif240";
//        UUID expectedPlayerUUID = UUID.fromString("cddf31f9-d3c4-4681-9b1a-2b4df9645645");
//
//        UUID actualPlayerUUID = doUUIDLookup(playerName);
//
//        Assertions.assertEquals(expectedPlayerUUID, actualPlayerUUID);
//        System.out.println("Name: " + playerName);
//        System.out.println("UUID: " + actualPlayerUUID.toString());
//
//        System.out.println("Expected UUID: " + actualPlayerUUID.toString());
//    }
//
//    @Test
//    public void insertPlayers() throws IOException {
//        List<LoggedPlayer> randomPlayers = generatePlayers(1000);
//
//        randomPlayers.parallelStream().forEach(loggedPlayer -> {
//            try {
//                sqlQueries.addPlayer(loggedPlayer.getUuid(), loggedPlayer.getName());
//            } catch (SQLException sqlException) {
//                sqlException.printStackTrace();
//            }
//        });
//
//        System.out.println("Inserted " + randomPlayers.size() + " random players.");
//    }
//
//    private List<LoggedPlayer> generatePlayers(int number) throws IOException {
//
//        List<LoggedPlayer> randomLoggedPlayers = new ArrayList<>(number);
//
//        Files.lines(Paths.get(RESOURCES_DIR + NAMES_FILENAME))
//                .limit(number)
//                .forEach(name ->
//            randomLoggedPlayers.add(new LoggedPlayer(UUID.randomUUID(), name))
//        );
//
//        Assertions.assertEquals(number, randomLoggedPlayers.size());
//
//        return randomLoggedPlayers;
//    }
//
//    public String doNameLookup(UUID playerUUID) throws SQLException, PlayerNotIDLoggedException {
//        if(playersByUUID.containsKey(playerUUID)) {
//            return playersByUUID.get(playerUUID);
//        } else {
//
//            String name = sqlQueries.getNameByUUID(playerUUID.toString());
//
//            return playersByUUID.put(playerUUID, name);
//        }
//
//    }
//
//    public UUID doUUIDLookup(String playerName) throws SQLException, PlayerNotIDLoggedException {
//        BiMap<String, UUID> playersByName = playersByUUID.inverse();
//        if(playersByName.containsKey(playerName)) {
//            return playersByName.get(playerName);
//        } else {
//            String playerUUID = sqlQueries.getUUIDByName(playerName);
//
//            return UUID.fromString(playerUUID);
//        }
//    }
//
//    /**
//     * Setup the DSM with some RDBMS specified in config
//     * @return the DataSourceManager
//     */
//    private static DataSourceManager setupDataSourceManager(String password) throws IOException {
//        Yaml yaml = new Yaml(new Constructor(Config.class));
//        File configFile = new File(RESOURCES_DIR + CONFIG_FILENAME);
//        InputStream inputStream = new FileInputStream(configFile);
//
//        Config config = yaml.load(inputStream);
//        Database db = config.getDb();
//
//        if(config.isUsingSQLite()) {
//            return new SQLiteDataSourceManager(RESOURCES_DIR, "vShop3.0");
//        } else {
//            String host = db.getHost();
//            String port = db.getPort();
//            String schema = db.getSchema();
//            String user = db.getUsername();
//
//            return new MySQLDataSourceManager(host, port, schema, user, password);
//        }
//    }
//
//    private boolean initMap() throws SQLException {
//        List<LoggedPlayer> loggedPlayers = sqlQueries.getAllLoggedPlayers();
//        playersByUUID = HashBiMap.create(loggedPlayers.size());
//
//        playersByUUID.putAll(loggedPlayers.stream().collect(Collectors.toMap(LoggedPlayer::getUuid,
//                LoggedPlayer::getName)));
//
//
//        return (playersByUUID != null);
//    }
//
//
}
