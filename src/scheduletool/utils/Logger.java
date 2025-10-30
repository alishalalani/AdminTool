package scheduletool.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Replacement for gsutils.Debug
 * Simple logging utility for debugging and tracking application flow
 */
public class Logger {
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static boolean debugEnabled = true;
    
    /**
     * Print a debug message with timestamp
     */
    public static void print(Object message) {
        if (debugEnabled) {
            String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
            System.out.println("[" + timestamp + "] " + message);
        }
    }
    
    /**
     * Print a debug message with timestamp and thread info
     */
    public static void printt(Object message) {
        if (debugEnabled) {
            String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + timestamp + "][" + threadName + "] " + message);
        }
    }
    
    /**
     * Print an error message
     */
    public static void error(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        System.err.println("[" + timestamp + "][ERROR] " + message);
    }
    
    /**
     * Print an error message with exception
     */
    public static void error(String message, Throwable t) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        System.err.println("[" + timestamp + "][ERROR] " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }
    
    /**
     * Print an info message
     */
    public static void info(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        System.out.println("[" + timestamp + "][INFO] " + message);
    }
    
    /**
     * Enable or disable debug logging
     */
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }
    
    /**
     * Check if debug is enabled
     */
    public static boolean isDebugEnabled() {
        return debugEnabled;
    }
}

