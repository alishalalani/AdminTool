/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduletool;

import scheduletool.login.LoginDialog;
import gsutils.Debug;
import gsutils.HDF.ScheduleChanged;
import gsutils.MSSQL;
import gsutils.MSSQLfactory;
import gsutils.SleepFrame;
import gsutils.data.Category;
import gsutils.data.Event;
import gsutils.data.Event_Key;
import gsutils.data.Schedule;
import gsutils.data.League;
import gsutils.data.League_Equivalent;
import gsutils.data.League_Player;
import gsutils.data.League_Team;
import gsutils.data.League_Team_Player;
import gsutils.data.Location;
import gsutils.data.Odds_Sportsbook;
import gsutils.data.Player;
import gsutils.data.Preset_Message;
import gsutils.data.Source;
import gsutils.data.Sport;
import gsutils.data.Sportsbook;
import gsutils.data.Venue;
import java.awt.Color;
import scheduletool.schedule_client.ScheduleClient;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import scheduletool.datetime.DateTimeDialog;
import scheduletool.schedule_client.ScoreServerClient;

/**
 *
 * @author samla
 */
public class Main
    {
    //---------------------------------------------------------------------------------------------
    static public  String name    = "Schedule Tool";
    static public  String version = "4.3";
    static private String db_value [];
    //---------------------------------------------------------------------------------------------
    static public boolean            development = false;
    static public MSSQL              db;
    static public boolean            done         = false;
    static public BufferedImage      originalImage;
    //---------------------------------------------------------------------------------------------
    static public Schedule           schedule;
    static public ScheduleClient     schedule_client;
    static public Thread             schedule_client_thread;
    static public ScoreServerClient  score_server_client;
    static public Thread             score_server_client_thread;
    static public ScheduleFrame      schedule_frame;
    static public CategoriesFrame    categories_frame;
    static public DateTimeDialog     date_and_time_dialog;
    static public TeamsDialog        teams_dialog;
    static public PitchersDialog     pitchers_dialog;
    static public AddEventDialog     add_event_dialog;
    static public AddCategoryDialog  add_category_dialog;
    static public EditCategoryDialog edit_category_dialog;
    static public LinesDialog        lines_dialog;
    static public MessageDialog      message_dialog = null;
    static public EventTimesDialog   event_times_dialog = null;
    static public LoadingFrame       loading_frame;
    static public boolean            initial_data_obtained = false;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public static void main (String[] args)
        {
        SleepFrame.main (name + " " + version, 0, 0);
        System.out.println ("Schedule Tool");

        development = (   args.length > 0
                       && args [0].equals ("dev"))
                      ;
        initialize ();
        }
    //---------------------------------------------------------------------------------------------
    public static void initialize ()
        {
        initialize (null);
        }
    //---------------------------------------------------------------------------------------------
    public static void initialize (Rectangle rect)
        {
        String function = "Main.initialize";
        LoginDialog dialog = LoginDialog.main (development);
        //Debug.print ("(" + LoginDialog.response.length () + ")(" + LoginDialog.response + ")");
        development = dialog.development;
        db_value = LoginDialog.parse_response ();

        schedule               = null;
        schedule_client        = null;
        schedule_client_thread = null;
        schedule_frame         = null;
        categories_frame       = null;
        date_and_time_dialog   = null;
        teams_dialog           = null;
        pitchers_dialog        = null;
        add_event_dialog       = null;
        add_category_dialog    = null;
        edit_category_dialog   = null;
        loading_frame          = null;
        initial_data_obtained  = false;

        SleepFrame.show_message (function, "Connecting to database");

        try
            {
            db = MSSQLfactory.getMSSQL ();
            //MSSQL.connect ("localhost", "1433", "OddsDev", "sa", "saSQL26");
            if (development)
                {
                SleepFrame.set_database_label ("development", Color.green);
                //MSSQL.connect ("192.168.1.82", "1433", "OddsDev", "sa", "saSQL26");
                MSSQL.connect (db_value [0], db_value [1], db_value [2], db_value [3], db_value [4]);
                System.out.println ("Connected to development database!");
                }
            else
                {
                SleepFrame.set_database_label ("PRODUCTION", Color.red);
                //MSSQL.connect ("odds2.c37q6gktziwt.us-west-1.rds.amazonaws.com", "1433", "Odds", "sa", "samOddsGS");
                MSSQL.connect (db_value [0], db_value [1], db_value [2], db_value [3], db_value [4]);
                System.out.println ("Connected to ***PRODUCTION*** database!");
                }

            //loading_frame = new LoadingFrame ();

            SleepFrame.show_message (function, "Waiting for ScheduleClient to exit");
            if (schedule_client != null)
                while (!schedule_client.isDone ())
                    SleepFrame.sleep (1000);

            if (development)
                {
                ScheduleClient   .setHost (ScheduleClient.host_dev);
                ScoreServerClient.setHost (ScheduleClient.host_dev);
                }
            else
                {
                ScheduleClient   .setHost (ScheduleClient.host_prod);
                ScoreServerClient.setHost (ScheduleClient.host_prod);
                }

            SleepFrame.show_message (function, "Waiting for Initializing ScheduleClient");
            schedule_client = new ScheduleClient ();
            schedule_client_thread = new Thread (schedule_client);
            schedule_client_thread.start ();

            score_server_client = new ScoreServerClient ();
            score_server_client_thread = new Thread (score_server_client);
            score_server_client_thread.start ();

            read_tables ();
            SleepFrame.close ();
            ScheduleFrame.main ();
            //get_initial_data ();
            }
        catch (Exception e)
            {
            System.out.println ("*** Main.main:  Exception " + e);
            e.printStackTrace ();
            }
        }
    //---------------------------------------------------------------------------------------------
    static void read_tables ()
        {
        String function = "Main.read_tables";
        Debug.printt ("READING TABLES ***********************");
        SleepFrame.show_message (function, "Getting Sports");
        Sport             .read_table (db);
        SleepFrame.show_message (function, "Getting Leagues");
        League            .read_table (db);
        SleepFrame.show_message (function, "Getting Source");
        Source            .read_table (db);

        SleepFrame.show_message (function, "Getting League-Teams");
        League_Team       .read_table (db);
        SleepFrame.show_message (function, "Getting Players");
        Player            .read_table (db);
        SleepFrame.show_message (function, "Getting League-Players");
        League_Player     .read_table (db);
        SleepFrame.show_message (function, "Getting League-Team-Players");
        League_Team_Player.read_table (db);
//        SleepFrame.show_message (function, "Getting League-Props");
//        League_Prop       .read_table (db);
        SleepFrame.show_message (function, "Getting League-Equivalents");
        League_Equivalent .read_table (db);
        SleepFrame.show_message (function, "Getting Sportsbook");
        Sportsbook        .read_table (db);
        Odds_Sportsbook   .read_table (db, "Lineserver_Sportsbook");
        Location          .read_table (db);
        Venue             .read_table (db);
        //preset messages have id and value
        Preset_Message    .read_table (db);

        SleepFrame.show_message (function, "Done reading tables");
        Debug.printt ("DONE READING TABLES ***********************");
        }
    //---------------------------------------------------------------------------------------------
    static public void close ()
        {
        Debug.print ("top");
        done = true;
        if (categories_frame != null)
            categories_frame.dispose ();
        schedule_frame.dispose ();
        try
            {
            MSSQL.disconnect ();
            Thread.sleep (1000);
            }
        catch (Exception e)
            {
            Debug.print ("Exception (" + e + ")");
            e.printStackTrace ();
            }
        Debug.print ("bottom");
        }
    //---------------------------------------------------------------------------------------------
    static public boolean isDone ()
        {
        return (done);
        }
    //---------------------------------------------------------------------------------------------
    static public void restart ()
        {
        Rectangle rect = schedule_frame.getBounds ();
        Main.close ();
        schedule_client.setDone (true);
        Main.initialize (rect);
        }
    //---------------------------------------------------------------------------------------------
    static void exit ()
        {
        Main.close ();
        System.exit (0);
        }
    //---------------------------------------------------------------------------------------------
    public static void get_initial_data ()
        {
        while (!schedule_client.isLeagues_obtained ())
            SleepFrame.sleep (1000);
        if (!initial_data_obtained)
            {
            schedule_client.send ("GET_SPORTS");
            schedule_client.send ("GET_LEAGUES");
            schedule_client.send ("GET_SPORTSBOOKS");
            schedule_client.send ("GET_SCHEDULE");
            schedule_client.send ("GET_SCORES");

            schedule_client.send ("GET_LEAGUE_EQUIVALENTS");
            schedule_client.send ("GET_LEAGUE_TEAMS");
            schedule_client.send ("GET_PLAYERS");
            schedule_client.send ("GET_LEAGUE_PLAYERS");
            schedule_client.send ("GET_LEAGUE_TEAM_PLAYERS");
            schedule_client.send ("GET_LEAGUE_PROPS");

            initial_data_obtained = true;
            }
        }
    //---------------------------------------------------------------------------------------------
    static public void setDone (boolean done)
        {
        Main.done = done;
        }
    //---------------------------------------------------------------------------------------------
    static public void send_schedule_changed_for_event (Event event)
        {
        ScheduleChanged schedule_changed = ScheduleChanged.factory ();
        Event_Key event_key = new Event_Key (event);
        schedule_changed.add_to_events (event_key, "", "");

        gsutils.HDF.create.ScheduleChanged schedule_changed_to_send = new gsutils.HDF.create.ScheduleChanged ();
        schedule_changed_to_send.setScheduleChanged (schedule_changed);
        schedule_changed_to_send.process ();

        String message = "SCHEDULE_CHANGED>>>\n" + schedule_changed_to_send.getHDF_data ().toString () + "\n<<<\n";
        Debug.print (message);
        Main.schedule_client.send (message);
        }
    //---------------------------------------------------------------------------------------------
    static public void send_category (Category category)
        {
        gsutils.HDF.create.Categories category_to_send = new gsutils.HDF.create.Categories (category);
        category_to_send.process ();
        String message = "CATEGORY_CHANGED>>>\n" + category_to_send.getHDF_data ().toString () + "\n<<<\n";
        Debug.print (message);
        Main.schedule_client.send (message);
        }
    //---------------------------------------------------------------------------------------------
    static public void add_category (Category category)
        {
        gsutils.HDF.create.Categories category_to_send = new gsutils.HDF.create.Categories (category);
        category_to_send.process ();
        String message = "CATEGORY_ADDED>>>\n" + category_to_send.getHDF_data ().toString () + "\n<<<\n";
        Debug.print (message);
        Main.schedule_client.send (message);
        }
    }
