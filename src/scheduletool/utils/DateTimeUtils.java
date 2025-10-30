package scheduletool.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Replacement for gsutils.DateTimeUtils
 * Utility class for date/time operations, especially Pacific timezone conversions
 */
public class DateTimeUtils {
    
    public static final ZoneId PACIFIC_ZONE = ZoneId.of("America/Los_Angeles");
    public static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * Get current time as formatted string
     */
    public static String get_current_time_string() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }
    
    /**
     * Get current Pacific time as OffsetDateTime
     */
    public static OffsetDateTime get_current_pacific_time() {
        return OffsetDateTime.now(PACIFIC_ZONE);
    }
    
    /**
     * Convert Pacific OffsetDateTime to Local DateTime
     */
    public static LocalDateTime pacific_to_local(OffsetDateTime pacificTime) {
        if (pacificTime == null) {
            return null;
        }
        return pacificTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * Convert Local DateTime to Pacific OffsetDateTime
     */
    public static OffsetDateTime local_to_pacific(LocalDateTime localTime) {
        if (localTime == null) {
            return null;
        }
        return localTime.atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(PACIFIC_ZONE)
                        .toOffsetDateTime();
    }
    
    /**
     * Get Pacific DateTime from epoch seconds
     */
    public static OffsetDateTime get_pacific_date_time_from_seconds(long epochSeconds) {
        return Instant.ofEpochSecond(epochSeconds)
                      .atZone(PACIFIC_ZONE)
                      .toOffsetDateTime();
    }
    
    /**
     * Get date/time string in UTC format for database storage
     */
    public static String get_date_time_string_UTC(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZoneSameInstant(UTC_ZONE)
                       .format(UTC_FORMATTER);
    }
    
    /**
     * Convert OffsetDateTime to formatted string
     */
    public static String format_datetime(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Parse UTC datetime string to OffsetDateTime
     */
    public static OffsetDateTime parse_utc_datetime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDateTime ldt = LocalDateTime.parse(dateTimeString, UTC_FORMATTER);
            return ldt.atOffset(ZoneOffset.UTC);
        } catch (Exception e) {
            Logger.error("Failed to parse datetime: " + dateTimeString, e);
            return null;
        }
    }
    
    /**
     * Get current date in Pacific timezone
     */
    public static LocalDate get_current_pacific_date() {
        return LocalDate.now(PACIFIC_ZONE);
    }
    
    /**
     * Check if a date is today in Pacific timezone
     */
    public static boolean is_today_pacific(LocalDate date) {
        return date != null && date.equals(get_current_pacific_date());
    }
}

