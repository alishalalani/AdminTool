/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool.parse;

import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.Utils;
import gsutils.data.Category;
import gsutils.data.Category_Key;
import gsutils.data.Category_Type;
import gsutils.data.Event;
import gsutils.data.Event_Key;
import gsutils.data.League;
import gsutils.data.League_Equivalent;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.swing.JFrame;
import scheduletool.Main;

/**
 *
 * @author samla
 */
public class ParseDialog extends javax.swing.JDialog
    {
    enum DataType
        {
        GET_DATE,
        GET_TIME,
        GET_AWAY_ROTATION_NUMBER,
        GET_HOME_ROTATION_NUMBER,
        GET_AWAY_TEAM,
        GET_HOME_TEAM,
        OTHER
        }
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public ParseDialog (java.awt.Frame parent, boolean modal)
        {
        super (parent, modal);
        initComponents ();
        }
    //---------------------------------------------------------------------------------------------
    private void dbs4 ()
        {
        String data = data_TextArea.getText ();
        String split [] = data.split ("\n");
        DataType data_type = DataType.GET_DATE;
        String main_header_string      = "";
        String old_main_header_string  = "";
        String secondary_header_string = "";
        String tertiary_header_string  = "";
        boolean print_header = false;
        League league = null;
        int new_league_id = 0;
        DBSevent dbs_event = new DBSevent ();
        LocalDate category_date = null;
        StringBuilder sql = initialize_sql ();

        for (int line_index = 0; line_index < split.length; line_index++)
            {
            String line = split [line_index];
            if (line.startsWith ("<tr"))
                {
                if (line.contains ("\"tr"))
                    {
                    //---------------------------------------------------------------------------------------------
                    // Header
                    //---------------------------------------------------------------------------------------------
                    boolean main_header = line.contains ("class=\"top1\"");
                    //Debug.print (line);
                    int index = line.indexOf ("<tr ");
                    line = line.substring (index);
                    index = line.indexOf ('>');
                    line = line.substring (index+1);
                    index = line.indexOf ('>');
                    if (index > 0)
                        {
                        line = line.substring (index+1);
                        int index2 = line.indexOf ('<');
                        if (index2 > 0)
                            {
                            print_header = true;
                            String value = line.substring (0, index2);
                            if (main_header)
                                {
                                main_header_string = value;
                                secondary_header_string = "";
                                }
                            else if (secondary_header_string.length () == 0)
                                {
                                secondary_header_string = value;
                                tertiary_header_string = "";
                                }
                            else if (tertiary_header_string.length () == 0)
                                tertiary_header_string += value;
                            else
                                tertiary_header_string += "\n" + value;
                            }
                        }
                    data_type = DataType.GET_DATE;
                    }
                else if (data_type == DataType.GET_HOME_TEAM)
                    dbs_event.neutral = true;
                else
                    data_type = DataType.GET_TIME;
                }
            else
                {
                if (line.startsWith ("<td align=\"right\" width=\"10\"><table ")) // Neutral
                    dbs_event.neutral = true;
                else if (   league != null
                         && league.getId () == gsutils.data.League.MLB
                         && (   data_type == DataType.GET_AWAY_TEAM
                             || data_type == DataType.GET_HOME_TEAM)
                         && line.startsWith ("<td><table ")) // MLB team and pitcher
                    {
                    line_index += 2;
                    line = split [line_index];
                    String value = get_value (line);
                    if (data_type == DataType.GET_AWAY_TEAM)
                        {
                        dbs_event.away_team = value;
                        line_index++;
                        line = split [line_index];
                        value = get_value (line);
                        dbs_event.away_pitcher = value;
                        data_type = DataType.OTHER;
                        }
                    else
                        {
                        dbs_event.home_team = value;
                        line_index++;
                        line = split [line_index];
                        value = get_value (line);
                        dbs_event.home_pitcher = value;
                        data_type = DataType.OTHER;
                        System.out.println ("(" + dbs_event + ")");
                        dbs_event = new DBSevent ();
                        }
                    }
                else if (line.contains ("class=\"print")) // Data
                    {
                    //---------------------------------------------------------------------------------------------
                    // Data
                    //---------------------------------------------------------------------------------------------
                    if (print_header)
                        {
                        if (main_header_string.length () == 0)
                            main_header_string = old_main_header_string;
                        if (main_header_string.length () > 0)
                            main_header_string = main_header_string.replaceAll ("  -  ", " - ");
                        if (secondary_header_string != null && secondary_header_string.length () > 0)
                            secondary_header_string = secondary_header_string.replaceAll ("  -  ", " - ");
                        if (tertiary_header_string != null && tertiary_header_string.length () > 0)
                            tertiary_header_string = tertiary_header_string.replaceAll ("  -  ", " - ");
                        if (secondary_header_string != null && secondary_header_string.startsWith ("MiLB Triple-A"))
                            {
                            int index = secondary_header_string.indexOf (" - ");
                            if (index > 0)
                                secondary_header_string = secondary_header_string.substring (index + 3);
                            }
                        System.out.println ("\n--------------------------------------------------------------------------------------");
                        System.out.println ("HEADER1***" + main_header_string
                                            + (secondary_header_string.length () > 0
                                               ? "\nHEADER2***" + secondary_header_string
                                                 + (tertiary_header_string.length () > 0
                                                    ? "\nHEADER3***" + tertiary_header_string
                                                    : "")
                                               : ""));
                        System.out.println ("--------------------------------------------------------------------------------------");
                        print_header = false;
                        if (main_header_string.length () > 0)
                            {
                            old_main_header_string = main_header_string;
                            int dash_index = main_header_string.indexOf (" - ");
                            String league_name = main_header_string.substring (0, dash_index);

//if (main_header_string.contains ("MLB"))
//    System.out.println ("debug");

                            League_Equivalent league_equivalent = get_league_equivalent (main_header_string);
                            System.out.println ("League_Equivalent for (" + league_name + ")(" + league_equivalent + ")");
                            league = (league_equivalent == null ? null : league_equivalent.getLeague ());
                            System.out.println ("League (" + league + ")" + (league == null ? " for (" + league_name + ")(" + main_header_string + ")" : ""));

                            category_date = convert_to_LocalDate (main_header_string.substring (dash_index + 3));
                            System.out.println ("======================================================================================");
                            System.out.println ("LocalDate (" + category_date + ")");
                            if (league != null)
                                {
                                Category_Type category_type = Category_Type.TEAM;
                                new_league_id = league.getId ();
                                if (new_league_id == League.CFB && secondary_header_string.contains ("FCS"))
                                    new_league_id = League.FCS;
                                if (secondary_header_string.contains ("WRITE-IN GAMES"))
                                    {
                                    category_type = Category_Type.WRITE_IN_GAMES;
                                    switch (new_league_id)
                                        {
                                        case gsutils.data.League.NFL -> new_league_id = 104;
                                        case gsutils.data.League.CFB -> new_league_id = 106;
                                        case gsutils.data.League.NBA -> new_league_id = 110;
                                        case gsutils.data.League.CBK -> new_league_id = 113;
                                        case gsutils.data.League.MLB -> new_league_id = 118;
                                        case gsutils.data.League.CBB -> new_league_id = 121;
                                        case gsutils.data.League.NHL -> new_league_id = 123;
                                        case gsutils.data.League.CFL -> new_league_id = 129;
                                        case gsutils.data.League.FCS -> new_league_id = 151;
                                        case gsutils.data.League.WNBA -> new_league_id = 127;
                                        }
                                    }
                                else if (secondary_header_string.contains ("GRAND SALAMI"))
                                    {
                                    category_type = Category_Type.GRAND_SALAMI;
                                    switch (new_league_id)
                                        {
                                        case gsutils.data.League.MLB -> new_league_id = 120;
                                        case gsutils.data.League.NHL -> new_league_id = 125;
                                        }
                                    }
                                else if (secondary_header_string.contains ("SERIES PRICES"))
                                    {
                                    category_type = Category_Type.SERIES_PRICE;
                                    switch (new_league_id)
                                        {
                                        case gsutils.data.League.NBA -> new_league_id = 111;
                                        case gsutils.data.League.MLB -> new_league_id = 119;
                                        case gsutils.data.League.NHL -> new_league_id = 124;
                                        }
                                    }
                                else if (secondary_header_string.contains ("IN-GAME LINES"))
                                    {
                                    category_type = Category_Type.IN_GAME_LINES;
                                    switch (new_league_id)
                                        {
                                        case gsutils.data.League.NFL -> new_league_id = 103;
                                        case gsutils.data.League.CFB -> new_league_id = 105;
                                        case gsutils.data.League.NBA -> new_league_id = 109;
                                        case gsutils.data.League.CBK -> new_league_id = 112;
                                        case gsutils.data.League.MLB -> new_league_id = 117;
                                        case gsutils.data.League.NHL -> new_league_id = 122;
                                        case gsutils.data.League.CFL -> new_league_id = 128;
                                        case gsutils.data.League.FCS -> new_league_id = 150;
                                        case gsutils.data.League.WNBA -> new_league_id = 126;
                                        }
                                    }
                                else if (new_league_id == gsutils.data.League.MILB)
                                    {
                                    if (secondary_header_string.contains ("International"))
                                        new_league_id = gsutils.data.League.MILB_INTERNATIONAL;
                                    else if (secondary_header_string.contains ("Pacific"))
                                        new_league_id = gsutils.data.League.MILB_PACIFIC;
                                    }
                                else if (new_league_id == gsutils.data.League.FIGHTING)
                                    {
                                    if (secondary_header_string.contains ("UFC"))
                                        new_league_id = gsutils.data.League.FIGHTING_UFC;
                                    else if (secondary_header_string.toUpperCase ().contains ("BOXING"))
                                        new_league_id = gsutils.data.League.FIGHTING_BOXING;
                                    else if (secondary_header_string.contains ("PFL"))
                                        new_league_id = gsutils.data.League.FIGHTING_PFL;
                                    else if (secondary_header_string.toUpperCase ().contains ("WRESTLING"))
                                        new_league_id = gsutils.data.League.FIGHTING_WRESTLING;
                                    else
                                        new_league_id = gsutils.data.League.FIGHTING_OTHER;
                                    }
                                else if (new_league_id == gsutils.data.League.TENNIS)
                                    {
                                    if (secondary_header_string.toUpperCase ().contains ("WOMEN"))
                                        new_league_id = gsutils.data.League.TENNIS_WOMENS;
                                    else if (secondary_header_string.toUpperCase ().contains ("MEN"))
                                        new_league_id = gsutils.data.League.TENNIS_MENS;
                                    }
                                else if (new_league_id == gsutils.data.League.GOLF)
                                    {
                                    if (secondary_header_string.toUpperCase ().contains ("PGA"))
                                        new_league_id = gsutils.data.League.GOLF_PGA;
                                    else if (secondary_header_string.toUpperCase ().contains ("EURO"))
                                        new_league_id = gsutils.data.League.GOLF_EUROPEAN;
                                    }
                                else if (new_league_id == gsutils.data.League.AUTO)
                                    {
                                    if (secondary_header_string.toUpperCase ().contains ("NASCAR"))
                                        new_league_id = gsutils.data.League.AUTO_NASCAR;
                                    else if (secondary_header_string.toUpperCase ().contains ("FORMULA 1"))
                                        new_league_id = gsutils.data.League.AUTO_FORMULA_1;
                                    else if (secondary_header_string.toUpperCase ().contains ("INDYCAR"))
                                        new_league_id = gsutils.data.League.AUTO_INDYCAR;
                                    else if (secondary_header_string.toUpperCase ().contains ("XFINITY"))
                                        new_league_id = gsutils.data.League.AUTO_XFINITY;
                                    else if (secondary_header_string.toUpperCase ().contains ("TRUCKS"))
                                        new_league_id = gsutils.data.League.AUTO_TRUCKS;
                                    }

                                Category_Key category_key = new Category_Key (category_date, new_league_id, secondary_header_string);
                                Category_Key original_category_key = category_key;
                                Category category = Main.schedule.getCategories_by_key ().get (category_key);
                                boolean process = false;
                                if (category == null && tertiary_header_string.length () > 0)
                                    {
                                    String search_string = " -  - ";
                                    int index = tertiary_header_string.indexOf (search_string);
                                    if (index > 0)
                                        process = true;
                                    else
                                        {
                                        search_string = " - BOTTOM TEAM IS NOT NECESSARILY HOME - ";
                                        index = tertiary_header_string.indexOf (search_string);
                                        if (index > 0)
                                            process = true;
                                        }
                                    if (process)
                                        {
                                        String header = tertiary_header_string.substring (index + search_string.length ());
                                        category_key = new Category_Key (category_date, new_league_id, header);
                                        category = Main.schedule.getCategories_by_key ().get (category_key);
                                        }
                                    }
                                else if (category == null && secondary_header_string.length () > 0)
                                    {
                                    String search_string = " -  - ";
                                    int index = secondary_header_string.indexOf (search_string);
                                    if (index > 0)
                                        process = true;
                                    else
                                        {
                                        search_string = " - BOTTOM TEAM IS NOT NECESSARILY HOME - ";
                                        index = tertiary_header_string.indexOf (search_string);
                                        if (index > 0)
                                            process = true;
                                        }
                                    if (process)
                                        {
                                        String header = secondary_header_string.substring (index + search_string.length ());
                                        category_key = new Category_Key (category_date, new_league_id, header);
                                        category = Main.schedule.getCategories_by_key ().get (category_key);
                                        }
                                    }
                                //LocalDate original_category_date = category_date;
                                //category_date = (category_date.compareTo (LocalDate.now ()) > 0 ? LocalDate.now () : category_date);
                                category_date = LocalDate.now ().minusDays (3);
                                LocalDate end_date = (category_date.compareTo (LocalDate.now ()) > 0 ? category_date : LocalDate.now ().plusDays (3));
                                while (category == null && category_date.isBefore (end_date))
                                    {
                                    category_key = new Category_Key (category_date, new_league_id, secondary_header_string);
                                    category = Main.schedule.getCategories_by_key ().get (category_key);
                                    category_date = category_date.plusDays (1);
                                    }
                                System.out.println ("Category (" + category + ")" + (category == null ? "(" + league + ")" : ""));
                                if (category == null)
                                    add_category (sql, original_category_key, new_league_id, category_type);
                                }
//                            try
//                                {
//                                String date_string = main_header_string.substring (dash_index + 3);
//                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("E, MMMM dd, yyyy");
//                                System.out.println (simpleDateFormat.parse (date_string).toInstant().atZone (TimeZone.getTimeZone ("America/Los Angeles").toZoneId ()).toLocalDate());
//                                }
//                            catch (Exception e)
//                                {
//                                System.out.println ("Exception:  " + e);
//                                e.printStackTrace ();
//                                }

                            System.out.println ("--------------------------------------------------------------------------------------");
                            }
                        }

                    main_header_string      = "";
                    secondary_header_string = "";

                    String value = get_value (line);
                    switch (data_type)
                        {
                        case GET_DATE:
                            //System.out.println ("DATE (" + value + ")");
                            dbs_event.date = value;
                            data_type = DataType.GET_AWAY_ROTATION_NUMBER;
                            break;
                        case GET_AWAY_ROTATION_NUMBER:
                            //System.out.println ("AWAY ROTATION# (" + value + ")");
                            dbs_event.rotation_number = value;
//if (Utils.parse_int (dbs_event.rotation_number) == 299)
//    System.out.println ("debug");
                            data_type = DataType.GET_AWAY_TEAM;
                            break;
                        case GET_AWAY_TEAM:
                            //System.out.println ("AWAY TEAM (" + value + ")");
                            dbs_event.away_team = value;
                            if (dbs_event.away_team.equals ("&nbsp;"))
                                dbs_event.away_team = "";
                            data_type = DataType.OTHER;
                            break;
                        case GET_TIME:
                            //System.out.println ("TIME (" + value + ")");
                            dbs_event.time = value;
                            dbs_event.set_time ();
                            data_type = DataType.GET_HOME_ROTATION_NUMBER;
                            break;
                        case GET_HOME_ROTATION_NUMBER:
                            //System.out.println ("HOME ROTATION# (" + value + ")");
                            data_type = DataType.GET_HOME_TEAM;
                            break;
                        case GET_HOME_TEAM:
                            //System.out.println ("HOME TEAM (" + value + ")");
//if (Utils.parse_int (dbs_event.rotation_number) == 210705)
//    System.out.println ("debug");
                            dbs_event.home_team = value;
                            if (dbs_event.home_team.equals ("&nbsp;"))
                                dbs_event.home_team = "";
                            //data_type = DataType.OTHER;
                            System.out.println ("(" + dbs_event + ")");

                            int event_number = Utils.parse_int (dbs_event.rotation_number);
                            Event_Key event_key = new Event_Key (category_date, event_number);
                            Event event = Main.schedule.getEvents_by_date_and_number ().get (event_key);
                            if (event == null && dbs_event.date.length () == 5)
                                {
                                event_key = new Event_Key (dbs_event.local_date, event_number);
                                event = Main.schedule.getEvents_by_date_and_number ().get (event_key);
                                }
                            System.out.println ("Event (" + event + ")");
                            System.out.println ("--------------------------------------------------------------------------------------");
                            if (event == null)
                                {
                                if (league == null)
                                    System.out.println ("debug");
                                else
                                    {
                                    while (league.getId () != league.getMain_league_id ())
                                        league = League.getLeagues ().get (league.getMain_league_id ());
                                    int main_league_id = league.getId ();

                                    OffsetDateTime event_time = dbs_event.date_time;
                                    add_event (sql, event_key, new_league_id, event_time, main_league_id, dbs_event);
                                    }
                                }

                            while (++line_index < split.length)
                                {
                                line = split[line_index];
                                if (line.startsWith ("<tr") && line.contains ("\"tr"))
                                    break;
                                }
                            line_index--;
                            data_type = DataType.GET_DATE;
                            dbs_event = new DBSevent ();
                            break;
                        case OTHER:
                            break;
                        }
                    }
                }
            }
        Debug.print (sql);
        }
    //---------------------------------------------------------------------------------------------
    private void close ()
        {
        setVisible (false);
        dispose ();
        }
    //---------------------------------------------------------------------------------------------
    private String get_value (String line)
        {
        String value = "";
        line = line.replaceAll ("<font color=\"red\">", "");
        int index = line.indexOf ('>');
        if (index > 0)
            {
            line = line.substring (index+1);
            int index2 = line.indexOf ('<');
            if (index2 > 0)
                {
                value = line.substring (0, index2);
                }
            }
        return (value);
        }
    //---------------------------------------------------------------------------------------------
    private League_Equivalent get_league_equivalent (String line)
        {
        //TreeMap <String, League_Equivalent> league_equivalents_by_name = League_Equivalent.getLeague_equivalents_by_name ();
        String league_name = line.substring (0, line.indexOf (" - "));
        League_Equivalent league_equivalent = League_Equivalent.get_league_equivalents_by_name (league_name);
        return (league_equivalent);
        }
    //---------------------------------------------------------------------------------------------
    private League_Equivalent get_league_equivalent_old (String line)
        {
        TreeMap <String, League_Equivalent> league_equivalents_by_name = League_Equivalent.getLeague_equivalents_by_name ();
        String league_name = line.substring (0, line.indexOf (" - "));
        League_Equivalent league_equivalent = league_equivalents_by_name.get (league_name);
        return (league_equivalent);
        }

    //---------------------------------------------------------------------------------------------
    public static String getCurrentTimezoneOffset_paific ()
        {
        //TimeZone tz             = TimeZone.getDefault ();
        TimeZone tz             = TimeZone.getTimeZone ("America/Los Angeles");
        Calendar cal            = GregorianCalendar.getInstance (tz);
        int      offsetInMillis = tz.getOffset (cal.getTimeInMillis ());

        String offset = String.format ("%02d:%02d", Math.abs (offsetInMillis / 3600000), Math.abs ((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;

        return offset.replaceAll (":", "");
        }
    //---------------------------------------------------------------------------------------------
    public static LocalDate convert_to_LocalDate (String date_string)
        {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("EEEE, MMMM d, yyyy");

        try
            {
            LocalDate date = LocalDate.parse (date_string, formatter);
            return date;
            }
        catch (DateTimeParseException e)
            {
            System.out.println ("Invalid date format: " + date_string);
            return null;
            }
        }
    //---------------------------------------------------------------------------------------------
    private StringBuilder initialize_sql ()
        {
        StringBuilder sql = new StringBuilder ()
            .append ("\n")
            .append ("DECLARE @schedule_id    SMALLINT;\n")
            .append ("DECLARE @sc_sequence    SMALLINT;\n")
            .append ("DECLARE @category_id    INT;\n")
            .append ("DECLARE @event_id       INT;\n")
            .append ("DECLARE @event_item_id  INT;\n")
            .append ("DECLARE @league_team_id INT;\n")
            .append ("DECLARE @league_prop_id INT;\n")
            .append ("DECLARE @current_league_team_id    INT;\n")
            .append ("DECLARE @event_item_league_team_id INT;\n")
            .append ("\n")
            .append ("SELECT @schedule_id = id FROM Schedule AS sch WHERE sch.timestamp = (SELECT MAX(timestamp) FROM Schedule AS sch1 WHERE sch1.date=(SELECT value FROM GlobalValues WHERE name1='schedule' AND name2='current'))\n\n")
            .append ("SELECT @sc_sequence = MAX(sequence) FROM Schedule_Category AS sc WHERE sc.schedule_id = @schedule_id;\n\n")
            ;
        return (sql);
        }
    //---------------------------------------------------------------------------------------------
    private void add_category (StringBuilder sql, Category_Key original_category_key, int new_league_id, Category_Type category_type)
        {
        sql.append ("SET @category_id = NULL;\n")
           .append ("SELECT @category_id = id FROM Category WHERE date='")
           .append (DateTimeUtils.get_date_string (original_category_key.getDate ()))
           .append ("' AND league_id=")
           .append (new_league_id)
           .append (" AND description='")
           .append (original_category_key.getDescription ().replaceAll ("'", "''"))
           .append ("';\n")
           .append ("IF @category_id = NULL\n")
           .append ("BEGIN\n")

           .append ("INSERT INTO Category (date, end_date, league_id, description, category_type_id, header, timestamp)\nVALUES ('")
           .append (DateTimeUtils.get_date_string (original_category_key.getDate ()))
           .append ("', NULL, ")
           .append (new_league_id)
           .append (", '")
           .append (original_category_key.getDescription ().replaceAll ("'", "''"))
           .append ("', ")
           .append (category_type.ordinal ())
           .append (", '")
           .append (original_category_key.getDescription ().replaceAll ("'", "''"))
           .append ("', SYSDATETIMEOFFSET());\n")
           .append ("SET @category_id = SCOPE_IDENTITY();\n")
           .append ("SET @sc_sequence = @sc_sequence + 1;\n")
           .append ("INSERT INTO Schedule_Category (schedule_id, category_id, sequence) VALUES (@schedule_id, @category_id, @sc_sequence);\n")
           .append ("END\n")
           ;
        }
    //---------------------------------------------------------------------------------------------
    private void add_event (StringBuilder sql, Event_Key event_key, int new_league_id, OffsetDateTime event_time, int main_league_id, DBSevent dbs_event)
        {
        sql.append ("SET @event_id = NULL;\n")
           .append ("SELECT @event_id = id FROM Event WHERE date='")
           .append (event_key.getDate ())
           .append ("' AND number=")
           .append (event_key.getNumber ())
           .append (";\n")
           .append ("IF @event_id = NULL\n")
           .append ("BEGIN\n")

           .append ("INSERT INTO Event (date, number, league_id, double_header, timestamp, updated)\nVALUES ('")
           .append (event_key.getDate ())
           .append ("', ")
           .append (event_key.getNumber ())
           .append (", ")
           .append (new_league_id)
           .append (", ")
           .append (0)
           .append (", SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET());\n")
           .append ("SET @event_id = SCOPE_IDENTITY();\n")
           
           .append ("INSERT INTO Event_Time (event_id, timestamp, time, TBA, source_id)\nVALUES (@event_id, SYSDATETIMEOFFSET(), '")
           .append (DateTimeUtils.get_date_time_string_UTC (event_time))
           .append ("', 0, 1);\n")
           
           .append ("INSERT INTO Category_Event (category_id, event_id)\nVALUES (@category_id, @event_id);\n")
           
           .append ("INSERT INTO Event_Item (event_id, sequence, timestamp)\nVALUES (@event_id, 0, SYSDATETIMEOFFSET());\n")
           .append ("SET @event_item_id = SCOPE_IDENTITY();\n")
           .append (add_team (main_league_id, dbs_event.away_team))
           .append ("INSERT INTO Event_Item (event_id, sequence, timestamp)\nVALUES (@event_id, 1, SYSDATETIMEOFFSET());\n")
           .append ("SET @event_item_id = SCOPE_IDENTITY();\n")
           .append (add_team (main_league_id, dbs_event.home_team))
           .append ("INSERT INTO Schedule_Category_Event (schedule_id, category_id, event_id, category_sequence) VALUES (@schedule_id, @category_id, @event_id, @sc_sequence);\n")
           .append ("END\n")
           ;
        }
    //---------------------------------------------------------------------------------------------
    private StringBuilder add_team (int main_league_id, String team_name)
        {
        StringBuilder sql = new StringBuilder ()
            .append ("SET @league_team_id = NULL;\n")
            .append ("SELECT @league_team_id = lt.id\n")
            .append ("  FROM Team_Equivalent AS te\n")
            .append ("JOIN League_Team AS lt ON lt.id = te.league_team_id\n")
            .append (" WHERE lt.league_id = ")
            .append (        main_league_id)
            .append (        "\n")
            .append ("   AND te.name = '")
            .append (        team_name)
            .append (        "';\n")
            .append ("IF @league_team_id IS NULL\n")                                           // If league_team is NULL
            .append ("  BEGIN\n")
            .append ("  INSERT INTO League_Team (active, league_id, temp_name) VALUES (1, ")      //             insert temp_name
            .append (       main_league_id)
            .append (       ", '")
            .append (       team_name)
            .append (       "');\n")
            .append ("  SET @league_team_id = SCOPE_IDENTITY();\n")
            .append ("  INSERT INTO Event_Item_League_Team (event_item_id, timestamp, league_team_id) VALUES (@event_item_id, SYSDATETIMEOFFSET(), @league_team_id);\n")
            .append ("  IF NOT EXISTS (SELECT name FROM Team_Equivalent WHERE league_id = ")
            .append (       main_league_id)
            .append (       " AND name = '")
            .append (       team_name)
            .append (       "')\n")
            .append ("    BEGIN\n")
            .append ("    INSERT INTO Team_Equivalent (league_id, name, league_team_id, source_id) VALUES (")
            .append (         main_league_id)
            .append (         ", '")
            .append (         team_name)
            .append (         "', @league_team_id, 2);\n")
            .append ("    END\n")
            .append ("  ELSE\n")                                                                   // else if league_prop exists
            .append ("    BEGIN\n")
            .append ("    UPDATE Team_Equivalent SET league_team_id = @league_team_id WHERE league_id = ")
            .append (         main_league_id)
            .append (         " AND name = '")
            .append (         team_name)
            .append (         "';\n")
            .append ("    END\n")
            .append ("  END\n")
            .append ("ELSE\n")                                                                 // else if league_team exists
            .append ("  BEGIN\n")                                                              //     insert event-item-league-team

            .append ("  SET @current_league_team_id = NULL;\n")
            .append ("  SELECT TOP 1 @current_league_team_id = league_team_id\n")
            .append ("    FROM Event_Item_League_Team AS eilt\n")
            .append ("   WHERE eilt.event_item_id = @event_item_id\n")
            .append ("   ORDER BY eilt.timestamp DESC\n")

            .append ("  IF @current_league_team_id IS NULL OR @league_team_id != @current_league_team_id\n")
            .append ("    BEGIN\n")
            .append ("    INSERT INTO Event_Item_League_Team (event_item_id, league_team_id, timestamp) VALUES (@event_item_id, @league_team_id, SYSDATETIMEOFFSET());\n")
            .append ("    SET @event_item_league_team_id = SCOPE_IDENTITY();\n")
            .append ("    END\n")
            .append ("  END\n")
            ;
        return (sql);
        }
    //---------------------------------------------------------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        close_Button = new javax.swing.JButton();
        dbs4_Button = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        data_TextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Parse");

        close_Button.setText("Close");
        close_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                close_ButtonActionPerformed(evt);
            }
        });

        dbs4_Button.setText("DBS4");
        dbs4_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                dbs4_ButtonActionPerformed(evt);
            }
        });

        data_TextArea.setColumns(20);
        data_TextArea.setRows(5);
        jScrollPane1.setViewportView(data_TextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dbs4_Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(close_Button)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(close_Button)
                    .addComponent(dbs4_Button))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dbs4_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_dbs4_ButtonActionPerformed
    {//GEN-HEADEREND:event_dbs4_ButtonActionPerformed
        dbs4 ();
    }//GEN-LAST:event_dbs4_ButtonActionPerformed

    private void close_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_close_ButtonActionPerformed
    {//GEN-HEADEREND:event_close_ButtonActionPerformed
        close ();
    }//GEN-LAST:event_close_ButtonActionPerformed
    //---------------------------------------------------------------------------------------------
    public static void main (JFrame parent)
        {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
            {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels ())
                {
                if ("Nimbus".equals (info.getName ()))
                    {
                    javax.swing.UIManager.setLookAndFeel (info.getClassName ());
                    break;
                    }
                }
            }
        catch (ClassNotFoundException ex)
            {
            java.util.logging.Logger.getLogger (ParseDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (InstantiationException ex)
            {
            java.util.logging.Logger.getLogger (ParseDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (IllegalAccessException ex)
            {
            java.util.logging.Logger.getLogger (ParseDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
            {
            java.util.logging.Logger.getLogger (ParseDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater (() ->
            {
            ParseDialog dialog = new ParseDialog (new javax.swing.JFrame (), true);
            dialog.addWindowListener (new java.awt.event.WindowAdapter ()
                {
                @Override
                public void windowClosing (java.awt.event.WindowEvent e)
                    {
                    }
                    });
            dialog.setVisible (true);
            });
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton close_Button;
    private javax.swing.JTextArea data_TextArea;
    private javax.swing.JButton dbs4_Button;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    }
