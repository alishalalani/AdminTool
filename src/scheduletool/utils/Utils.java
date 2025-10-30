package scheduletool.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Replacement for gsutils.Utils
 * General utility methods
 */
public class Utils {
    
    /**
     * Parse string to integer, return 0 if invalid
     */
    public static int parse_int(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Parse string to double, return 0.0 if invalid
     */
    public static double parse_double(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Parse string to long, return 0 if invalid
     */
    public static long parse_long(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    
    /**
     * Check if string is null or empty
     */
    public static boolean is_empty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * Check if string is not null and not empty
     */
    public static boolean is_not_empty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Process and log exception
     */
    public static void process_exception(String context, Exception e) {
        Logger.error(context + ": Exception occurred", e);
    }
    
    /**
     * Get exception stack trace as string
     */
    public static String get_stack_trace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * Log memory usage
     */
    public static void memory_usage(String context) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        Logger.info(String.format("%s - Memory: Used=%dMB, Free=%dMB, Total=%dMB, Max=%dMB",
            context,
            usedMemory / (1024 * 1024),
            freeMemory / (1024 * 1024),
            totalMemory / (1024 * 1024),
            maxMemory / (1024 * 1024)
        ));
    }
    
    /**
     * Write string to file
     */
    public static void write_to_file(String filename, StringBuilder content) {
        write_to_file(filename, content.toString());
    }
    
    /**
     * Write string to file
     */
    public static void write_to_file(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
            Logger.info("Written to file: " + filename);
        } catch (IOException e) {
            Logger.error("Failed to write to file: " + filename, e);
        }
    }
    
    /**
     * Safe string comparison (null-safe)
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }
    
    /**
     * Get string value or default if null
     */
    public static String get_or_default(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
    
    /**
     * Null-safe trim
     */
    public static String safe_trim(String value) {
        return value != null ? value.trim() : null;
    }
}

