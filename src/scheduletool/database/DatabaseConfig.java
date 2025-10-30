package scheduletool.database;

/**
 * Database configuration holder
 */
public class DatabaseConfig {
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    
    public DatabaseConfig(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }
    
    public String getHost() {
        return host;
    }
    
    public String getPort() {
        return port;
    }
    
    public String getDatabase() {
        return database;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getJdbcUrl() {
        return String.format("jdbc:mariadb://%s:%s/%s", host, port, database);
    }
}

