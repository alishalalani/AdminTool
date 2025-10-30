package scheduletool.database;

import scheduletool.utils.Logger;
import java.sql.*;

/**
 * Replacement for gsutils.MSSQL
 * Database connection manager for MariaDB
 */
public class DatabaseConnection {
    
    private static Connection connection = null;
    private static DatabaseConfig config = null;
    
    /**
     * Connect to MariaDB database
     */
    public static void connect(String host, String port, String database, String username, String password) throws SQLException {
        config = new DatabaseConfig(host, port, database, username, password);
        connect();
    }
    
    /**
     * Connect using stored configuration
     */
    private static void connect() throws SQLException {
        if (config == null) {
            throw new SQLException("Database configuration not set");
        }
        
        try {
            // Load MariaDB JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");
            
            String jdbcUrl = config.getJdbcUrl();
            Logger.info("Connecting to database: " + jdbcUrl);
            
            connection = DriverManager.getConnection(
                jdbcUrl,
                config.getUsername(),
                config.getPassword()
            );
            
            Logger.info("Successfully connected to database: " + config.getDatabase());
            
        } catch (ClassNotFoundException e) {
            Logger.error("MariaDB JDBC Driver not found", e);
            throw new SQLException("MariaDB JDBC Driver not found", e);
        }
    }
    
    /**
     * Disconnect from database
     */
    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                Logger.info("Database connection closed");
            } catch (SQLException e) {
                Logger.error("Error closing database connection", e);
            } finally {
                connection = null;
            }
        }
    }
    
    /**
     * Execute a SELECT query
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        return executeQuery(new StringBuilder(sql));
    }
    
    /**
     * Execute a SELECT query
     */
    public ResultSet executeQuery(StringBuilder sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            Logger.error("Database connection is not available");
            throw new SQLException("Database connection is not available");
        }
        
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql.toString());
        } catch (SQLException e) {
            Logger.error("Error executing query: " + sql.toString(), e);
            throw e;
        }
    }
    
    /**
     * Execute an INSERT, UPDATE, or DELETE statement
     */
    public int executeUpdate(String sql) throws SQLException {
        return executeUpdate(new StringBuilder(sql));
    }
    
    /**
     * Execute an INSERT, UPDATE, or DELETE statement
     */
    public int executeUpdate(StringBuilder sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            Logger.error("Database connection is not available");
            throw new SQLException("Database connection is not available");
        }
        
        try {
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate(sql.toString());
            statement.close();
            return result;
        } catch (SQLException e) {
            Logger.error("Error executing update: " + sql.toString(), e);
            throw e;
        }
    }
    
    /**
     * Execute a stored procedure or batch of statements
     */
    public boolean execute(String sql) throws SQLException {
        return execute(new StringBuilder(sql));
    }
    
    /**
     * Execute a stored procedure or batch of statements
     */
    public boolean execute(StringBuilder sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            Logger.error("Database connection is not available");
            throw new SQLException("Database connection is not available");
        }
        
        try {
            Statement statement = connection.createStatement();
            boolean result = statement.execute(sql.toString());
            statement.close();
            return result;
        } catch (SQLException e) {
            Logger.error("Error executing statement: " + sql.toString(), e);
            throw e;
        }
    }
    
    /**
     * Close a ResultSet
     */
    public static void close_rs(ResultSet rs) {
        if (rs != null) {
            try {
                Statement stmt = rs.getStatement();
                rs.close();
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                Logger.error("Error closing ResultSet", e);
            }
        }
    }
    
    /**
     * Get the current connection
     */
    public static Connection getConnection() {
        return connection;
    }
    
    /**
     * Check if connected
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Begin transaction
     */
    public static void beginTransaction() throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(false);
        }
    }
    
    /**
     * Commit transaction
     */
    public static void commit() throws SQLException {
        if (connection != null) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Rollback transaction
     */
    public static void rollback() {
        if (connection != null) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                Logger.error("Error rolling back transaction", e);
            }
        }
    }
}

