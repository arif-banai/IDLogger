package me.arifbanai.idLogger.managers;

import me.arifbanai.easypool.DataSourceManager;
import me.arifbanai.easypool.enums.DataSourceType;
import me.arifbanai.idLogger.interfaces.AsyncQueries;
import me.arifbanai.idLogger.interfaces.Queries;

/**
 * Represents a generic query manager for some DB implementation.
 * Powered by HikariCP using my EasyPool lib for dynamic datasource types
 *
 * @since 4/26/2020 3:38AM, EST
 * @author Arif Banai
 */
public abstract class QueryManager implements Queries, AsyncQueries {

    protected final DataSourceManager dataSourceManager;
    protected final DataSourceType dataSourceType;

    protected QueryManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
        this.dataSourceType = dataSourceManager.getDataSourceType();
    }
}
