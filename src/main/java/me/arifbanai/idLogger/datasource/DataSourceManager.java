package me.arifbanai.idLogger.datasource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import me.arifbanai.idLogger.IDLogger;
import me.arifbanai.idLogger.managers.ConfigManager;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.sql.ConnectionPoolDataSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * <p>Manages access to a {@link ComboPooledDataSource} reference.
 * Provides a reference to the underlying {@link DataSourceType}.</p>
 *
 * @since 4/26/2020 4:05AM, EST
 * @see ComboPooledDataSource
 * @see DataSourceType
 * @author Arif Banai
 */

public class DataSourceManager {

    private final ComboPooledDataSource cpds;
    private final DataSourceType dataSourceType;

    /**
     * <p>Sets up and configures a {@link ComboPooledDataSource}.
     * Utilizes a {@link Properties} file to store driver class paths.</p>
     *
     * @param plugin Reference to the main {@link IDLogger} class
     */
    public DataSourceManager(final IDLogger plugin) {

        String path = plugin.getDataFolder().getAbsolutePath();

        String dbPropertiesPath = path + "c3po.properties";
        Properties dbProperties = new Properties();

        try {
            dbProperties.load(new FileInputStream(dbPropertiesPath));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Cannot read c3po.properties. Shutting down...");
            e.printStackTrace();
            plugin.getPluginLoader().disablePlugin(plugin);
        }

        String dbType;
        String driverClassName;
        String factoryClassName = "";
        String host;
        String db;
        ConnectionPoolDataSource pds;

        ConfigManager configMan = plugin.getConfigManager();
        if(configMan.usingMySQL()) {
            dataSourceType = DataSourceType.MYSQL;
            driverClassName = dbProperties.getProperty("mysql-driver-class");
            dbType = "mysql";
            host = configMan.getHost();
            db = configMan.getDatabase();
            factoryClassName = dbProperties.getProperty("mysql-factory-class");
            pds = new MysqlConnectionPoolDataSource();
        } else {
            dataSourceType = DataSourceType.SQLITE;
            driverClassName = dbProperties.getProperty("sqlite-driver-class");
            dbType = "sqlite";
            db = "IDLogger.db";
            host = plugin.getDataFolder().toPath().toString();
            pds = new SQLiteConnectionPoolDataSource();

            File file = new File(plugin.getDataFolder(), db);

            if (!(file.exists())) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().severe("Unable to create SQLite database!");
                    e.printStackTrace();
                    plugin.getPluginLoader().disablePlugin(plugin);
                }
            }
        }

        String url = "jdbc:" + dbType + "://" + host + "/" + db;

        cpds = new ComboPooledDataSource();

        try {
            cpds.setDriverClass(driverClassName);
            cpds.setConnectionPoolDataSource(pds);
        } catch (PropertyVetoException e) {
            plugin.getLogger().severe("Unable to set Driver class/set ConnectionPoolDataSource. Shutting down...");
            e.printStackTrace();
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        cpds.setForceUseNamedDriverClass(true);

        //If using MySQL, we must set User and Password.
        //TODO Does factory class location need to be set explicitly?
        if(dataSourceType.equals(DataSourceType.MYSQL)) {
            cpds.setFactoryClassLocation(factoryClassName);

            cpds.setUser(configMan.getUsername());
            cpds.setPassword(configMan.getPassword());
        }

        cpds.setJdbcUrl(url);

        cpds.setMinPoolSize(1);
        cpds.setInitialPoolSize(2);
        cpds.setMaxPoolSize(10);

        cpds.setMaxStatements(40);
        cpds.setMaxStatementsPerConnection(4);

        cpds.setMaxIdleTime(86400);
    }

    /**
     * <p>Gets a connection from the {@link ComboPooledDataSource}.</p>
     *
     * @return {@link java.sql.Connection}
     * @throws SQLException if a database access error occurs
     * @see ComboPooledDataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }

    /**
     * <p>Get the underlying datasource type</p>
     *
     * @return {@link DataSourceType}
     */
    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    /**
     * <p>Closes the DataSource pool</p>
     * @see ComboPooledDataSource#close()
     */
    public void close() {
        cpds.close();
    }
}
