package me.arifbanai.idLogger.managers;

import me.arifbanai.easypool.DataSourceManager;
import me.arifbanai.easypool.MySQLDataSourceManager;
import me.arifbanai.easypool.SQLiteDataSourceManager;
import me.arifbanai.idLogger.managers.sql.SqlQueries;
import me.arifbanai.idLogger.utils.Config;
import me.arifbanai.idLogger.utils.Database;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.UUID;

public class QueryTests {

    private final DataSourceManager dsm;
    private final SqlQueries sqlQueries;

    private static final String RESOURCES_DIR = "src/test/resources/";
    private static final String CONFIG_FILENAME = "config.yml";
    private static final String UUIDS_FILENAME = "playerUUIDs.txt";

    private QueryTests() throws IOException, SQLException {
        dsm = setupDataSourceManager(System.getProperty("password"));
        sqlQueries = new SqlQueries(dsm);

        sqlQueries.prepareDB();
    }

    public void addPlayer() {
        UUID playerUUID = UUID.fromString("cddf31f9-d3c4-4681-9b1a-2b4df9645645");
        String playerName = "arif240";

    }

    /**
     * Setup the DSM with some RDBMS specified in config
     * @return the DataSourceManager
     */
    private static DataSourceManager setupDataSourceManager(String password) throws IOException {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        File configFile = new File(RESOURCES_DIR + CONFIG_FILENAME);
        InputStream inputStream = new FileInputStream(configFile);

        Config config = yaml.load(inputStream);
        Database db = config.getDb();

        if(config.isUsingSQLite()) {
            return new SQLiteDataSourceManager(RESOURCES_DIR, "vShop3.0");
        } else {
            String host = db.getHost();
            String port = db.getPort();
            String schema = db.getSchema();
            String user = db.getUsername();

            return new MySQLDataSourceManager(host, port, schema, user, password);
        }
    }
}
