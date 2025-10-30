/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduletool.table;

import gsutils.Debug;
import gsutils.MSSQL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.TreeMap;
import scheduletool.Main;

/**
 *
 * @author samla
 */
public class ScheduleRecord
    {
    static String class_name = "ScheduleRecord";
    //---------------------------------------------------------------------------------------------
    private int            id;
    private LocalDate      date;
    private OffsetDateTime created_timestamp;
    //---------------------------------------------------------------------------------------------
    static public final TreeMap <Integer, ScheduleRecord> records = new TreeMap <> (Collections.reverseOrder()); // key is Schedule ID
    //---------------------------------------------------------------------------------------------
    static public void read_table ()
        {
        String function = class_name + ".read_table";
        if (Debug.debug)
            System.out.println (function + ":  Reading table...");
        String sql =   "SELECT s.id"
                   + "\n     , s.date"
                   + "\n     , s.timestamp"
                   + "\n  FROM Schedule AS s"
                   + "\n WHERE date > DATEADD(dd, -14, DATEDIFF(dd, 0, GETDATE()))"
                   + "\n ORDER BY s.id"
                   ;
        try
            {
            ResultSet rs = Main.db.executeQuery (sql);
            if (rs != null)
                {
                records.clear ();
                while (rs.next ())
                    {
                    ScheduleRecord record = new ScheduleRecord ();
                    record.id   = rs.getInt ("id");
                    record.date = rs.getDate ("date").toLocalDate ();
                    record.created_timestamp = OffsetDateTime.ofInstant (rs.getTimestamp ("timestamp").toInstant (), ZoneOffset.UTC);
                    records.put (record.getId (), record);
                    }
                }
            MSSQL.close_rs (rs);
            }
        catch (Exception e)
            {
            System.out.println (function + ":  " + e);
            e.printStackTrace ();
            }
        if (Debug.debug)
            System.out.println (function + ":  Done reading table...(" + records.size () + ") records");
        }
    //---------------------------------------------------------------------------------------------
    public int getId ()
        {
        return id;
        }
    //---------------------------------------------------------------------------------------------
    public LocalDate getDate ()
        {
        return date;
        }
    //---------------------------------------------------------------------------------------------
    public OffsetDateTime getCreated_timestamp ()
        {
        return created_timestamp;
        }
    //---------------------------------------------------------------------------------------------
    static public ScheduleRecord get_schedule_record (int schedule_id)
        {
        return records.get (schedule_id);
        }
    }
