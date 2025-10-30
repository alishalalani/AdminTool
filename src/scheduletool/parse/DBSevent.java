/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduletool.parse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Map;

/**
 *
 * @author samla
 */
public class DBSevent
    {
    //---------------------------------------------------------------------------------------------
    private static final DateTimeFormatter mm_dd_yyyy_formatter = DateTimeFormatter.ofPattern ("MM/dd/yyyy");
    //---------------------------------------------------------------------------------------------
    String date;
    String time;
    LocalDate local_date;
    LocalTime local_time;
    OffsetDateTime date_time;
    String rotation_number;
    String away_team;
    String away_pitcher;
    String home_team;
    String home_pitcher;
    boolean neutral;
    boolean tba;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    @Override
    public String toString ()
        {
        return ("DBSevent (" + date + ")(" + local_date + ")(" + time + ")(" + local_time + ")(" + date_time + ")(" + rotation_number + ")(" + away_team + ")(" + away_pitcher + ")(" + home_team + ")(" + home_pitcher + ")(" + neutral + ")");
        }
    //---------------------------------------------------------------------------------------------
    public void set_time ()
        {
        try
            {
            if (time.equals ("TBA"))
                {
                tba = true;
                time = "3:00a";
                }
            Map<Long, String> ampmStrings = Map.of (0L, "a", 1L, "p");
            DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder ()
                    .appendPattern ("hh:mm")
                    .appendText (ChronoField.AMPM_OF_DAY, ampmStrings)
                    .toFormatter ();
            local_time = LocalTime.parse ((time.length () == 5 ? '0' : "") + time, timeFormatter);
            local_date = LocalDate.parse (date + "/" + LocalDate.now ().getYear (), mm_dd_yyyy_formatter);
            date_time = get_OffsetDateTime (local_time.atDate (local_date));
            }
        catch (Exception e)
            {
            System.out.println ("Exception (" + date + ")(" + time + ")(" + rotation_number + ")(" + e + ")");
            e.printStackTrace ();
            }
        }
    //---------------------------------------------------------------------------------------------
    public static OffsetDateTime get_OffsetDateTime (LocalDateTime pacific_date_time)
        {
        ZoneId zoneId = ZoneId.of ("America/Los_Angeles");
        ZonedDateTime zonedDateTime = ZonedDateTime.of (pacific_date_time, zoneId);
        return (zonedDateTime.toOffsetDateTime ());
        }
    }
