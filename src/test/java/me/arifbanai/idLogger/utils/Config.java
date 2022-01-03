package me.arifbanai.idLogger.utils;


/**
 * It is very important that the fields and getters/setters are named a specific way
 *
 * <br>
 *
 * The variable names must be EXACTLY EQUAL to their YAML counterpart
 *
 * <br><br>
 *
 *
 * All the getters and setters must follow the following pattern:
 *
 * <br>
 *
 * Getters: getUsing....()
 * <br> The method name must use camelCase
 *
 * <br>
 * Setters: setUsing...()
 * <br>
 * <br>
 *
 * If these rules aren't followed precisely you will trigger {@link org.yaml.snakeyaml.error.YAMLException}
 */
public class Config {

    public String dialect;
    public String host;
    public String port;
    public String schema;
    public String username;
    public String password;

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
