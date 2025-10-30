package scheduletool.database;

/**
 * Replacement for gsutils.MSSQLfactory
 * Factory for creating database connections
 */
public class DatabaseFactory {
    
    private static DatabaseConnection instance = null;
    
    /**
     * Get singleton database connection instance
     */
    public static DatabaseConnection getDatabase() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Reset the database instance (for testing or reconnection)
     */
    public static void reset() {
        if (instance != null) {
            DatabaseConnection.disconnect();
            instance = null;
        }
    }
}

