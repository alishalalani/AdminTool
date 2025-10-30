/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package scheduletool;

import scheduletool.parse.ParseDialog;
import javax.swing.table.DefaultTableModel;
import scheduletool.table.ScheduleRecord;
import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.data.Category;
import gsutils.data.Category_Key;
import gsutils.data.Event;
import gsutils.data.Event_Item;
import gsutils.data.Event_Item_League_Team;
import gsutils.data.Event_Score;
import gsutils.data.Event_Score_Item;
import gsutils.data.Event_Time;
import gsutils.data.League;
import gsutils.data.League_Player;
import gsutils.data.League_Position;
import gsutils.data.League_Team;
import gsutils.data.League_Team_Player;
import gsutils.data.Player;
import gsutils.data.Sport;
import gsutils.data.Schedule;
import gsutils.data.Event_Key;
import gsutils.schedule.Schedule_Reader;

import java.sql.ResultSet;
import java.awt.Rectangle;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ScrollPaneConstants;
import scheduletool.datetime.DateTimeDialog;

/**
 *
 * @author samla
 */
public class CategoriesFrame extends javax.swing.JFrame
    {
    //---------------------------------------------------------------------------------------------
    private final int LEAGUE_ID          = 0;
    private final int LEAGUE_NAME        = 1;
    //---------------------------------------------------------------------------------------------
    private final int EXCLUDE            = 0;
    private final int CATEGORY_ID        = 1;
    private final int CATEGORY_DATE      = 2;
    private final int CATEGORY_END_DATE  = 3;
    private final int CATEGORY_HEADER    = 4;
    //---------------------------------------------------------------------------------------------
//  private final int EXCLUDE            = 0;
    private final int EVENT_ID           = 1;
    private final int EVENT_TIME         = 2;
    private final int EVENT_NUMBER       = 3;
    private final int GAME_NUMBER        = 4;
    private final int EVENT_STATUS_1     = 5;
    private final int EVENT_STATUS_2     = 6;
    private final int EVENT_STATUS_0     = 7;
    private final int EVENT_AWAY_POSSESSION = 8;
    private final int EVENT_HOME_POSSESSION = 9;
    //---------------------------------------------------------------------------------------------
    private final int EVENT_ITEM_ID      = 0;
    private final int EVENT_ITEM_NUMBER  = 1;
    private final int EVENT_ITEM_TEAM    = 2;
    private final int EVENT_ITEM_PITCHER = 3;
    private final int EVENT_ITEM_SCORE   = 4;
    //---------------------------------------------------------------------------------------------
    private final int default_sport_id = 3;
    //---------------------------------------------------------------------------------------------
    private ArrayList <Category> categories_for_league;
    //---------------------------------------------------------------------------------------------
    enum SELECTION
        {
        SPORT,
        LEAGUE,
        CATEGORY,
        EVENT,
        EVENT_ITEM
        }
    //---------------------------------------------------------------------------------------------
    Sport              selected_sport       = null;
    League             selected_league      = null;
    Category           selected_category    = null;
    Event              selected_event       = null;
    int                selected_event_index = 0;
    Event_Item         selected_event_item  = null;
    League_Team_Player selected_pitcher     = null;
    DefaultTableModel  category_model;
    DefaultTableModel  event_model;
    ArrayList <Event> events_for_selected_category = new ArrayList <> (); //key is event_id
    //---------------------------------------------------------------------------------------------
    private Category new_category;
    boolean sports_initialized = false;
    boolean show_all_leagues_for_sport = false;
    //---------------------------------------------------------------------------------------------
    public CategoriesFrame ()
        {
        initComponents ();
        setTitle (Main.name + " " + Main.version);
        setLocation (10, 10);

        all_SplitPane   .setDividerLocation (300);
        top_SplitPane   .setDividerLocation (150);
        bottom_SplitPane.setDividerLocation (150);
        }
    //---------------------------------------------------------------------------------------------
    public void initialize (int schedule_id)
        {
        Debug.print ("top");
        frame_resized ();
        ScheduleRecord schedule_record = ScheduleRecord.get_schedule_record (schedule_id);
        schedule_date_Label.setText (DateTimeUtils.get_date_display_string (schedule_record.getDate ()));
//        AddCategoryDialog .main (this);
//        EditCategoryDialog.main (this);
//        AddEventDialog    .main (this);
        Main.schedule = new Schedule ();
        Main.schedule.setId (schedule_id);
        Main.schedule.setDate (schedule_record.getDate ());
        get_schedule ();

//        if (Main.schedule != null)
//            schedule_received_initialize_frame ();
//        else
//            System.out.println ("main.schedule is null");

        category_model                     = (DefaultTableModel) categories_Table .getModel ();
        event_model                        = (DefaultTableModel) events_Table     .getModel ();
        DefaultTableModel league_model     = (DefaultTableModel) leagues_Table    .getModel ();
        DefaultTableModel event_item_model = (DefaultTableModel) event_items_Table.getModel ();

        TreeSet <Sport> sports  = new TreeSet <> ();
        for (Category category : Main.schedule.getCategories_by_id ().values ())
            {
            League league = category.getLeague ();
            int sport_id = league.getSport_id ();
            Sport sport = Sport.get_sport (sport_id);
            sports.add (sport);
            }

        sport_ComboBox.removeAllItems ();
        for (Sport sport : sports)
            sport_ComboBox.addItem (sport.getName ());

        leagues_Table    .setModel (league_model);
        categories_Table .setModel (category_model);
        events_Table     .setModel (event_model);
        event_items_Table.setModel (event_item_model);

        events_ScrollPane.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        sports_initialized = true;
        sport_ComboBox.setSelectedItem (Sport.getSports ().get (default_sport_id).getName ());
//        categories_Table.setRowSelectionInterval (0, 0);

        }
    //---------------------------------------------------------------------------------------------
    public void schedule_received_initialize_frame ()
        {
        category_model                     = (DefaultTableModel) categories_Table .getModel ();
        event_model                        = (DefaultTableModel) events_Table     .getModel ();
        DefaultTableModel league_model     = (DefaultTableModel) leagues_Table    .getModel ();
        DefaultTableModel event_item_model = (DefaultTableModel) event_items_Table.getModel ();

        TreeSet <Sport > sports  = new TreeSet <> ();
        for (Category category : Main.schedule.getCategories_by_id ().values ())
            {
            League league = category.getLeague ();
            int sport_id = league.getSport_id ();
            Sport sport = Sport.get_sport (sport_id);
            sports.add (sport);
            }

        for (Sport sport : sports)
            sport_ComboBox.addItem (sport.getName ());
        sport_ComboBox.setSelectedItem (Sport.get_sport (default_sport_id).getName ());

        leagues_Table    .setModel (league_model);
        categories_Table .setModel (category_model);
        events_Table     .setModel (event_model);
        event_items_Table.setModel (event_item_model);
        }
    //---------------------------------------------------------------------------------------------
    private void get_schedule ()
        {
        Debug.printt ("READING SCHEDULE ***********************");
        LocalDate schedule_date = Main.schedule.getDate ();

        String sql;
        if (schedule_date.equals (LocalDate.now ()))
            sql = "EXEC usp_Schedule '" + schedule_date + "'";
        else
            sql = "EXEC usp_Archive_Schedule '" + schedule_date + "'";

        Debug.print ("SQL (" + sql + ") at " + DateTimeUtils.get_current_time_string ());
        try
            {
            ResultSet rs = Main.db.executeQuery (sql);
            Schedule_Reader.get_schedule (rs, Main.schedule);
            System.out.println ("Schedule_Reader.get:  Done parsing schedule at " + DateTimeUtils.get_current_time_string ());
            }
        catch (Exception e)
            {
            Debug.print ("Exception " + e);
            e.printStackTrace ();
            }
        Debug.printt ("DONE READING SCHEDULE ***********************");
        }
    //---------------------------------------------------------------------------------------------
    private void clear_models (SELECTION selection)
        {
        switch (selection)
            {
            case SPORT:
                //System.out.println (function + ":  clearing leagues");
                DefaultTableModel league_model     = (DefaultTableModel) leagues_Table    .getModel ();
                league_model    .setRowCount (0);
            case LEAGUE:
                //System.out.println (function + ":  clearing categories");
                category_model.setRowCount (0);
            case CATEGORY:
                //System.out.println (function + ":  clearing events");
                event_model.setRowCount (0);
                selected_event = null;
            case EVENT:
                //System.out.println (function + ":  clearing event_items");
                DefaultTableModel event_item_model = (DefaultTableModel) event_items_Table.getModel ();
                event_item_model.setRowCount (0);
                selected_event_item = null;
            }
        }
    //---------------------------------------------------------------------------------------------
    private void sport_changed (String from)
        {
        if (!sports_initialized)
            return;
        Debug.print ("top - from (" + from + ")");

        String sport_name = (String) sport_ComboBox.getSelectedItem ();
        Sport sport;
        if (sport_name == null)
            sport = Sport.getSports_by_name ().firstEntry ().getValue ();
        else
            sport = Sport.getSports_by_name ().get (sport_name);
        //if (selected_sport != null && selected_sport.getId () == sport.getId ())
        //    return;
        selected_sport = sport;
        System.out.println ("Sport selected (" + sport.getName () + ")");
        TreeSet <League> leagues = new TreeSet <> ();
        if (show_all_leagues_for_sport_CheckBox.isSelected ())
            {
            for (League league : selected_sport.getLeagues ().values ())
                leagues.add (league);
            }
        else
            {
            TreeMap <Integer, Category> map = Main.schedule.getCategories_by_id ();
            for (Category category : map.values ())
                {
                League league = League.get_league (category.getLeague ().getId ());
////if (league.getId () == 277)
////    Debug.print ("debug");
                int sport_id = league.getSport_id ();

                if (sport_id == sport.getId ())
                    leagues.add (league);
                }
            }

        DefaultTableModel league_model = (DefaultTableModel) leagues_Table.getModel ();
        clear_models (SELECTION.SPORT);
        //System.out.println (function + ":  before for");
        for (League league : leagues)
            {
            //System.out.println (function + ":  " + league);
            Object league_row_data [] = {"" + league.getId (), league.getName ()};
            league_model.addRow (league_row_data);
            }
        selected_league = null;
        Debug.print ("bottom");
        }
    //---------------------------------------------------------------------------------------------
    private void league_changed () throws NumberFormatException
        {
        selected_category = null;
        selected_pitcher  = null;
        int row = leagues_Table.getSelectedRow ();
        System.out.println ("League row " + row);
        if (row >= 0)
            {
            //Do this if League != Womens or Mens tennis
            event_model                        = (DefaultTableModel) events_Table     .getModel ();

            int selected_league_id = Integer.parseInt ((String) leagues_Table.getValueAt (row, 0));
//            if (selected_league_id == 97 || selected_league_id == 98)
//                {
//                 String[] newColumnNames = {"Exclude", "Event ID", "Date/Time", "Number","GM#", "Status1", "Status2", "Status0", "Away Possession", "Home Possession"};
//                 event_model.setColumnIdentifiers (newColumnNames);
//                 events_Table.setModel (event_model);
//                }
            System.out.println ("selected_league_id " + selected_league_id);
            if (selected_league_id > 0)
                {
                selected_league = League.get_league (selected_league_id);
                System.out.println ("selected league (" + selected_league.getName () + ")");

                category_model = (DefaultTableModel) categories_Table.getModel ();
                clear_models (SELECTION.LEAGUE);
                int previous_category_id = 0;
                categories_for_league = new ArrayList ();

                int new_category_index = -1;
                int i = 0;
                TreeMap <Category_Key, Category> map = Main.schedule.getCategories_by_key ();
                for (Category category : map.values ())
                    {
                    categories_for_league.add (category);

                    League current_league = category.getLeague ();

                    if (current_league.getId () == selected_league.getId ())
                        {
                        LocalDate end_date = category.getEnd_date ();
                        if (category.getId () != previous_category_id)
                            {
                            Object rowData[] = {category.isExclude (),"" + category.getId (), "" + category.getDate (), "" + (end_date == null ? "" : end_date), "" + category.getHeader ()};
                            category_model.addRow (rowData);
                            previous_category_id = category.getId ();

                            if (new_category != null && (category.getId () == new_category.getId ()))
                                {
                                new_category_index = i;
                                }
                            i ++;
                            }
                        }
                    }

                if (new_category_index >= 0)
                    {
                    categories_Table.setRowSelectionInterval (new_category_index, new_category_index);
                    new_category = null;
                    Rectangle cellRectangle = categories_Table.getCellRect (categories_Table.getSelectedRow (), 0, true);
                    categories_Table.scrollRectToVisible (cellRectangle);
                    }
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    private void category_changed () throws NumberFormatException
        {
        //Main.add_category_dialog = null;
        selected_pitcher = null;
        int row = categories_Table.getSelectedRow ();
        System.out.println ("Categories row " + row);
        if (row >= 0)
            {
            int selected_category_id = Integer.parseInt ((String) categories_Table.getValueAt (row, 1));
            System.out.println ("selected_category_id " + selected_category_id);
            if (selected_category_id > 0)
                {
                Category category = Main.schedule.getCategories_by_id ().get (selected_category_id);
                selected_category = category;
                System.out.println ("selected category (" + category.getHeader () + ")");
                event_model = (DefaultTableModel) events_Table.getModel ();
                clear_models (SELECTION.CATEGORY);
                events_for_selected_category.clear ();
                for (Event event : category.getEvents ().values ())
                    {
                    events_for_selected_category.add (event);
                    Event_Time event_time = event.getEvent_time ();
                    String time;
                    if (event_time == null || event_time.isTBA ())
                        time = "TBA";
                    else
                        time = DateTimeUtils.get_pacific_date_time_string_for_viewing (event_time.getTime ());
                        //time = DateTimeUtils.get_date_time_string_with_zone (event_time.getTime (), DateTimeUtils.pacific_zone_ID);

                    String status1    = "";
                    String status2    = "";
                    String status0    = "";
                    Event_Score event_score = event.getEvent_score ();
                    if (event_score != null && event_score.getEvent_score_items ().size () >= 2)
                        {
                        status1 = event_score.getStatus1 ();
                        status2 = event_score.getStatus2 ();
                        status0 = event_score.getStatus0 ();
                        }
                    Object event_row_data [] = {event.isExclude (), "" + event.getId (), time, "" + event.getNumber (), event.getDouble_header (), status1, status2, status0, false, false};
                    event_model.addRow (event_row_data);
                    }
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    private void category_changed (Event new_event) throws NumberFormatException
        {
        //Main.add_category_dialog = null;
        int row = categories_Table.getSelectedRow ();
        System.out.println ("Categories row " + row);
        if (row >= 0)
            {
            int selected_category_id = Integer.parseInt ((String) categories_Table.getValueAt (row, 1));
            System.out.println ("selected_category_id " + selected_category_id);
            int index_to_select = -1;
            if (selected_category_id > 0)
                {
                Category category = Main.schedule.getCategories_by_id ().get (selected_category_id);
                selected_category = category;
                System.out.println ("selected category (" + category.getHeader () + ")");
                event_model = (DefaultTableModel) events_Table.getModel ();
                clear_models (SELECTION.CATEGORY);
                int x = 0;
                for (Event event : category.getEvents ().values ())
                    {
                    if (event.getNumber () == new_event.getNumber ())
                        index_to_select = x;
                    Event_Time event_time = event.getEvent_time ();
                    String time;
                    if (event_time == null || event_time.isTBA ())
                        time = "TBA";
                    else
                        time = DateTimeUtils.get_date_time_string_with_zone (event_time.getTime (), DateTimeUtils.pacific_zone_ID);

                    String status1    = "";
                    String status2    = "";
                    String status0    = "";
                    Event_Score event_score = event.getEvent_score ();
                    if (event_score != null && event_score.getEvent_score_items ().size () >= 2)
                        {
                        status1 = event_score.getStatus1 ();
                        status2 = event_score.getStatus2 ();
                        status0 = event_score.getStatus0 ();
                        }

                    Object [] event_row_data = new Object [] {event.isExclude (),"" + event.getId (), time, "" + event.getNumber (), event.getDouble_header (), status1, status2, status0, false, false};
                    event_model.addRow (event_row_data);

                    x++;
                    }
                }
            events_Table.setRowSelectionInterval (index_to_select, index_to_select);
            }
        }
    //---------------------------------------------------------------------------------------------
    private void event_changed () throws NumberFormatException
        {
        selected_pitcher = null;
        selected_event_index = events_Table.getSelectedRow ();
        System.out.println ("Events row " + selected_event_index);
        if (selected_event_index >= 0)
            {
            int selected_event_id = Integer.parseInt ((String) events_Table.getValueAt (selected_event_index, 1));
            System.out.println ("selected_event_id " + selected_event_id);
            if (selected_event_id > 0)
                {
                Event event = Main.schedule.getEvents_by_id ().get (selected_event_id);
                selected_event = event;
                System.out.println ("selected event (" + event + ")");
                DefaultTableModel event_item_model = (DefaultTableModel) event_items_Table.getModel ();
                clear_models (SELECTION.EVENT);
                int index = 0;
                int sport_id = selected_league.getSport_id ();
                Event_Score event_score = selected_event.getEvent_score ();
                for (Event_Item event_item : event.getEvent_items ().values ())
                    {
                    String team_name = "";
                    String pitcher   = "";
                    Event_Item_League_Team event_item_league_team = event_item.getEvent_item_league_team ();
                    if (event_item_league_team != null)
                        {
                        League_Team league_team = event_item_league_team.getLeague_team ();
                        if (league_team != null)
                            {
                            if (league_team.getName () != null)
                                {
                                team_name = league_team.getName ();
                                System.out.println ("LEAGUE TEAM id: " + league_team.getId () + " name: " + team_name);
                                League_Team_Player league_team_player = event_item_league_team.getLeague_team_players ().get (League_Position.MLB_PITCHER);
                                if (league_team_player != null)
                                    {
                                    League_Player league_player = league_team_player.getLeague_player ();
                                    if (league_player != null)
                                        {
                                        Player player = league_player.getPlayer ();
                                        if (player != null)
                                            pitcher = player.getDisplay_name ();
                                        }
                                    }
                                }
                            else if (league_team.getTemp_name () != null)
                                {
                                team_name = league_team.getTemp_name ();
                                System.out.println ("LEAGUE TEAM id: " + league_team.getId () + " temp-name: " + team_name);
                                }
                            }
                        }

                    String score = "";
                    if (   event_score != null
                        && event_score.getEvent_score_items () != null
                        && event_score.getEvent_score_items ().size () > index)
                        {
                        if (   sport_id == gsutils.data.Sport.TENNIS
                            || sport_id == gsutils.data.Sport.TABLE_TENNIS)
                            {
                            if (event_score.getEvent_score_items ().get (index).getAddendum () != null)
                                score = "" + event_score.getEvent_score_items ().get (index).getAddendum ();
                            }
                        else if (event_score.getEvent_score_items ().get (index).getScore () != null)
                            {
                            score = "" + event_score.getEvent_score_items ().get (index).getScore ();
                            }
                        }
                    Object event_item_row_data [] = {"" + event_item.getId (), "" + (event.getNumber () + event_item.getSequence ()), team_name, pitcher, score};
                    event_item_model.addRow (event_item_row_data);
//                    if (event_item_parameter != null && event_item_parameter.getId () == event_item.getId ())
//                        selected_index = index;
                    index++;
                    }
                event_items_Table.setModel (event_item_model);
//                if (selected_index >= 0)
//                    {
//                    event_items_Table.setRowSelectionInterval (selected_index, selected_index);
//                    selected_event_item = event_item_parameter;
//                    }
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    private void resend_event ()
        {
        if (selected_event == null)
            JOptionPane.showMessageDialog (this, "Please select an event to send");
        else
            Main.send_schedule_changed_for_event (selected_event);
        }
    //---------------------------------------------------------------------------------------------
    private void event_item_changed () throws NumberFormatException
        {
        int row = event_items_Table.getSelectedRow ();
        System.out.println ("Event Items row " + row);
        if (row >= 0)
            {
            int selected_event_item_id = Integer.parseInt ((String) event_items_Table.getValueAt (row, 0));
            System.out.println ("selected_event_id " + selected_event_item_id);
            if (selected_event_item_id > 0)
                {
                Event_Item event_item = Main.schedule.getEvent_items_by_id ().get (selected_event_item_id);
                selected_event_item = event_item;
                if (   event_item.getEvent_item_league_team () != null
                    && event_item.getEvent_item_league_team ().getLeague_team_players () != null
                    && event_item.getEvent_item_league_team ().getLeague_team_players ().get (League_Position.MLB_PITCHER) != null)
                    {
                    selected_pitcher = event_item.getEvent_item_league_team ().getLeague_team_players ().get (League_Position.MLB_PITCHER);
                    }
                System.out.println ("selected event item (" + event_item + ") pitcher (" + selected_pitcher + ")");
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    private void frame_resized ()
        {
        Debug.print ("resized (" + getWidth () + ")(" + getHeight () + ")");

        int frame_width = getWidth ();

        leagues_Table    .getColumnModel().getColumn(LEAGUE_ID)         .setPreferredWidth (100);
        leagues_Table    .getColumnModel().getColumn(LEAGUE_NAME)       .setPreferredWidth (frame_width - 100);

        categories_Table .getColumnModel().getColumn(EXCLUDE)           .setPreferredWidth (75);
        categories_Table .getColumnModel().getColumn(CATEGORY_ID)       .setPreferredWidth (100);
        categories_Table .getColumnModel().getColumn(CATEGORY_DATE)     .setPreferredWidth (100);
        categories_Table .getColumnModel().getColumn(CATEGORY_END_DATE) .setPreferredWidth (100);
        categories_Table .getColumnModel().getColumn(CATEGORY_HEADER)   .setPreferredWidth (frame_width - 275);

        events_Table     .getColumnModel().getColumn(EXCLUDE)              .setPreferredWidth ((int)(frame_width * 0.07));
        events_Table     .getColumnModel().getColumn(EVENT_ID)             .setPreferredWidth ((int)(frame_width * 0.08));
        events_Table     .getColumnModel().getColumn(EVENT_TIME)           .setPreferredWidth ((int)(frame_width * 0.2));
        events_Table     .getColumnModel().getColumn(EVENT_NUMBER)         .setPreferredWidth ((int)(frame_width * 0.1));
        events_Table     .getColumnModel().getColumn(GAME_NUMBER)          .setPreferredWidth ((int)(frame_width * 0.05));
        events_Table     .getColumnModel().getColumn(EVENT_STATUS_1)       .setPreferredWidth ((int)(frame_width * 0.1));
        events_Table     .getColumnModel().getColumn(EVENT_STATUS_2)       .setPreferredWidth ((int)(frame_width * 0.1));
        events_Table     .getColumnModel().getColumn(EVENT_STATUS_0)       .setPreferredWidth ((int)(frame_width * 0.1));
        events_Table     .getColumnModel().getColumn(EVENT_AWAY_POSSESSION).setPreferredWidth ((int)(frame_width * 0.12));
        events_Table     .getColumnModel().getColumn(EVENT_HOME_POSSESSION).setPreferredWidth ((int)(frame_width * 0.12));

        event_items_Table.getColumnModel().getColumn(EVENT_ITEM_ID)     .setPreferredWidth (100);
        event_items_Table.getColumnModel().getColumn(EVENT_ITEM_NUMBER) .setPreferredWidth (100);
        event_items_Table.getColumnModel().getColumn(EVENT_ITEM_TEAM)   .setPreferredWidth (200);
        event_items_Table.getColumnModel().getColumn(EVENT_ITEM_PITCHER).setPreferredWidth (frame_width - 400);
        event_items_Table.getColumnModel().getColumn(EVENT_ITEM_SCORE)  .setPreferredWidth (200);
        }
    //---------------------------------------------------------------------------------------------
    private void change_time ()
        {
        if (selected_event == null)
            JOptionPane.showMessageDialog (this, "Select an event to change its date/time");
        else
            {
            if (Main.date_and_time_dialog == null)
                Main.date_and_time_dialog = new DateTimeDialog (this, true, selected_event, true);
            else
                {
                if (selected_event.getEvent_time () != null)
                    Debug.print (selected_event.getEvent_time ().getTime ().toLocalDateTime () + "");
                Main.date_and_time_dialog.initialize_public (selected_event);
                }

            Main.date_and_time_dialog.setVisible (true);
            if (Main.date_and_time_dialog.isSaved ())
                {
                //OffsetDateTime new_offset_date_time = DateTimeUtils.local_to_pacific (Main.date_and_time_dialog.getSelected_date (), Main.date_and_time_dialog.getSelected_time ());
                //Debug.print (new_offset_date_time.toString ());
                //selected_event.getEvent_time ().setTime (new_offset_date_time);
                //Debug.print (selected_event.getEvent_time ().getTime ().toString ());
/*
                ScheduleChanged schedule_changed = ScheduleChanged.factory ();
                Event_Key event_key = new Event_Key (selected_event);
                schedule_changed.add_to_events (event_key, "Time", new_offset_date_time.withOffsetSameInstant (ZoneOffset.UTC).toString ());

                gsutils.HDF.create.ScheduleChanged schedule_changed_to_send = new gsutils.HDF.create.ScheduleChanged ();
                schedule_changed_to_send.setScheduleChanged (schedule_changed);
                schedule_changed_to_send.process ();

                String message = "SCHEDULE_CHANGED>>>\n" + schedule_changed_to_send.getHDF_data ().toString () + "\n<<<\n";
                Debug.print (message);
                Main.schedule_client.send (message);
*/
                OffsetDateTime offset_date_time = Main.date_and_time_dialog.get_selected_offset_datetime ();
                Event_Time event_time = selected_event.getEvent_time ();
                String time;
                if (event_time == null)
                    time = "TBA";
                else
                    {
                    event_time.setTime (offset_date_time);
                    if (event_time.isTBA ())
                        time = "TBA";
                    else
                        time = DateTimeUtils.get_pacific_date_time_string_for_viewing (event_time.getTime ());
                    //time = DateTimeUtils.get_date_time_string_with_zone (event_time.getTime (), DateTimeUtils.pacific_zone_ID);
                    }

                String status1    = "";
                String status2    = "";
                String status0    = "";
                Event_Score event_score = selected_event.getEvent_score ();
                if (event_score != null && event_score.getEvent_score_items ().size () >= 2)
                    {
                    status1 = event_score.getStatus1 ();
                    status2 = event_score.getStatus2 ();
                    status0 = event_score.getStatus2 ();
                    }

                Object [] updatedRowData = new Object [] {selected_event.isExclude (), "" + selected_event.getId (), time, "" + selected_event.getNumber (), "" + selected_event.getDouble_header (), status1, status2, status0, false, false};
                int row = events_Table.getSelectedRow ();

                for (int col = 0; col < updatedRowData.length; col++)
                    event_model.setValueAt (updatedRowData [col], row, col);
                event_model.fireTableRowsUpdated (row, row);
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    private void select_team ()
        {
        if (selected_event_item == null)
            JOptionPane.showMessageDialog (this, "Select an event-item to change its team");
        else
            {
            League_Team selected_team = null;
            Event_Item_League_Team selected_event_item_league_team = this.selected_event_item.getEvent_item_league_team ();
            if (selected_event_item_league_team != null)
                selected_team = selected_event_item_league_team.getLeague_team ();

            League_Team selected_temp = null;
            Event_Item_League_Team selected_event_item_league_temp = this.selected_event_item.getEvent_item_league_team ();
            if (selected_event_item_league_temp != null)
                selected_temp = selected_event_item_league_temp.getLeague_team ();
            //TeamsFrame.main (selected_event.getLeague ().getId ());
            if (Main.teams_dialog == null)
                {
                Main.teams_dialog = new TeamsDialog (this, true, selected_league.getId (), selected_event_item, selected_team, selected_temp, false);
                }
            else
                {
                Main.teams_dialog.set_item_team_and_temp (selected_event_item, selected_team, selected_temp);
                Main.teams_dialog.initialize_public (selected_league.getId ());
                }

            Main.teams_dialog.setVisible (true);

            if (Main.teams_dialog.isSaved ())
                {
                //System.out.println ("******************************88BEFORE SEND TEAM HDF CALLED");
                int league_team_id = Main.teams_dialog.getSelected_team ();
                int league_temp_id = Main.teams_dialog.getSelected_temp ();
                if (league_team_id > 0)
                    {
                    League_Team league_team = League_Team.getLeague_team_by_ID ().get (league_team_id);
                    Debug.print ("Selected team " + league_team);
                    selected_event_item.setEvent_item_league_team (null);
                    //if (selected_event_item.getEvent_item_league_team () == null)
                    selected_event_item.setEvent_item_league_team (new Event_Item_League_Team ());
                    selected_event_item.getEvent_item_league_team ().setLeague_team (league_team);
                    //selected_event_item.getEvent_item_league_team ().
                    }
                else if (league_temp_id > 0)
                    {
                    League_Team league_temp = League_Team.getLeague_team_by_ID ().get (league_temp_id);
                    Debug.print ("Selected temp " + league_temp);
                    selected_event_item.setEvent_item_league_team (null);
                    if (selected_event_item.getEvent_item_league_team () == null)
                        selected_event_item.setEvent_item_league_team (new Event_Item_League_Team ());
                    selected_event_item.getEvent_item_league_team ().setLeague_team (league_temp);
                    }
                send_team_hdf (league_team_id, league_temp_id);
                int selected_event_item_index = event_items_Table.getSelectedRow ();
                event_changed ();
                if (selected_event_item_index >= 0)
                    {
                    event_items_Table.setRowSelectionInterval (selected_event_item_index, selected_event_item_index);
                    event_item_changed ();
                    save_team ();
                    }
                }
            else if (Main.teams_dialog.isRemoved ())
                {
                Main.teams_dialog.remove_team_from_database (selected_event, selected_event_item);
                int selected_event_item_index = event_items_Table.getSelectedRow ();
                event_changed ();
                if (selected_event_item_index >= 0)
                    {
                    event_items_Table.setRowSelectionInterval (selected_event_item_index, selected_event_item_index);
                    event_item_changed ();
                    }
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    private void send_team_hdf (int league_team_id, int league_temp_id)
        {
        String data_text = "";
        String hdf_header = "[event_item_league_teams{updated,event_item_league_team{event_item_id,league_team_id}}]";
        String title = "EVENT_ITEM_LEAGUE_TEAM" ;
        int id = -1;
        //EVENT_ITEM_LEAGUE_TEAM
        if (this.selected_event_item != null)
            {
            String type = "";
            if (league_team_id > 0)
                {
                type = "team";
                id = league_team_id;
                }
            else if (league_temp_id > 0)
                {
                type = "temp";
                id = league_temp_id;
                hdf_header = "[event_item_league_teams{updated,event_item_league_team{event_item_id, league_team_id}}]";
                }

            if (league_team_id > 0 || league_temp_id > 0)
                {
                data_text += "EVENT_ITEM_LEAGUE_" + type.toUpperCase () + ">>>\n";
                data_text += hdf_header + "\n";
                data_text += "{";
                data_text += DateTimeUtils.get_current_pacific_time ();
                data_text += "{";
                data_text += this.selected_event_item.getId () + ",";
                data_text += id;
                data_text += "}";
                data_text += "}\n";
                data_text += "<<<";

                System.out.println (data_text);
                //Main.schedule_client.send (data_text);
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    private void select_pitcher ()
        {
        if (selected_event_item == null)
            JOptionPane.showMessageDialog (this, "Select an event-item to change its pitcher");
        else
            {
            //TeamsFrame.main (selected_event.getLeague ().getId ());
            if (Main.pitchers_dialog == null)
                Main.pitchers_dialog = new PitchersDialog (this, true, selected_event, selected_event_item, selected_pitcher, false);
            else
                Main.pitchers_dialog.initialize_public (selected_event, selected_event_item, selected_pitcher);

            if (!Main.pitchers_dialog.isError ())
                {
                Main.pitchers_dialog.setVisible (true);

                if (Main.pitchers_dialog.isSaved ())
                    {
                    League_Team_Player new_pitcher = Main.pitchers_dialog.getSelected_pitcher ();
//                    if (new_pitcher == null)
//                        {
//                        Event_Item_League_Team event_item_league_team = selected_event_item.getEvent_item_league_team ();
//                        League_Team league_team = event_item_league_team.getLeague_team ();
//                        for (League_Team_Player league_team_player : league_team.getLeague_team_player_by_ID ().values ())
//                            {
//                            if (league_team_player.getLeague_player ().getId () == League_Player.UNDECIDED)
//                                {
//                                new_pitcher = league_team_player;
//                                break;
//                                }
//                            }
//                        }
//                    selected_pitcher = new_pitcher;
//                    selected_event_item.getEvent_item_league_team ().getLeague_team_players ().put (League_Position.MLB_PITCHER, new_pitcher);

                    if (new_pitcher != null)
                        {
                        selected_pitcher = new_pitcher;
                        selected_event_item.getEvent_item_league_team ().getLeague_team_players ().put (League_Position.MLB_PITCHER, new_pitcher);
                        }

                    int selected_event_item_index = event_items_Table.getSelectedRow ();
                    event_changed ();
                    if (selected_event_item_index >= 0)
                        {
                        event_items_Table.setRowSelectionInterval (selected_event_item_index, selected_event_item_index);
                        event_item_changed ();
                        }
                    }
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    private void save_team ()
        {
        if (selected_event_item == null)
            JOptionPane.showMessageDialog (this, "Select an event-item to save its team");
        else
            {
            Event_Item_League_Team event_item_league_team = selected_event_item.getEvent_item_league_team ();
            if (event_item_league_team != null)
                {
                String sql = "INSERT INTO Event_Item_League_Team (event_item_id, league_team_id, timestamp) VALUES (" + selected_event_item.getId () + "," + event_item_league_team.getLeague_team ().getId () + ", SYSDATETIMEOFFSET())";
                Main.db.executeUpdate (sql);
                Main.send_schedule_changed_for_event (selected_event);
                JOptionPane.showMessageDialog (this, "Team (" + event_item_league_team.getLeague_team ().getName () + ") saved!");
                }
            else
                {
                Event_Item_League_Team event_item_league_temp = selected_event_item.getEvent_item_league_team ();
                if (event_item_league_temp != null)
                    {
                    String sql = "INSERT INTO Event_Item_League_Team (event_item_id, league_team_id, timestamp) VALUES (" + selected_event_item.getId () + "," + event_item_league_temp.getLeague_team ().getId () + ", SYSDATETIMEOFFSET())";
                    Main.db.executeUpdate (sql);
                    JOptionPane.showMessageDialog (this, "Team (" + event_item_league_temp.getLeague_team ().getTemp_name () + ") saved!");
                    }
                else
                    JOptionPane.showMessageDialog (this, "No team found!");
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    private void save_team (Event_Item selected_event_item)
        {
        if (selected_event_item == null)
            JOptionPane.showMessageDialog (this, "Select an event-item to save its team");
        else
            {
            Event_Item_League_Team event_item_league_team = selected_event_item.getEvent_item_league_team ();
            if (event_item_league_team != null)
                {
                String sql = "INSERT INTO Event_Item_League_Team (event_item_id, league_team_id, timestamp) VALUES (" + selected_event_item.getId () + "," + event_item_league_team.getLeague_team ().getId () + ", SYSDATETIMEOFFSET())";
                Main.db.executeUpdate (sql);
                Main.send_schedule_changed_for_event (selected_event);
                JOptionPane.showMessageDialog (this, "Team (" + event_item_league_team.getLeague_team ().getName () + ") saved!");
                }
            else
                {
                Event_Item_League_Team event_item_league_temp = selected_event_item.getEvent_item_league_team ();
                if (event_item_league_temp != null)
                    {
                    String sql = "INSERT INTO Event_Item_League_Team (event_item_id, league_temp_id, timestamp) VALUES (" + selected_event_item.getId () + "," + event_item_league_temp.getLeague_team ().getId () + ", SYSDATETIMEOFFSET())";
                    Main.db.executeUpdate (sql);
                    JOptionPane.showMessageDialog (this, "Team (" + event_item_league_temp.getLeague_team ().getTemp_name () + ") saved!");
                    }
                else
                    JOptionPane.showMessageDialog (this, "No team found!");
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    public void save_score ()
        {
        if (events_Table.getCellEditor () != null)
            events_Table.getCellEditor ().stopCellEditing ();

        if (event_items_Table.getCellEditor () != null)
            event_items_Table.getCellEditor ().stopCellEditing ();

        if (selected_event == null)
            JOptionPane.showMessageDialog (this, "Select an event to save its score");
        else
            {
            //System.out.println ("SELECTED EVENT IS NOT NULL-------------");
            String event_date = selected_event.getDate ().toString ().replaceAll ("-", "");
            int event_number = selected_event.getNumber ();
            String away_score = ((String) event_items_Table.getModel ().getValueAt (0, EVENT_ITEM_SCORE)).trim ();
            String home_score = ((String) event_items_Table.getModel ().getValueAt (1, EVENT_ITEM_SCORE)).trim ();

            Object cell = events_Table     .getModel ().getValueAt (selected_event_index, EVENT_STATUS_1);
            String status1    = (cell == null ? "" : ((String) cell).trim ());

            cell = events_Table     .getModel ().getValueAt (selected_event_index, EVENT_STATUS_2);
            String status2    = (cell == null ? "" : ((String) cell).trim ());

            cell = events_Table     .getModel ().getValueAt (selected_event_index, EVENT_STATUS_0);
            String status0    = (cell == null ? "" : ((String) cell).trim ());

            cell = events_Table     .getModel ().getValueAt (selected_event_index, EVENT_AWAY_POSSESSION);
            boolean away_possession = (cell == null ? false : (boolean) cell);

            cell = events_Table     .getModel ().getValueAt (selected_event_index, EVENT_HOME_POSSESSION);
            boolean home_possession = (cell == null ? false : (boolean) cell);

            StringBuilder sql = new StringBuilder ();
            Event event = Main.schedule.getEvents_by_date_and_number ().get (new Event_Key (selected_event.getDate (), selected_event.getNumber ()));
            int sport_id = event.getLeague ().getSport_id ();
            if (   sport_id == gsutils.data.Sport.TENNIS
                || sport_id == gsutils.data.Sport.TABLE_TENNIS)
                {
                int period = 0;
                if (status2 == null)
                    period = 0;
                else if (status2.equals ("Final"))
                    {

                    }
                else if (status2.length () > 0 && Character.isDigit (status2.charAt (status2.length ()-1)))
                    {
                    if (status2.length () > 1 && Character.isDigit (status2.charAt (status2.length ()-2)))
                        period = Integer.parseInt (status2.substring (status2.length ()-2));
                    else
                        period = Integer.parseInt (status2.substring (status2.length ()-1));
                    }

                String away_score_last_set;
                String home_score_last_set;
                if (away_score.contains (","))
                    {
                    int last_comma = away_score.lastIndexOf (',');
                    away_score_last_set = away_score.substring (last_comma + 1);
                    last_comma = home_score.lastIndexOf (',');
                    home_score_last_set = home_score.substring (last_comma + 1);
                    }
                else
                    {
                    away_score_last_set = away_score;
                    home_score_last_set = home_score;
                    }

                boolean override = selected_event.getEvent_score ().isOverride ();
                sql.append ("DECLARE @event_score_id INT;\n")
                   .append ("INSERT INTO Event_Score (event_id, source_id, timestamp, feed_timestamp, event_completed, period_completed, period, status1, status2, away_possession, override)\n")
                   .append ("VALUES (")
                   .append (selected_event.getId ())
                   .append (", ")
                   .append (gsutils.data.Source.ODDSLOGIC)
                   .append (", SYSDATETIMEOFFSET()")
                   .append (", SYSDATETIMEOFFSET()")
                   .append (", ")
                   .append (status2 == null ? 0 : status2.equals ("Final") ? 1 : 0)
                   .append (", ")
                   .append (status2 == null ? 0 : status2.equals ("Final") || status2.equals ("End") || status2.equals ("Time") ? 1 : 0)
                   .append (", ")
                   .append (period)
                   .append (", '")
                   .append (status1 == null ? "" : status1)
                   .append ("', '")
                   .append (status2 == null ? "" : status2)
                   .append ("', ")
                   .append (away_possession ? "1" : home_possession ? "0" : "NULL")
                   .append (", ")
                   .append (override ? 1 : 0)
                   .append (");\n")
                   .append ("SET @event_score_id = SCOPE_IDENTITY();\n")

                   .append ("INSERT INTO Event_Score_Item (event_score_id, sequence, score, addendum)\n")
                   .append ("VALUES (@event_score_id, 0, '")
                   .append (away_score_last_set)
                   .append ("', '")
                   .append (away_score.replaceAll ("'", "''"))
                   .append ("');\n")

                   .append ("INSERT INTO Event_Score_Item (event_score_id, sequence, score, addendum)\n")
                   .append ("VALUES (@event_score_id, 1, '")
                   .append (home_score_last_set)
                   .append ("', '")
                   .append (home_score.replaceAll ("'", "''"))
                   .append ("');\n")
                   ;

                Event_Score event_score = selected_event.getEvent_score ();
                Event_Score_Item away_event_score_item = event_score.getEvent_score_items ().get (0);
                Event_Score_Item home_event_score_item = event_score.getEvent_score_items ().get (1);
                away_event_score_item.setAddendum (away_score);
                home_event_score_item.setAddendum (home_score);
                //JOptionPane.showMessageDialog (this, "Score NOT YET saved for (" + event_date + ")(" + event_number + ") FIX SQL!!!");
                }
            else
                {
                int period = 0;
                if (status2 == null)
                    period = 0;
                else if (status2.equals ("Time"))
                    {
                    int league_id = event.getLeague ().getId ();
                    switch (league_id)
                        {
                        case gsutils.data.League.NFL:
                        case gsutils.data.League.UFL:
                        case gsutils.data.League.CFB:
                        case gsutils.data.League.NBA:
                            period = 2;
                            break;
                        default:
                            period = 1;
                            break;
                        }
                    }
                else if (status2.length () > 0 && Character.isDigit (status2.charAt (0)))
                    {
                    if (status2.length () > 1 && Character.isDigit (status2.charAt (1)))
                        period = Integer.parseInt (status2.substring (0, 2));
                    else
                        period = Integer.parseInt (status2.substring (0, 1));
                    }

                boolean override = true;
                sql.append ("DECLARE @event_score_id INT;\n")
                   .append ("INSERT INTO Event_Score (event_id, source_id, timestamp, feed_timestamp, event_completed, period_completed, period, status1, status2, status0, away_possession, override)\n")
                   .append ("VALUES (")
                   .append (selected_event.getId ())
                   .append (", ")
                   .append (gsutils.data.Source.ODDSLOGIC)
                   .append (", SYSDATETIMEOFFSET()")
                   .append (", SYSDATETIMEOFFSET()")
                   .append (", ")
                   .append (status2 == null ? 0 : status2.equals ("Final") ? 1 : 0)
                   .append (", ")
                   .append (status2 == null ? 0 : status2.equals ("Final") || status2.equals ("End") || status2.equals ("Time") ? 1 : 0)
                   .append (", ")
                   .append (period)
                   .append (", '")
                   .append (status1 == null ? "" : status1)
                   .append ("', '")
                   .append (status2 == null ? "" : status2)
                   .append ("', '")
                   .append (status0 == null ? "" : status0)
                   .append ("', ")
                   .append (away_possession ? "1" : home_possession ? "0" : "NULL")
                   .append (", ")
                   .append (override ? 1 : 0)
                   .append (");\n")
                   .append ("SET @event_score_id = SCOPE_IDENTITY();\n")

                   .append ("INSERT INTO Event_Score_Item (event_score_id, sequence, score, addendum)\n")
                   .append ("VALUES (@event_score_id, 0, '")
                   .append (away_score.replaceAll ("'", "''"))
                   .append ("', NULL);\n")

                   .append ("INSERT INTO Event_Score_Item (event_score_id, sequence, score, addendum)\n")
                   .append ("VALUES (@event_score_id, 1, '")
                   .append (home_score.replaceAll ("'", "''"))
                   .append ("', NULL);\n")
                   ;

                Event_Score event_score = selected_event.getEvent_score ();
                if (event_score == null)
                    {
                    event_score = new Event_Score ();
                    selected_event.setEvent_score (event_score);
                    event_score.getEvent_score_items ().add (new Event_Score_Item ());
                    event_score.getEvent_score_items ().add (new Event_Score_Item ());
                    }

                event_score.setOverride (true);
                Event_Score_Item away_event_score_item = event_score.getEvent_score_items ().get (0);
                Event_Score_Item home_event_score_item = event_score.getEvent_score_items ().get (1);
                away_event_score_item.setScore (away_score);
                home_event_score_item.setScore (home_score);
                //JOptionPane.showMessageDialog (this, "Score NOT YET saved for (" + event_date + ")(" + event_number + ") FIX SQL!!!");
                }

            if (!Main.schedule_frame.isToday ())
                {
                Debug.print ("sql>>>\n" + sql.toString () + "\n<<<");
                Main.db.executeUpdate (sql.toString ());
            }
        }
        }
    //---------------------------------------------------------------------------------------------
    private void add_category ()
        {
       // Main.add_category_dialog.set_league (selected_league);

//        Category category_sample_for_header = null;
//        for (Category category : Main.schedule.getCategories_by_id ().values ())
//            {
//            League current_league = category.getLeague ();
//
//            if (current_league.getId () == selected_league.getId ())
//                {
//                category_sample_for_header = category;
//                break;
//                }
//            }

       // category_sample_for_header = selected_league.get

        //Main.add_category_dialog.set_prefilled_category_sample (category_sample_for_header);
        if (Main.add_category_dialog == null)
            AddCategoryDialog.main (this);
        Main.add_category_dialog.initialize_fields_add_category (selected_league, null);
        Main.add_category_dialog.setVisible (true);
        }
    //---------------------------------------------------------------------------------------------
    private void add_event ()
        {
        if (selected_league != null && selected_category != null)
            {
            ArrayList <Category> categories = new ArrayList <> ();
            for (Category category : Main.schedule.getCategories_by_id ().values ())
                categories.add (category);
            if (Main.add_event_dialog == null)
                AddEventDialog.main (this);
            Main.add_event_dialog.initialize_fields_add_event (selected_league, selected_category, categories);
            Main.add_event_dialog.setVisible (true);
            }
        //else popup to select league?
        }
    //---------------------------------------------------------------------------------------------
    public void edit_category ()
        {
//        Main.edit_category_dialog.initialize_fields (selected_category, selected_league);
//        Main.edit_category_dialog.setVisible (true);
//        EditCategoryDialog.main (this);
       if (selected_league != null && selected_category != null)
            {
            if (Main.add_category_dialog == null)
                AddCategoryDialog.main (this);
            Main.add_category_dialog.initialize_fields_edit_category (selected_category, selected_league);
            Main.add_category_dialog.setVisible (true);
            }
        }
    //---------------------------------------------------------------------------------------------
    public void add_new_category (Category category)
        {
        this.new_category = category;
        Main.schedule.add_category (category);
//        TreeMap <Integer, Category> categories = Main.schedule.getCategories_by_id ();
//        categories.put (category.getId (), category);
        league_changed ();
        }
    //---------------------------------------------------------------------------------------------
    public void display_new_category (int category_id)
        {
        if (new_category != null)
            {
            this.new_category.setId (category_id);
            Main.schedule.getCategories_by_id ().put (new_category.getId (), new_category);
            league_changed ();
            category_changed ();
            }
        }
    //---------------------------------------------------------------------------------------------
    public void display_new_event (int event_id, int away_event_item_id, int home_event_item_id)
        {
        if (selected_event != null)
            {
            this.selected_event.setId (event_id);
            TreeMap <Integer, Event_Item> event_items = this.selected_event.getEvent_items ();
            Event_Item away_event_item = event_items.get (0);
            away_event_item.setId (away_event_item_id);
            Event_Item home_event_item = event_items.get (1);
            home_event_item.setId (home_event_item_id);

            send_category_event_hdf ();
            selected_category.getEvents ().put (new Event_Key (selected_event.getDate (), selected_event.getNumber ()), selected_event);
            //this.selected_category.getEvents ().put (new Event_Key (event.getDate (), event.getNumber ()), event);

            Main.schedule.getEvents_by_id ().put (selected_event.getId (), selected_event);
            Main.schedule.getEvent_items_by_id ().put (away_event_item_id, away_event_item);
            Main.schedule.getEvent_items_by_id ().put (home_event_item_id, home_event_item);
            Main.schedule.getEvents_by_date_and_number ().put (new Event_Key (selected_event.getDate (), selected_event.getNumber ()), selected_event);
            category_changed (selected_event);
            event_changed ();
            TreeMap <Integer,Event_Item> event_items_for_new_event  = selected_event.getEvent_items ();
            save_team (event_items_for_new_event.get (0));
            save_team (event_items_for_new_event.get (1));
            }
        else
            System.out.println ("selected event is null");
        }
    //---------------------------------------------------------------------------------------------
    public void setEventOverride (boolean override)
        {
        this.selected_event.getEvent_time ().setOverride (override);
        }
    //---------------------------------------------------------------------------------------------
    public void edit_category (Category category)
        {
        selected_category.setCategory_type (category.getCategory_type ());
        selected_category.setDate          (category.getDate ());
        selected_category.setEnd_date      (category.getEnd_date ());
        selected_category.setHeader        (category.getHeader ());
        Main.schedule.getCategories_by_id ().replace (selected_category.getId (), selected_category);

        LocalDate end_date = category.getEnd_date ();
        int row = categories_Table.getSelectedRow ();
        Object updatedRowData[] = {category.isExclude (),"" + category.getId (), "" + category.getDate (), "" + (end_date == null ? "" : end_date), "" + category.getHeader ()};
        for (int col = 0; col < category_model.getColumnCount (); col++)
            category_model.setValueAt (updatedRowData[col], row, col);
        category_model.fireTableRowsUpdated (row, row);
        }
    //---------------------------------------------------------------------------------------------
    public void save_screen_event_after_editing (Event event)
        {
//        selected_category.setCategory_type(category.getCategory_type());
//        selected_category.setDate(category.getDate());
//        selected_category.setEnd_date(category.getEnd_date());
//        selected_category.setHeader(category.getHeader());
//        Main.schedule.getCategories_by_id ().replace(selected_category.getId(), selected_category);

//        this.selected_category.getEvents().remove(new Event_Key (selected_event.getDate(), selected_event.getNumber()));
//        this.selected_category.getEvents().put(new Event_Key (event.getDate(), event.getNumber()), event);
   //     System.out.println ("selected event time: ");
   //     System.out.println (event.getEvent_time().getTimestamp().toLocalTime());
//        this.selected_event = event;
//        System.out.println ("time in treemap: ");

        Event_Time event_time = event.getEvent_time ();
        String time;
        if (event_time == null || event_time.isTBA ())
            time = "TBA";
        else
            time = DateTimeUtils.get_pacific_date_time_string_for_viewing (event_time.getTime ());
            //time = DateTimeUtils.get_date_time_string_with_zone (event_time.getTime (), DateTimeUtils.pacific_zone_ID);

        String status1    = "";
        String status2    = "";
        String status0    = "";
        Event_Score event_score = event.getEvent_score ();
        if (event_score != null && event_score.getEvent_score_items ().size () >= 2)
            {
            status1 = event_score.getStatus1 ();
            status2 = event_score.getStatus2 ();
            status0 = event_score.getStatus0 ();
            }
        Object [] updatedRowData = new Object [] {event.isExclude (), "" + event.getId (), time, "" + event.getNumber (), "" + event.getDouble_header (), status1, status2, status0, false, false};

        int row = events_Table.getSelectedRow ();

        for (int col = 0; col < updatedRowData.length; col++)
            event_model.setValueAt (updatedRowData[col], row, col);
        event_model.fireTableRowsUpdated (row, row);
        int selected_event_item_index = event_items_Table.getSelectedRow ();
        event_changed ();
        if (selected_event_item_index >= 0)
            event_items_Table.setRowSelectionInterval (selected_event_item_index, selected_event_item_index);
        }
    //---------------------------------------------------------------------------------------------
    public void save_event_after_adding (Event event)
        {
//        selected_category.setCategory_type(category.getCategory_type());
//        selected_category.setDate(category.getDate());
//        selected_category.setEnd_date(category.getEnd_date());
//        selected_category.setHeader(category.getHeader());
//        Main.schedule.getCategories_by_id ().replace(selected_category.getId(), selected_category);


     //   Main.schedule.getEvents_by_id ().put (event.getId (), event);
       // this.selected_category.getEvents().put(new Event_Key (event.getDate(), event.getNumber()), event);
   //     System.out.println ("selected event time: ");
   //     System.out.println (event.getEvent_time().getTimestamp().toLocalTime());
        this.selected_event = event;
        System.out.println ("Setting selected event");
 //       System.out.println ("time in treemap: ");

        //int index = events_Table.getSelectedRow();
       // System.out.println ("index selected: " + index);
      // category_changed ();

       /// events_Table.setRowSelectionInterval(index, index);
        //event_changed (null);
        }
    //---------------------------------------------------------------------------------------------
    public void edit_event ()
        {
        if (selected_category != null && selected_event != null)
            {
            ArrayList <Category> categories = new ArrayList <> ();
            for (Category category : Main.schedule.getCategories_by_id ().values ())
                categories.add (category);
            if (Main.add_event_dialog == null)
                AddEventDialog.main (this);
            Main.add_event_dialog.initialize_fields_edit_event (selected_event, selected_category, categories);
            Main.add_event_dialog.setVisible (true);

            if (Main.add_event_dialog.isSaved ())
                {
                //event_changed (null);
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    public void add_score ()
        {
        if (selected_category != null && selected_event != null)
            {
            ScoreDialog add_score = new ScoreDialog (this, true);
            add_score.initialize_fields_add_score (selected_event, selected_category);
            add_score.setVisible (true);
            }
        }
    //---------------------------------------------------------------------------------------------
    public void edit_score ()
        {
        if (selected_category != null && selected_event != null)
            {
            ScoreDialog edit_score = new ScoreDialog (this, true);
            edit_score.initialize_fields_edit_score (selected_event, selected_category);
            edit_score.setVisible (true);
            }
        }
    //---------------------------------------------------------------------------------------------
    private void edit_lines ()
        {
        if (selected_category != null && selected_event != null)
            {
            LinesDialog edit_lines = new LinesDialog (this, true, selected_event);
            edit_lines.setVisible (true);
            }
        }
    //---------------------------------------------------------------------------------------------
    public void add_score (Event_Score event_score)
        {
        if (selected_event != null)
            {
            this.selected_category.getEvents ().remove (new Event_Key (selected_event.getDate (), selected_event.getNumber ()));
            selected_event.setEvent_score (event_score);
            this.selected_category.getEvents ().put (new Event_Key (selected_event.getDate (), selected_event.getNumber ()), selected_event);

            update_event (selected_event);
            event_changed ();
            }
        }
    //---------------------------------------------------------------------------------------------
    public void update_score_on_screen (Event event, Event_Score event_score)
        {
        if (event != null)
            {
            event.setEvent_score (event_score);

            for (int i = 0; i < events_for_selected_category.size (); i++)
                {
                Event current_event = events_for_selected_category.get (i);
                if (current_event.getId () == event.getId ())
                    {
                    int selected_event_index      = events_Table     .getSelectedRow ();
                    int selected_event_item_index = event_items_Table.getSelectedRow ();
                    //category_changed ();
                    //events_Table.setRowSelectionInterval (selected_event_index, selected_event_index);
                    Event_Time event_time = event.getEvent_time ();
                    String time;
                    if (event_time == null || event_time.isTBA ())
                        time = "TBA";
                    else
                        time = DateTimeUtils.get_pacific_date_time_string_for_viewing (event_time.getTime ());
                        //time = DateTimeUtils.get_date_time_string_with_zone (event_time.getTime (), DateTimeUtils.pacific_zone_ID);

                    String status1 = "";
                    String status2 = "";
                    String status0 = "";
                    if (event_score != null && event_score.getEvent_score_items ().size () >= 2)
                        {
                        status1 = event_score.getStatus1 ();
                        status2 = event_score.getStatus2 ();
                        status0 = event_score.getStatus0 ();
                        }
                    Object [] updatedRowData = new Object [] {event.isExclude (), "" + event.getId (), time, "" + event.getNumber (), "" + event.getDouble_header (), status1, status2, status0, false, false};

                    int row = i;

                    for (int col = 0; col < updatedRowData.length; col++)
                        event_model.setValueAt (updatedRowData[col], row, col);
                    event_model.fireTableRowsUpdated (row, row);

                    //save_score ();

                    event_changed ();
                    if (selected_event_item_index >= 0)
                        event_items_Table.setRowSelectionInterval (selected_event_item_index, selected_event_item_index);
                    if (selected_event_index >= 0)
                        events_Table.setRowSelectionInterval (selected_event_index, selected_event_index);

                    break;
                    }
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    public void edit_score (Event_Score event_score)
        {
        if (selected_event != null)
            {
           // this.selected_category.getEvents().remove(new Event_Key (selected_event.getDate(), selected_event.getNumber()));
            selected_event.setEvent_score(event_score);
           // this.selected_category.getEvents().put(new Event_Key (selected_event.getDate(), selected_event.getNumber()), selected_event);
           // Main.schedule.getEvents_by_id ().remove (selected_event.getId ());
           // Main.schedule.getEvents_by_id ().put (selected_event.getId (), selected_event);
           // System.out.println ("away score after setting: " + selected_event.getEvent_score ().getEvent_score_items ().get (0).getScore ());
           // update_event (selected_event);

            //int selected_event_index = events_Table.getSelectedRow ();
            int selected_event_item_index = event_items_Table.getSelectedRow ();
            //category_changed ();
            //events_Table.setRowSelectionInterval (selected_event_index, selected_event_index);
            Event_Time event_time = selected_event.getEvent_time ();
            String time;
            if (event_time == null || event_time.isTBA ())
                time = "TBA";
            else
                time = DateTimeUtils.get_pacific_date_time_string_for_viewing (event_time.getTime ());
                //time = DateTimeUtils.get_date_time_string_with_zone (event_time.getTime (), DateTimeUtils.pacific_zone_ID);

            String status1    = "";
            String status2    = "";
            String status0    = "";
            if (event_score != null && event_score.getEvent_score_items ().size () >= 2)
                {
                status1 = event_score.getStatus1 ();
                status2 = event_score.getStatus2 ();
                status0 = event_score.getStatus0 ();
                }
            Object [] updatedRowData = new Object [] {selected_event.isExclude (), "" + selected_event.getId (), time, "" + selected_event.getNumber (), "" + selected_event.getDouble_header (), status1, status2, status0, false, false};

            int row = events_Table.getSelectedRow ();

            for (int col = 0; col < updatedRowData.length; col++)
                event_model.setValueAt (updatedRowData[col], row, col);
            event_model.fireTableRowsUpdated (row, row);

            event_changed ();
            event_item_changed ();
            //save_score ();
            if (selected_event_item_index >= 0)
                event_items_Table.setRowSelectionInterval (selected_event_item_index, selected_event_item_index);
            }
        }
//---------------------------------------------------------------------------------------------
    public void update_event (Event event)
        {
        Event_Time event_time = event.getEvent_time ();
        String time;
        if (event_time == null || event_time.isTBA ())
            time = "TBA";
        else
            time = DateTimeUtils.get_date_time_string_with_zone (event_time.getTime (), DateTimeUtils.pacific_zone_ID);

        String status0    = "";
        String status1    = "";
        String status2    = "";
        Event_Score event_score = event.getEvent_score ();
        if (event_score != null && event_score.getEvent_score_items ().size () >= 2)
            {
            status1 = event_score.getStatus1 ();
            System.out.println ("NEW STATSU1: " + status1);
            status2 = event_score.getStatus2 ();
            status0 = event_score.getStatus0 ();
            }

        Object updatedRowData [] = new Object [] {event.isExclude (), "" + event.getId (), time, "" + event.getNumber (),"" + event.getDouble_header (), status1, status2, status0, false, false};

        int row = events_Table.getSelectedRow ();

        for (int col = 0; col < updatedRowData.length; col++)
            event_model.setValueAt (updatedRowData[col], row, col);
        event_model.fireTableRowsUpdated (row, row);
        }
//-------------------------------------------------------------------------------------------------
    public void send_category_event_hdf ()
        {
        String data_text = "";
        data_text += "CATEGORY_EVENT>>>\n";
        data_text += "[category_events{updated,category_event{category_id,event_id}}]\n";
        data_text += "{";
        data_text += DateTimeUtils.get_current_pacific_time ();
        data_text += "{";
        data_text += selected_category.getId () + ",";
        data_text += selected_event.getId ();
        data_text += "}";
        data_text += "}\n";
        data_text += "<<<";

        System.out.println (data_text);
        Main.schedule_client.send (data_text);

        TreeMap <Integer, Event_Item> event_items = selected_event.getEvent_items ();
        Event_Item away_event_item = event_items.get (0);
        Event_Item home_event_item = event_items.get (1);

        data_text = "";
        String hdf_header = "[event_item_league_teams{updated,event_item_league_team{event_item_id,league_team_id}}]";
        String title = "EVENT_ITEM_LEAGUE_TEAM" ;
        int id = -1;
        //EVENT_ITEM_LEAGUE_TEAM
        if (away_event_item != null)
            {
            Event_Item_League_Team away_event_item_league_team = away_event_item.getEvent_item_league_team ();
            if (away_event_item_league_team != null)
                {
                League_Team away_league_team = away_event_item_league_team.getLeague_team ();
                if (away_league_team != null)
                    {
                    id = away_league_team.getId ();
                    }
                }

            Event_Item_League_Team away_event_item_league_temp = away_event_item.getEvent_item_league_team ();
            if (away_event_item_league_temp != null)
                {
                League_Team away_league_temp = away_event_item_league_temp.getLeague_team ();
                if (away_league_temp != null)
                    {
                    title = "EVENT_ITEM_LEAGUE_TEAM";
                    hdf_header = "[event_item_league_teams{updated,event_item_league_team{event_item_id,league_team_id}}]";
                    id = away_league_temp.getId ();
                    }
                }

            data_text += title + ">>>\n";
            data_text += hdf_header + "\n";
            data_text += "{";
            data_text += DateTimeUtils.get_current_pacific_time ();
            data_text += "{";
            data_text += away_event_item.getId () + ",";
            data_text += id;
            data_text += "}";
            data_text += "}\n";
            data_text += "<<<";

            System.out.println (data_text);
            Main.schedule_client.send (data_text);
            }

        data_text = "";
        hdf_header = "[event_item_league_teams{updated,event_item_league_team{event_item_id,league_team_id}}]";
        title = "EVENT_ITEM_LEAGUE_TEAM" ;
        id = -1;
        if (home_event_item != null)
            {
            Event_Item_League_Team home_event_item_league_team = home_event_item.getEvent_item_league_team ();
            if (home_event_item_league_team != null)
                {
                League_Team home_league_team = home_event_item_league_team.getLeague_team ();
                if (home_league_team != null)
                    {
                    id = home_league_team.getId ();
                    }
                }

            Event_Item_League_Team home_event_item_league_temp = home_event_item.getEvent_item_league_team ();
            if (home_event_item_league_temp != null)
                {
                League_Team home_league_temp = home_event_item_league_temp.getLeague_team ();
                if (home_league_temp != null)
                    {
                    title = "EVENT_ITEM_LEAGUE_TEAM";
                    hdf_header = "[event_item_league_teams{updated,event_item_league_team{event_item_id, league_team_id}}]";
                    id = home_league_temp.getId ();
                    }
                }

            //[event_League_teams{updated,event_League_Team{event_item_id, league_team_id}}]
            data_text += title + ">>>\n";
            data_text += hdf_header + "\n";
            data_text += "{";
            data_text += DateTimeUtils.get_current_pacific_time ();
            data_text += "{";
            data_text += home_event_item.getId () + ",";
            data_text += id;
            data_text += "}";
            data_text += "}\n";
            data_text += "<<<";

            System.out.println (data_text);
            Main.schedule_client.send (data_text);
             }
        }

//-------------------------------------------------------------------------------------------------
//    public void set_event_item_league_team_player_id (int event_item_league_team_player_id)
//        {
//        Player_Item new_pitcher = Main.pitchers_dialog.getSelected_pitcher ();
//        League_Team_Player new_pitcher_league_team_player = League_Team_Player.getLeague_team_player_by_ID ().get (new_pitcher.getLeague_team_player_id ());
//        this.selected_event_item.getEvent_item_league_team ().getLeague_team_players ().get (this)
//        new_pitcher_league_team_player.setId (event_item_league_team_player_id);
//        this.selected_event_item.getEvent_item_league_team ().getLeague_team_players ().replace (1, new_pitcher_league_team_player);
//        event_changed (selected_event_item);
//        }
//-------------------------------------------------------------------------------------------------
    public void event_message_button_selected ()
        {
        if (selected_event == null)
            JOptionPane.showMessageDialog (this, "Please select an event to send a message");
        else
            {
            if (Main.message_dialog == null)
                {
                Main.message_dialog = new MessageDialog (this, true);
                }

            Main.message_dialog.initialize_fields_for_event (selected_league.getId (), selected_event);
            Main.message_dialog.setVisible (true);
            }
        }
//-------------------------------------------------------------------------------------------------
    public void category_message_button_selected ()
        {
        if (selected_category == null)
            JOptionPane.showMessageDialog (this, "Please select a category to send a message");
        else
            {
            if (Main.message_dialog == null)
                {
                Main.message_dialog = new MessageDialog (this, true);
                }

            Main.message_dialog.initialize_fields_for_category (selected_league.getId (), selected_category);
            Main.message_dialog.setVisible (true);
            }
        }
//-------------------------------------------------------------------------------------------------
    public void event_time_for_category_button_selected ()
        {
        if (selected_category == null)
            JOptionPane.showMessageDialog (this, "Please select a category to send a message");
        else
            {
            if (Main.event_times_dialog == null)
                {
                Main.event_times_dialog = new EventTimesDialog (this, true);
                }

            Main.event_times_dialog.initialize_fields (selected_category);
            Main.event_times_dialog.setVisible (true);

            if (Main.event_times_dialog.isSaved ())
                {
                OffsetDateTime offset_date_time = Main.event_times_dialog.getOffset_date_time ();
                TreeMap <Integer, Event> selected_events_map = Main.event_times_dialog.getSelected_events_map ();
                StringBuilder sql = new StringBuilder ("UPDATE Event_Time SET time='")
                                                       .append (DateTimeUtils.get_date_time_string_TZ2 (offset_date_time))
                                                       .append ("', override = 1 WHERE event_id IN (")
                                                       ;

                String comma = "";
                int selected_row_index = -1;
                for (int i = 0; i < events_for_selected_category.size (); i++)
                    {
                    Event event_in_category_frame = events_for_selected_category.get (i);
                    if (selected_event != null && selected_event.getId () == event_in_category_frame.getId ())
                        selected_row_index = i;
                    Event current_event = selected_events_map.get (event_in_category_frame.getId ());
                    if (current_event != null)
                        {
                        current_event.getEvent_time ().setTime (offset_date_time);

                        sql.append (comma)
                           .append (event_in_category_frame.getId ())
                           ;
                        comma = ",";
                        }
                    }

                sql.append (");");
                Debug.print (sql);
                try
                    {
                    Main.db.executeUpdate (sql);
                    for (int i = 0; i < events_for_selected_category.size (); i++)
                        {
                        Event event_in_category_frame = events_for_selected_category.get (i);
                        Event current_event = selected_events_map.get (event_in_category_frame.getId ());
                        if (current_event != null)
                            Main.send_schedule_changed_for_event (current_event);
                        }
                    }
                catch (Exception ex)
                    {
                    Logger.getLogger (CategoriesFrame.class.getName()).log (Level.SEVERE, null, ex);
                    }

                category_changed ();

                if (selected_row_index >= 0)
                    events_Table.setRowSelectionInterval (selected_row_index, selected_row_index);
                }
            }
        }
//-------------------------------------------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        schedule_date_Label = new javax.swing.JLabel();
        close_Button = new javax.swing.JButton();
        all_SplitPane = new javax.swing.JSplitPane();
        top_SplitPane = new javax.swing.JSplitPane();
        leagues_Panel = new javax.swing.JPanel();
        leagues_ScrollPane = new javax.swing.JScrollPane();
        leagues_Table = new javax.swing.JTable();
        leagues_label_Panel = new javax.swing.JPanel();
        leagues_Label = new javax.swing.JLabel();
        show_all_leagues_for_sport_CheckBox = new javax.swing.JCheckBox();
        categories_Panel = new javax.swing.JPanel();
        categories_ScrollPane = new javax.swing.JScrollPane();
        categories_Table = new javax.swing.JTable();
        category_buttons_Panel = new javax.swing.JPanel();
        categories_Label = new javax.swing.JLabel();
        add_category_Button = new javax.swing.JButton();
        edit_category_Button = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        bottom_SplitPane = new javax.swing.JSplitPane();
        event_Panel = new javax.swing.JPanel();
        event_buttons_Panel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        add_event_Button = new javax.swing.JButton();
        edit_event_Button = new javax.swing.JButton();
        score_Button = new javax.swing.JButton();
        time_Button = new javax.swing.JButton();
        resend_Button = new javax.swing.JButton();
        lines_Button = new javax.swing.JButton();
        message_Button = new javax.swing.JButton();
        events_ScrollPane = new javax.swing.JScrollPane();
        events_Table = new javax.swing.JTable();
        event_item_Panel = new javax.swing.JPanel();
        event_item_buttons_Panel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        team_Button = new javax.swing.JButton();
        pitcher_Button = new javax.swing.JButton();
        event_items_ScrollPane = new javax.swing.JScrollPane();
        event_items_Table = new javax.swing.JTable();
        sport_ComboBox = new javax.swing.JComboBox<>();
        sport_Label = new javax.swing.JLabel();
        parse_Button = new javax.swing.JButton();

        setTitle("Categories and Events");
        addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                formComponentResized(evt);
            }
        });

        schedule_date_Label.setText("Schedule date");

        close_Button.setText("Close");
        close_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                close_ButtonActionPerformed(evt);
            }
        });

        all_SplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        top_SplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        leagues_Panel.setMinimumSize(new java.awt.Dimension(16, 21));
        leagues_Panel.setPreferredSize(new java.awt.Dimension(984, 200));
        leagues_Panel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                leagues_PanelMouseReleased(evt);
            }
        });
        leagues_Panel.setLayout(new java.awt.GridBagLayout());

        leagues_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {
                "League ID", "Name"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean []
            {
                false, false
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit [columnIndex];
            }
        });
        leagues_Table.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                leagues_TableMouseReleased(evt);
            }
        });
        leagues_ScrollPane.setViewportView(leagues_Table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 968;
        gridBagConstraints.ipady = 140;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        leagues_Panel.add(leagues_ScrollPane, gridBagConstraints);

        leagues_label_Panel.setLayout(new java.awt.GridBagLayout());

        leagues_Label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        leagues_Label.setText("  Leagues");
        leagues_Label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 732;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 1.0;
        leagues_label_Panel.add(leagues_Label, gridBagConstraints);

        show_all_leagues_for_sport_CheckBox.setText("Show all leagues for sport");
        show_all_leagues_for_sport_CheckBox.setToolTipText("");
        show_all_leagues_for_sport_CheckBox.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        show_all_leagues_for_sport_CheckBox.setMaximumSize(new java.awt.Dimension(38, 20));
        show_all_leagues_for_sport_CheckBox.setMinimumSize(new java.awt.Dimension(38, 20));
        show_all_leagues_for_sport_CheckBox.setPreferredSize(new java.awt.Dimension(38, 20));
        show_all_leagues_for_sport_CheckBox.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                show_all_leagues_for_sport_CheckBoxStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        leagues_label_Panel.add(show_all_leagues_for_sport_CheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        leagues_Panel.add(leagues_label_Panel, gridBagConstraints);

        top_SplitPane.setTopComponent(leagues_Panel);

        categories_Panel.setMinimumSize(new java.awt.Dimension(16, 16));
        categories_Panel.setPreferredSize(new java.awt.Dimension(972, 300));
        categories_Panel.setLayout(new java.awt.GridBagLayout());

        categories_ScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        categories_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {
                "Exclude", "Category ID", "Date", "End-date", "Header"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean []
            {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit [columnIndex];
            }
        });
        categories_Table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        categories_Table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        categories_Table.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                categories_TableMouseReleased(evt);
            }
        });
        categories_ScrollPane.setViewportView(categories_Table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        categories_Panel.add(categories_ScrollPane, gridBagConstraints);

        category_buttons_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        categories_Label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        categories_Label.setText("  Categories");

        add_category_Button.setText("Add");
        add_category_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                add_category_ButtonActionPerformed(evt);
            }
        });

        edit_category_Button.setText("Edit");
        edit_category_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                edit_category_ButtonActionPerformed(evt);
            }
        });

        jButton1.setText("Message");
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Event Times");
        jButton2.setToolTipText("");
        jButton2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout category_buttons_PanelLayout = new javax.swing.GroupLayout(category_buttons_Panel);
        category_buttons_Panel.setLayout(category_buttons_PanelLayout);
        category_buttons_PanelLayout.setHorizontalGroup(
            category_buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(category_buttons_PanelLayout.createSequentialGroup()
                .addComponent(categories_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jButton2)
                .addGap(1, 1, 1)
                .addComponent(jButton1)
                .addGap(1, 1, 1)
                .addComponent(add_category_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(edit_category_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(230, Short.MAX_VALUE))
        );
        category_buttons_PanelLayout.setVerticalGroup(
            category_buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(category_buttons_PanelLayout.createSequentialGroup()
                .addGroup(category_buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(categories_Label)
                    .addGroup(category_buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(add_category_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)
                        .addComponent(jButton2)
                        .addComponent(edit_category_Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 761;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        categories_Panel.add(category_buttons_Panel, gridBagConstraints);

        top_SplitPane.setBottomComponent(categories_Panel);

        all_SplitPane.setTopComponent(top_SplitPane);

        bottom_SplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        event_Panel.setMinimumSize(new java.awt.Dimension(16, 16));
        event_Panel.setPreferredSize(new java.awt.Dimension(450, 400));
        event_Panel.setLayout(new java.awt.GridBagLayout());

        event_buttons_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("  Events");

        add_event_Button.setText("Add");
        add_event_Button.setEnabled(false);
        add_event_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                add_event_ButtonActionPerformed(evt);
            }
        });

        edit_event_Button.setText("Edit ");
        edit_event_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                edit_event_ButtonActionPerformed(evt);
            }
        });

        score_Button.setText("Score");
        score_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                score_ButtonActionPerformed(evt);
            }
        });

        time_Button.setText("Time");
        time_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                time_ButtonActionPerformed(evt);
            }
        });

        resend_Button.setText("Resend");
        resend_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                resend_ButtonActionPerformed(evt);
            }
        });

        lines_Button.setText("Lines");
        lines_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                lines_ButtonActionPerformed(evt);
            }
        });

        message_Button.setText("Message");
        message_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                message_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout event_buttons_PanelLayout = new javax.swing.GroupLayout(event_buttons_Panel);
        event_buttons_Panel.setLayout(event_buttons_PanelLayout);
        event_buttons_PanelLayout.setHorizontalGroup(
            event_buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(event_buttons_PanelLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 471, Short.MAX_VALUE)
                .addComponent(message_Button)
                .addGap(1, 1, 1)
                .addComponent(lines_Button)
                .addGap(0, 0, 0)
                .addComponent(resend_Button)
                .addGap(0, 0, 0)
                .addComponent(time_Button)
                .addGap(0, 0, 0)
                .addComponent(score_Button)
                .addGap(0, 0, 0)
                .addComponent(add_event_Button)
                .addGap(0, 0, 0)
                .addComponent(edit_event_Button))
        );
        event_buttons_PanelLayout.setVerticalGroup(
            event_buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(event_buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(edit_event_Button)
                .addComponent(add_event_Button)
                .addComponent(score_Button)
                .addComponent(time_Button)
                .addComponent(resend_Button)
                .addComponent(lines_Button)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(message_Button))
        );

        event_buttons_PanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {add_event_Button, edit_event_Button, jLabel1});

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 538;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        event_Panel.add(event_buttons_Panel, gridBagConstraints);

        events_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String []
            {
                "Exclude", "Event ID", "Date/Time", "Number", "GM#", "Status1", "Status2", "Status0", "Away Posession", "Home Possession"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean []
            {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit [columnIndex];
            }
        });
        events_Table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        events_Table.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                events_TableMouseReleased(evt);
            }
        });
        events_ScrollPane.setViewportView(events_Table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 850;
        gridBagConstraints.ipady = 339;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        event_Panel.add(events_ScrollPane, gridBagConstraints);

        bottom_SplitPane.setTopComponent(event_Panel);

        event_item_Panel.setMinimumSize(new java.awt.Dimension(16, 21));
        event_item_Panel.setLayout(new java.awt.GridBagLayout());

        event_item_buttons_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("  Event-Items");

        team_Button.setText("Team");
        team_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                team_ButtonActionPerformed(evt);
            }
        });

        pitcher_Button.setText("Pitcher");
        pitcher_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pitcher_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout event_item_buttons_PanelLayout = new javax.swing.GroupLayout(event_item_buttons_Panel);
        event_item_buttons_Panel.setLayout(event_item_buttons_PanelLayout);
        event_item_buttons_PanelLayout.setHorizontalGroup(
            event_item_buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(event_item_buttons_PanelLayout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1012, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(team_Button)
                .addGap(0, 0, 0)
                .addComponent(pitcher_Button)
                .addGap(0, 0, 0))
        );
        event_item_buttons_PanelLayout.setVerticalGroup(
            event_item_buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(event_item_buttons_PanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(event_item_buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(team_Button)
                    .addComponent(pitcher_Button))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 409;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        event_item_Panel.add(event_item_buttons_Panel, gridBagConstraints);

        event_items_ScrollPane.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                event_items_ScrollPaneMouseReleased(evt);
            }
        });

        event_items_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {
                "Item ID", "Number", "Team", "Pitcher", "Score"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean []
            {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit [columnIndex];
            }
        });
        event_items_Table.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                event_items_TableMouseReleased(evt);
            }
        });
        event_items_ScrollPane.setViewportView(event_items_Table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 752;
        gridBagConstraints.ipady = 283;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        event_item_Panel.add(event_items_ScrollPane, gridBagConstraints);

        bottom_SplitPane.setBottomComponent(event_item_Panel);

        all_SplitPane.setBottomComponent(bottom_SplitPane);

        sport_ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                sport_ComboBoxActionPerformed(evt);
            }
        });

        sport_Label.setText("Sport:");

        parse_Button.setText("Parse");
        parse_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                parse_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(all_SplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1158, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(schedule_date_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sport_Label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sport_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(parse_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(close_Button)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(schedule_date_Label)
                    .addComponent(sport_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sport_Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(all_SplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(close_Button)
                    .addComponent(parse_Button))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void close_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_close_ButtonActionPerformed
    {//GEN-HEADEREND:event_close_ButtonActionPerformed
        Main.categories_frame.setVisible (false);
    }//GEN-LAST:event_close_ButtonActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentResized
    {//GEN-HEADEREND:event_formComponentResized
        frame_resized ();
    }//GEN-LAST:event_formComponentResized

    private void sport_ComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sport_ComboBoxActionPerformed
    {//GEN-HEADEREND:event_sport_ComboBoxActionPerformed
        sport_changed ("sport_ComboBoxActionPerformed");
    }//GEN-LAST:event_sport_ComboBoxActionPerformed

    private void categories_TableMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_categories_TableMouseReleased
    {//GEN-HEADEREND:event_categories_TableMouseReleased
        category_changed ();
    }//GEN-LAST:event_categories_TableMouseReleased

    private void parse_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_parse_ButtonActionPerformed
    {//GEN-HEADEREND:event_parse_ButtonActionPerformed
        ParseDialog.main (this);
    }//GEN-LAST:event_parse_ButtonActionPerformed

    private void score_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_score_ButtonActionPerformed
    {//GEN-HEADEREND:event_score_ButtonActionPerformed
        edit_score ();
    }//GEN-LAST:event_score_ButtonActionPerformed

    private void add_category_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_add_category_ButtonActionPerformed
    {//GEN-HEADEREND:event_add_category_ButtonActionPerformed
        add_category ();
    }//GEN-LAST:event_add_category_ButtonActionPerformed

    private void edit_category_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_edit_category_ButtonActionPerformed
    {//GEN-HEADEREND:event_edit_category_ButtonActionPerformed
        edit_category ();
    }//GEN-LAST:event_edit_category_ButtonActionPerformed

    private void add_event_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_add_event_ButtonActionPerformed
    {//GEN-HEADEREND:event_add_event_ButtonActionPerformed
        add_event ();
    }//GEN-LAST:event_add_event_ButtonActionPerformed

    private void team_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_team_ButtonActionPerformed
    {//GEN-HEADEREND:event_team_ButtonActionPerformed
        select_team ();
    }//GEN-LAST:event_team_ButtonActionPerformed

    private void pitcher_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pitcher_ButtonActionPerformed
    {//GEN-HEADEREND:event_pitcher_ButtonActionPerformed
        select_pitcher ();
    }//GEN-LAST:event_pitcher_ButtonActionPerformed

    private void time_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_time_ButtonActionPerformed
    {//GEN-HEADEREND:event_time_ButtonActionPerformed
        change_time ();
    }//GEN-LAST:event_time_ButtonActionPerformed

    private void edit_event_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_edit_event_ButtonActionPerformed
    {//GEN-HEADEREND:event_edit_event_ButtonActionPerformed
        edit_event ();
    }//GEN-LAST:event_edit_event_ButtonActionPerformed

    private void leagues_PanelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_leagues_PanelMouseReleased
    {//GEN-HEADEREND:event_leagues_PanelMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_leagues_PanelMouseReleased

    private void leagues_TableMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_leagues_TableMouseReleased
    {//GEN-HEADEREND:event_leagues_TableMouseReleased
        league_changed ();
    }//GEN-LAST:event_leagues_TableMouseReleased

    private void event_items_ScrollPaneMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_event_items_ScrollPaneMouseReleased
    {//GEN-HEADEREND:event_event_items_ScrollPaneMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_event_items_ScrollPaneMouseReleased

    private void event_items_TableMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_event_items_TableMouseReleased
    {//GEN-HEADEREND:event_event_items_TableMouseReleased
        event_item_changed ();
    }//GEN-LAST:event_event_items_TableMouseReleased

    private void events_TableMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_events_TableMouseReleased
    {//GEN-HEADEREND:event_events_TableMouseReleased
        // TODO add your handling code here:
        event_changed ();
    }//GEN-LAST:event_events_TableMouseReleased

    private void resend_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resend_ButtonActionPerformed
    {//GEN-HEADEREND:event_resend_ButtonActionPerformed
        resend_event ();
    }//GEN-LAST:event_resend_ButtonActionPerformed

    private void lines_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lines_ButtonActionPerformed
    {//GEN-HEADEREND:event_lines_ButtonActionPerformed
        edit_lines ();
    }//GEN-LAST:event_lines_ButtonActionPerformed

    private void show_all_leagues_for_sport_CheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_show_all_leagues_for_sport_CheckBoxStateChanged
    {//GEN-HEADEREND:event_show_all_leagues_for_sport_CheckBoxStateChanged
        if (show_all_leagues_for_sport == show_all_leagues_for_sport_CheckBox.isSelected ())
            Debug.print ("show_all_leagues_for_sport is same");
        else
            {
            Debug.print ("show_all_leagues_for_sport is NOT same (" + show_all_leagues_for_sport + ")(" + show_all_leagues_for_sport_CheckBox.isSelected () + ")");
            show_all_leagues_for_sport = show_all_leagues_for_sport_CheckBox.isSelected ();
            sport_changed ("show_all_leagues_for_sport_CheckBoxStateChanged");
            }
    }//GEN-LAST:event_show_all_leagues_for_sport_CheckBoxStateChanged

    private void message_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_message_ButtonActionPerformed
    {//GEN-HEADEREND:event_message_ButtonActionPerformed

    event_message_button_selected ();

    }//GEN-LAST:event_message_ButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
    category_message_button_selected ();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton2ActionPerformed
    {//GEN-HEADEREND:event_jButton2ActionPerformed
    event_time_for_category_button_selected ();
    }//GEN-LAST:event_jButton2ActionPerformed

    public static void main ()
        {
        java.awt.EventQueue.invokeLater (() ->
            {
            Main.categories_frame = new CategoriesFrame ();
            Main.categories_frame.setSize (1000, 800);
            Main.categories_frame.setLocation (Main.schedule_frame.getWidth (), Main.schedule_frame.getY ());
            Main.categories_frame.setVisible (true);
            Main.categories_frame.initialize (Main.schedule_frame.getSelected_schedule_id ());

            //System.out.println ("WIDTH: " + Main.categories_frame.getWidth ());
//            Main.loading_frame.setLocation (Main.categories_frame.getWidth (), Main.categories_frame.getY ());
            });
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add_category_Button;
    private javax.swing.JButton add_event_Button;
    private javax.swing.JSplitPane all_SplitPane;
    private javax.swing.JSplitPane bottom_SplitPane;
    private javax.swing.JLabel categories_Label;
    private javax.swing.JPanel categories_Panel;
    private javax.swing.JScrollPane categories_ScrollPane;
    private javax.swing.JTable categories_Table;
    private javax.swing.JPanel category_buttons_Panel;
    private javax.swing.JButton close_Button;
    private javax.swing.JButton edit_category_Button;
    private javax.swing.JButton edit_event_Button;
    private javax.swing.JPanel event_Panel;
    private javax.swing.JPanel event_buttons_Panel;
    private javax.swing.JPanel event_item_Panel;
    private javax.swing.JPanel event_item_buttons_Panel;
    private javax.swing.JScrollPane event_items_ScrollPane;
    private javax.swing.JTable event_items_Table;
    private javax.swing.JScrollPane events_ScrollPane;
    private javax.swing.JTable events_Table;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel leagues_Label;
    private javax.swing.JPanel leagues_Panel;
    private javax.swing.JScrollPane leagues_ScrollPane;
    private javax.swing.JTable leagues_Table;
    private javax.swing.JPanel leagues_label_Panel;
    private javax.swing.JButton lines_Button;
    private javax.swing.JButton message_Button;
    private javax.swing.JButton parse_Button;
    private javax.swing.JButton pitcher_Button;
    private javax.swing.JButton resend_Button;
    private javax.swing.JLabel schedule_date_Label;
    private javax.swing.JButton score_Button;
    private javax.swing.JCheckBox show_all_leagues_for_sport_CheckBox;
    private javax.swing.JComboBox<String> sport_ComboBox;
    private javax.swing.JLabel sport_Label;
    private javax.swing.JButton team_Button;
    private javax.swing.JButton time_Button;
    private javax.swing.JSplitPane top_SplitPane;
    // End of variables declaration//GEN-END:variables
    }
