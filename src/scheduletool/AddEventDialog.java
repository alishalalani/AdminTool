/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool;

import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.data.Category;
import gsutils.data.Event;
import gsutils.data.Event_Item;
import gsutils.data.Event_Item_League_Team;
import gsutils.data.Event_Time;
import gsutils.data.League;
import gsutils.data.League_Position;
import gsutils.data.League_Team;
import gsutils.data.League_Team_Player;
import gsutils.data.Event_Key;
import gsutils.data.Event_Venue;
import gsutils.data.Location;
import gsutils.data.Sport;
import gsutils.data.Venue;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import scheduletool.datetime.DateTimeDialog;
//-----------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------
public class AddEventDialog extends javax.swing.JDialog
    {
    private LocalDate     date_value;
    private LocalTime     time_value;
    private LocalDateTime local_date_time;
    private League        league;
    private Category      category;
    private League_Team   away_league_team;
    private League_Team   home_league_team;
    private League_Team   away_league_temp;
    private League_Team   home_league_temp;
    private int           double_header = 0;

    private ArrayList <Category> categories;

    //for edit event
    private Event       original_event;
    private Event       selected_event;
    private Event_Venue selected_event_venue;
    private Category    selected_category;
    private boolean     edit_mode = false;
    private int         event_number;
    private TreeMap <Integer, Event_Item> event_items;

    private Event_Item         away_event_item;
    private Event_Item         home_event_item;
    private League_Team_Player away_pitcher;
    private League_Team_Player home_pitcher;

    private boolean saved = false;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public AddEventDialog (java.awt.Frame parent, boolean modal)
        {
        super (parent, modal);
        initComponents ();

        // Remove default button styling
        date_time_Button.setBorderPainted     (false);
        date_time_Button.setFocusPainted      (false);
        date_time_Button.setContentAreaFilled (false);
        //date_time_Button.setPreferredSize (new Dimension (30, 30));

        int       buttonWidth  = date_time_Button.getWidth  ();
        int       buttonHeight = date_time_Button.getHeight ();
        if (Main.originalImage != null)
            {
            Image     scaledImage  = Main.originalImage.getScaledInstance (buttonWidth, buttonHeight, Image.SCALE_DEFAULT);
            ImageIcon icon         = new ImageIcon (scaledImage);
            date_time_Button.setIcon (icon);
            }
        game_number_error_Label.setForeground (Color.RED);
        }
    //---------------------------------------------------------------------------------------------
    // initialize when editing an event
    //---------------------------------------------------------------------------------------------
    public void initialize_fields_edit_event (Event event, Category category, ArrayList <Category> categories)
        {
        this.edit_mode = true;

        this.original_event    = event;
        this.selected_event    = new Event ();
        this.category          = category;
        this.selected_category = category;
        this.league            = event.getLeague ();
        this.categories        = categories;

        selected_event = new Event (original_event);

        category_header_TextArea.setText (selected_category.getHeader ());
        league_value_Label      .setText (league.getName ());
        event_number_TextField  .setText (event.getNumber () + "");
        id_Label                .setText ("Event ID: " + event.getId ());

        set_date_time (event.getEvent_time().getTime(), event.getEvent_time().isTBA (), event.getEvent_time ().isOverride ());
        set_teams (event);

        //date_time_Button.setText ("Change Date/Time");
        exclude_CheckBox.setSelected (original_event.isExclude ());
        //add_away_team_Button.setText ("Change Away Team");
        //add_home_team_Button.setText ("Change Home Team");

        initialize_double_header_buttons ();

        this.double_header = event.getDouble_header();
        switch (double_header)
            {
            case 0:
                not_selected_radio_btn.setSelected (true);
                break;
            case 1:
                game1_radio_btn.setSelected (true);
                break;
            case 2:
                game2_radio_btn.setSelected (true);
                break;
            default:
                break;
            }

       if (league.getMain_league_id () == League.MLB)
           {
           TreeMap <Integer, Event_Item> local_event_items = event.getEvent_items ();
           away_event_item = local_event_items.get (0);
           //set pitchers
           Event_Item_League_Team event_item_league_team = away_event_item.getEvent_item_league_team();
            if (event_item_league_team != null)
                {
                TreeMap <Integer, League_Team_Player> players = event_item_league_team.getLeague_team_players();
                if (players.size () >= 1)
                    {
                    away_pitcher = players.get (League_Position.MLB_PITCHER);
                    }
                }

            home_event_item = local_event_items.get (1);
            event_item_league_team = home_event_item.getEvent_item_league_team();
            if (event_item_league_team != null)
                {
                TreeMap <Integer, League_Team_Player> players = event_item_league_team.getLeague_team_players();
                if (players.size () >= 1)
                    {
                    home_pitcher = players.get (League_Position.MLB_PITCHER);
                    }
                }

           if (   away_pitcher != null
               && away_pitcher.getLeague_player () != null
               && away_pitcher.getLeague_player ().getPlayer () != null)
               {
               away_pitcher_TextField.setText (away_pitcher.getLeague_player ().getPlayer ().getDisplay_name ());
               }
           away_pitcher_TextField.setVisible (true);

           if (   home_pitcher != null
               && home_pitcher.getLeague_player () != null
               && home_pitcher.getLeague_player ().getPlayer () != null)
               {
               home_pitcher_TextField.setText (home_pitcher.getLeague_player ().getPlayer ().getDisplay_name ());
               }
           home_pitcher_TextField.setVisible (true);
           }
       else
           {
           //away_pitcher_Label.setVisible (false);
           away_pitcher_TextField.setVisible (false);

           //home_pitcher_Label.setVisible (false);
           home_pitcher_TextField.setVisible (false);
           }

        game_number_error_Label.setText ("");

        venue_ComboBox.addItem ("<Not selected>");
        for (Venue venue : Venue.get_venues_by_name ().values ())
            venue_ComboBox.addItem (venue.getName ());

        selected_event_venue = selected_event.getEvent_venue ();
        if (selected_event_venue != null)
            {
            neutral_CheckBox .setSelected (selected_event_venue.isNeutral  ());
            override_CheckBox.setSelected (selected_event_venue.isOverride ());

            Venue venue = selected_event_venue.getVenue ();
            if (venue != null)
                venue_ComboBox.setSelectedItem (venue.getName ());
            else if (selected_event_venue.getVenue_name () == null || Venue.get_venue (selected_event_venue.getVenue_name ()) == null)
                venue_ComboBox.setSelectedIndex (0);
            else
                venue_ComboBox.setSelectedItem (selected_event_venue.getVenue_name ());

            venue_TextField  .setText     (selected_event_venue.getVenue_name () == null ? "" : selected_event_venue.getVenue_name ());
            city_TextField   .setText     (selected_event_venue.getCity_name  () == null || selected_event_venue.getCity_name  ().equals (", ") ? "" : selected_event_venue.getCity_name  ());
            }

        setTitle ("Edit Event");
        }
    //---------------------------------------------------------------------------------------------
    // initialize when adding an event
    //---------------------------------------------------------------------------------------------
    public void initialize_fields_add_event (League league, Category category, ArrayList <Category> categories)
        {
        this.away_league_team = null;
        this.away_league_temp = null;
        this.home_league_team = null;
        this.home_league_temp = null;

        this.date_value = null;
        this.time_value = null;

        this.original_event = new Event ();
        this.selected_event = new Event ();
        this.league         = league;
        this.category       = category;
        this.categories     = categories;

        league_value_Label      .setText (league.getName ());
        category_header_TextArea.setText (category.getHeader ());
        id_Label.setText ("Event ID: ---");

        date_value_Label         .setText ("Click calendar");
        time_value_Label         .setText ("To select");
        event_number_TextField    .setText ("");
        away_team_value_Textfield.setText ("Click to Select Away Team");
        home_team_value_Textfield.setText ("Click to Select Home Team");

        game_number_error_Label.setText ("");
        exclude_CheckBox.setSelected (false);

        initialize_double_header_buttons ();
        not_selected_radio_btn.setSelected (true);
        //double_header ();

        //away_pitcher_Label.setVisible (false);
        away_pitcher_TextField.setVisible (false);

        //home_pitcher_Label.setVisible (false);
        home_pitcher_TextField.setVisible (false);
        }
    //---------------------------------------------------------------------------------------------
    public boolean game_number_already_exists (String text)
        {
        LineBorder red   = new LineBorder (Color.RED);
        LineBorder black = new LineBorder (Color.BLACK);

        if (text.length () > 0)
            {
             // Check if the string is numerical
            boolean isNumerical = text.matches ("\\d+");
            if (isNumerical)
                {
                boolean duplicate_game_number_exists = false;

                Event old_event = Main.schedule.getEvents_by_date_and_number ().get (new Event_Key (selected_event.getDate (), event_number));
                if (old_event != null)
                    {
                    duplicate_game_number_exists = true;
                    }

                if (duplicate_game_number_exists)
                    {
                    event_number_Label      .setBorder (red);
                    event_number_TextField  .setBorder (red);
                    game_number_error_Label.setText ("Use Another Game Number");
                    return true;
                    }
                else
                    {
                    event_number_Label      .setBorder (black);
                    event_number_TextField  .setBorder (black);
                    game_number_error_Label.setText ("");
                    return false;
                    }
                }
            else
                {
                event_number_Label      .setBorder (red);
                event_number_TextField  .setBorder (red);
                game_number_error_Label.setText ("Enter Numbers Only");
                return true;
                }
            }
        else
            {
            event_number_Label      .setBorder (black);
            event_number_TextField  .setBorder (black);
            game_number_error_Label.setText ("");
            return false;
            }
        }
    //---------------------------------------------------------------------------------------------
    // LOL - useless function !!!
    //---------------------------------------------------------------------------------------------
    public static boolean containsEvenDigits (int number)
        {
        String numberString = String.valueOf (number);

        for (char c : numberString.toCharArray())
            {
            int digit = Character.getNumericValue (c);
            if (digit % 2 == 0)
                {
                return true;
                }
            }

        return false;
        }
    //---------------------------------------------------------------------------------------------
    private void set_teams (Event event)
        {
        event_items = event.getEvent_items();
        Event_Item away_item = event_items.get (0);
        boolean away = true;
        set_team_label (away_item, away);

        Event_Item home_item = event_items.get (1);
        set_team_label (home_item, !away);
        //away_team_value_Label.setText (.getLeague_team().getName());

        //Event_Item home_item = event_items.get (1);
        //home_team_value_Label.setText (home_item.getEvent_item_league_team().getLeague_team().getName());
        }
    //---------------------------------------------------------------------------------------------
    private void set_team_label (Event_Item event_item, boolean away)
        {
        Event_Item_League_Team team_item = event_item.getEvent_item_league_team ();
        Event_Item_League_Team team_temp = event_item.getEvent_item_league_team ();

        String text = "";
        if (team_item != null)
            {
            League_Team league_team = team_item.getLeague_team();
            if (league_team != null)
                {
                text = league_team.getName();
                if (away)
                    away_league_team = league_team;
                else
                    home_league_team = league_team;
                }
            }
        else if (team_temp != null)
            {
            League_Team league_temp = team_temp.getLeague_team ();
            if (league_temp != null)
                {
                text = league_temp.getTemp_name ();
                if (away)
                    away_league_temp = league_temp;
                else
                    home_league_temp = league_temp;
                }
            }

        if (away)
            away_game_number.setText ("" + event_item.getEvent ().getNumber ());
        else
            home_game_number.setText ("" + event_item.getEvent ().getNumber ());

        if (text.length() > 0)
            {
            if (away)
                away_team_value_Textfield.setText (text);
            else
                home_team_value_Textfield.setText (text);
            }

        }
    //---------------------------------------------------------------------------------------------
    private void add_away_team ()
        {
        TeamsDialog teamDialog;
        if (edit_mode)
            teamDialog = new TeamsDialog (Main.categories_frame, true, league.getId(), away_event_item, away_league_team, away_league_temp, true);
        else
            {
            System.out.println ("GOING IN THE ELSE");
            teamDialog = new TeamsDialog (Main.categories_frame, true, league.getId(), true);
            }

        teamDialog.setVisible (true);

        if (teamDialog.isSaved ())
            {
            League_Team selected_away_league_team = League_Team.get_league_team_by_ID (teamDialog.getSelected_team ());
            TreeMap <Integer, League_Team> league_temps = League_Team.getLeague_team_by_ID ();
            League_Team selected_away_league_temp = league_temps.get (teamDialog.getSelected_temp ());
            boolean set_away_team = true;
            set_team (selected_away_league_team, selected_away_league_temp, set_away_team);
            }
        else if (teamDialog.isRemoved ())
            {
            if (edit_mode)
                {
                Main.teams_dialog.remove_team_from_database (original_event, away_event_item);
                //selected_event.setEvent_score (null);
                }
            away_event_item.setEvent_item_league_team (null);
            away_league_team = null;
            away_league_temp = null;
            away_team_value_Textfield.setText ("Click to Select");

            away_pitcher = null;
            away_pitcher_TextField.setText ("Click to Select");
            }
        }
    //---------------------------------------------------------------------------------------------
    private void add_home_team ()
        {
        TeamsDialog teamDialog;
        if (edit_mode)
            teamDialog = new TeamsDialog (Main.categories_frame, true, league.getId(), home_event_item, home_league_team, home_league_temp, true);
        else
            teamDialog = new TeamsDialog (Main.categories_frame, true, league.getId(), true);

        teamDialog.setVisible (true);

        if (teamDialog.isSaved ())
            {
            League_Team selected_home_league_team = League_Team.get_league_team_by_ID (teamDialog.getSelected_team ());
            TreeMap <Integer, League_Team> league_temps = League_Team.getLeague_team_by_ID ();
            League_Team selected_home_league_temp = league_temps.get (teamDialog.getSelected_temp ());
            boolean set_away_team = false;
            set_team (selected_home_league_team, selected_home_league_temp, set_away_team);
            }
        if (!teamDialog.isSaved () && teamDialog.isRemoved ())
            {
            Event event = Main.schedule.getEvents_by_id ().get (home_event_item.getEvent ().getId ());
            event.setEvent_score (null);
            home_event_item.setEvent_item_league_team (null);
            home_league_team = null;
            home_league_temp = null;
            home_team_value_Textfield.setText ("Click to Select");

            home_pitcher = null;
            home_pitcher_TextField.setText ("Click to Select");
            }
        }
    //---------------------------------------------------------------------------------------------
    private void add_date_time ()
        {
        //ZoneId zone = ZoneId.of ("America/Los_Angeles");
        DateTimeDialog dateTime = new DateTimeDialog (this, true, original_event, true);
        dateTime.setVisible (true);

        if (dateTime.isSaved ())
            set_date_time (dateTime.getSelected_date (), dateTime.getSelected_time (), dateTime.isTBA (), dateTime.isOverride ());
        }
    //---------------------------------------------------------------------------------------------
    private void edit_date_time ()
        {
        DateTimeDialog dateTime = new DateTimeDialog (this, true, selected_event, true);
        dateTime.setVisible (true);

        if (dateTime.isSaved ())
            set_date_time (dateTime.getSelected_date (), dateTime.getSelected_time (), dateTime.isTBA (), dateTime.isOverride ());
        }
    //---------------------------------------------------------------------------------------------
    private void save ()
        {
        saved = true;
        setVisible (false);

        if (edit_mode)
            {
            edit_event_database ();
            Main.categories_frame.setEventOverride (selected_event.getEvent_time ().isOverride ());
            }
        else
            add_event_database ();
        }
    //---------------------------------------------------------------------------------------------
    public boolean isSaved ()
        {
        return saved;
        }
    //---------------------------------------------------------------------------------------------
    // edit event
    //---------------------------------------------------------------------------------------------
    private void save_fields_edit_event ()
        {
        this.selected_event.setDouble_header (double_header);
        String game_number_string = event_number_TextField.getText();
        if (game_number_string.length() > 0)
            {
            event_number = Integer.parseInt (game_number_string.trim());
            selected_event.setNumber (event_number);
            }
        else
            event_number = -1;

        if (date_value == null)
            date_value = original_event.getDate ();

        this.selected_event.setDate (date_value);

        if (time_value != null)
            {
            //set time
            LocalDateTime dateTime = LocalDateTime.of (date_value, time_value);
            long epoch_date_time = dateTime.atZone (ZoneId.of ("America/Los_Angeles")).toEpochSecond();
            OffsetDateTime offset_date_time = DateTimeUtils.get_pacific_date_time_from_seconds (epoch_date_time);
            selected_event.getEvent_time().setTime (offset_date_time);
            }
        //save teams
        Event_Item away_item = event_items.get (0);
        Event_Item home_item = event_items.get (1);

        if (away_league_team != null)
            {
            if (away_item.getEvent_item_league_team() == null)
                {
                Event_Item_League_Team event_item_league_team = new Event_Item_League_Team ();
                away_item.setEvent_item_league_team (event_item_league_team);
                }
            away_item.getEvent_item_league_team().setLeague_team (away_league_team);
            away_item.getEvent_item_league_team().setTimestamp (OffsetDateTime.now());

            if (away_pitcher != null)
                away_item.getEvent_item_league_team().getLeague_team_players ().put (League_Position.MLB_PITCHER, away_pitcher);
            }
        else if (away_league_temp != null)
            {
            if (away_item.getEvent_item_league_team () == null)
                {
                Event_Item_League_Team event_item_league_temp = new Event_Item_League_Team ();
                away_item.setEvent_item_league_team (event_item_league_temp);
                }
            away_item.getEvent_item_league_team ().setLeague_team (away_league_temp);
            away_item.getEvent_item_league_team ().setTimestamp (OffsetDateTime.now());
            }

        if (home_league_team != null)
            {
            if (home_item.getEvent_item_league_team() == null)
                {
                Event_Item_League_Team event_item_league_team = new Event_Item_League_Team ();
                home_item.setEvent_item_league_team (event_item_league_team);
                }
            home_item.getEvent_item_league_team().setLeague_team (home_league_team);
            home_item.getEvent_item_league_team().setTimestamp (OffsetDateTime.now());

            if (home_pitcher != null)
                home_item.getEvent_item_league_team().getLeague_team_players ().put (League_Position.MLB_PITCHER, home_pitcher);
            }
        else if (home_league_temp != null)
            {
            if (home_item.getEvent_item_league_team () == null)
                {
                Event_Item_League_Team event_item_league_temp = new Event_Item_League_Team ();
                home_item.setEvent_item_league_team (event_item_league_temp);
                }
            home_item.getEvent_item_league_team ().setLeague_team (home_league_temp);
            home_item.getEvent_item_league_team ().setTimestamp (OffsetDateTime.now());
            }

        double_header ();

        selected_event.getEvent_items ().replace (0, away_item);
        selected_event.getEvent_items ().replace (1, home_item);

        selected_event_venue = selected_event.getEvent_venue ();
        Venue venue = null;
        if (neutral_CheckBox.isSelected ())
            {
            boolean save_venue     = false;
            boolean override       = override_CheckBox.isSelected ();
            String  venue_string   = venue_TextField.getText ().trim ();
            String  city_string    = city_TextField .getText ().trim ();
            String  selected_venue = (String) venue_ComboBox.getSelectedItem ();
            int     venue_id       = -1;

            if (!selected_venue.equals ("<Not selected>"))
                {
                venue    = Venue.get_venue (selected_venue);
                venue_id = venue.getId ();
                }

            if (venue_string.length () > 0)
                {
                if (selected_event_venue != null)
                    {
                    if (   !venue_string.equals (selected_event_venue.getVenue_name ())
                        || !city_string .equals (selected_event_venue.getCity_name  ())
                        || override != selected_event_venue.isOverride ()
                        )
                        {
                        save_venue = true;
                        }
                    }
                }
            else if (venue_id >= 0)
                {
                if (   selected_event_venue == null
                    || selected_event_venue.getVenue () == null
                    || venue_id != selected_event_venue.getVenue ().getId ())
                    {
                    save_venue = true;
                    }
                }
            else
                {
                JOptionPane.showMessageDialog (this, "If you select Neutral then you have to select a venue from the pull-down or enter a venue name and city name");
                return;
                }

            if (save_venue)
                {
                Event_Venue event_venue = new Event_Venue ();
                event_venue.setNeutral    (true);
                event_venue.setOverride   (override_CheckBox.isSelected ());
                event_venue.setVenue (venue);
                event_venue.setVenue_name (venue_string);
                event_venue.setCity_name  (city_string );
                selected_event.setEvent_venue (event_venue);
                }
            }
        else
            selected_event.setEvent_venue (null);

        Main.categories_frame.save_screen_event_after_editing (selected_event);
        save ();
        }
    //---------------------------------------------------------------------------------------------
    private void save_fields_add_event ()
        {
        selected_event.setLeague (league);
        selected_event.setDouble_header (double_header);

//        selected_event.setDate (date_value);
//        //time
//        Event_Time event_time = new Event_Time ();
//        LocalDateTime dateTime = LocalDateTime.of (date_value, time_value);
//        long epoch_date_time = dateTime.atZone (ZoneId.of ("America/Los_Angeles")).toEpochSecond();
//        OffsetDateTime offset_date_time = DateTimeUtils.get_pacific_date_time_from_seconds (epoch_date_time);
//        event_time.setTime (offset_date_time);
//        selected_event.setEvent_time (event_time);

        String game_number_string = event_number_TextField.getText();
        if (game_number_string.length() > 0)
            {
            event_number = Integer.parseInt (game_number_string.trim());
            selected_event.setNumber (event_number);
            }
        else
            event_number = -1;

        double_header ();

        TreeMap <Integer, Event_Item> local_event_items = selected_event.getEvent_items();
        Event_Item away_item = new Event_Item ();
        Event_Item home_item = new Event_Item ();
        away_item.setEvent (selected_event);
        away_item.setSequence (0);
        home_item.setEvent (selected_event);
        home_item.setSequence (1);
        local_event_items.put (0, away_item);
        local_event_items.put (1, home_item);

//        System.out.println ("selected away: " + away_league_team.getName ());
//        System.out.println ("selected home: " + home_league_team.getName ());

        Event_Item_League_Team away_event_item_league_team = new Event_Item_League_Team ();
        away_event_item_league_team.setLeague_team (away_league_team);
        away_event_item_league_team.setEvent_item (away_item);
        away_item.setEvent_item_league_team (away_event_item_league_team);
        away_event_item_league_team.setTimestamp (OffsetDateTime.now());

        Event_Item_League_Team away_event_item_league_temp = new Event_Item_League_Team ();
        away_event_item_league_temp.setLeague_team (away_league_temp);
        away_item.setEvent_item_league_team (away_event_item_league_temp);

        Event_Item_League_Team home_event_item_league_team = new Event_Item_League_Team ();
        home_event_item_league_team.setLeague_team (home_league_team);
        home_event_item_league_team.setEvent_item (home_item);
        home_item.setEvent_item_league_team (home_event_item_league_team);
        home_event_item_league_team.setTimestamp (OffsetDateTime.now());

        Event_Item_League_Team home_event_item_league_temp = new Event_Item_League_Team ();
        home_event_item_league_temp.setLeague_team (home_league_temp);
        home_item.setEvent_item_league_team (home_event_item_league_temp);

        Main.categories_frame.save_event_after_adding (selected_event);

        save ();
        }
    //---------------------------------------------------------------------------------------------
    private void cancel ()
        {
        setVisible (false);
        dispose ();
        }
    //---------------------------------------------------------------------------------------------
    public void set_date_time (OffsetDateTime offset_date_time, boolean TBA, boolean override)
        {
        System.out.println ("IN SET DATE TIME !!!!!!!!!!!!");
        this.local_date_time = DateTimeUtils.pacific_to_local (offset_date_time);
        this.time_value = local_date_time.toLocalTime();
        this.date_value = local_date_time.toLocalDate();

        int month = local_date_time.getMonthValue();
        int day   = local_date_time.getDayOfMonth();
        int year  = local_date_time.getYear();
        String date_value_string = month + "/" + day + "/" + year;

        String am_pm = "am";
        int hour = local_date_time.getHour();
        if (hour == 12)
            am_pm = "pm";
        else if (hour == 0)
            hour = 12;
        else if (hour > 12)
            {
            hour -= 12;
            am_pm = "pm";
            }
        String minute;
        int minute_int = local_date_time.getMinute ();
        if (minute_int < 10)
            minute = "0" + minute_int;
        else
            minute = "" + minute_int;
        String time_value_string = hour + ":" + minute + am_pm;

        date_value_Label.setText (date_value_string);
        time_value_Label.setText (time_value_string);

        //set date and time
        selected_event.setDate (date_value);
        //time
        Event_Time event_time = new Event_Time ();
//        LocalDateTime dateTime = LocalDateTime.of (date_value, time_value);
//        long epoch_date_time = dateTime.atZone (ZoneId.of ("America/Los_Angeles")).toEpochSecond();
//        OffsetDateTime offset_date_time2 = DateTimeUtils.get_pacific_date_time_from_seconds (epoch_date_time);
        event_time.setTime (offset_date_time);
        event_time.setTBA (TBA);
        event_time.setOverride (override);

        selected_event.setEvent_time (event_time);

        double_header ();
        }
    //---------------------------------------------------------------------------------------------
    public void set_date_time (LocalDate date, LocalTime time, boolean TBA, boolean override)
        {
        this.date_value = date;
        this.time_value = time;

        int month = date_value.getMonthValue ();
        int day   = date_value.getDayOfMonth ();
        int year  = date_value.getYear       ();
        String date_value_string = month + "/" + day + "/" + year;

        String am_pm = "am";
        int hour = time_value.getHour();
        if (hour == 12)
            am_pm = "pm";
        else if (hour == 0)
            hour = 12;
        else if (hour > 12)
            {
            hour -= 12;
            am_pm = "pm";
            }
        String minute;
        int minute_int = time_value.getMinute();
        if (minute_int < 10)
            minute = "0" + minute_int;
        else
            minute = "" + minute_int;

        String time_value_string = (TBA ? "TBA" : hour + ":" + minute + am_pm);

        date_value_Label.setText (date_value_string);
        time_value_Label.setText (time_value_string);

        //set date and time
        selected_event.setDate (date_value);
        //time
        LocalDateTime dateTime = LocalDateTime.of (date_value, time_value);
        long epoch_date_time = dateTime.atZone (ZoneId.of ("America/Los_Angeles")).toEpochSecond();
        OffsetDateTime offset_date_time = DateTimeUtils.get_pacific_date_time_from_seconds (epoch_date_time);

        Event_Time event_time = selected_event.getEvent_time ();
        if (event_time == null)
            event_time = new Event_Time ();

        if (event_time.getTime () != null && event_time.getTime ().isEqual (offset_date_time) && event_time.isTBA () == TBA)
            {
            event_time.setTime (offset_date_time);
            event_time.setTBA (TBA);
            }

        event_time.setOverride (override);
        selected_event.setEvent_time (event_time);

        double_header ();
        }
    //---------------------------------------------------------------------------------------------
    public void set_team (League_Team team, League_Team temp, boolean set_away_team)
        {
        System.out.println ("IN SET TEAM!!!!!!!!!!!!!!!!");
        if (set_away_team)
            {
            this.away_league_team = team;
            this.away_league_temp = temp;
            if (team != null)
                {
                if (away_event_item != null)
                    {
                    if (away_event_item.getEvent_item_league_team() == null)
                        {
                        Event_Item_League_Team event_item_league_team = new Event_Item_League_Team ();
                        away_event_item.setEvent_item_league_team (event_item_league_team);
                        }
                    away_event_item.getEvent_item_league_team().setLeague_team (away_league_team);
                    away_event_item.getEvent_item_league_team().setTimestamp (OffsetDateTime.now());
                    away_event_item.getEvent_item_league_team ().getLeague_team_players ().remove (League_Position.MLB_PITCHER);
                    }
                away_team_value_Textfield.setText (team.getName());
                away_pitcher = null;
                away_pitcher_TextField.setText ("Click to Select");
                }
            else if (temp != null)
                {
                if (away_event_item != null)
                    {
                    if (away_event_item.getEvent_item_league_team () == null)
                        {
                        Event_Item_League_Team event_item_league_temp = new Event_Item_League_Team ();
                        away_event_item.setEvent_item_league_team (event_item_league_temp);
                        }
                    away_event_item.getEvent_item_league_team ().setLeague_team (away_league_temp);
                    away_event_item.getEvent_item_league_team ().setTimestamp (OffsetDateTime.now());
                    away_event_item.getEvent_item_league_team ().getLeague_team_players ().remove (League_Position.MLB_PITCHER);
                    }
                away_team_value_Textfield.setText (temp.getTemp_name ());
                away_pitcher = null;
                away_pitcher_TextField.setText ("Click to Select");
                }
            }
        else
            {
            this.home_league_team = team;
            this.home_league_temp = temp;
            if (team != null)
                {
                if (home_event_item != null)
                    {
                    if (home_event_item.getEvent_item_league_team() == null)
                        {
                        Event_Item_League_Team event_item_league_team = new Event_Item_League_Team ();
                        home_event_item.setEvent_item_league_team (event_item_league_team);
                        }
                    home_event_item.getEvent_item_league_team().setLeague_team (home_league_team);
                    home_event_item.getEvent_item_league_team().setTimestamp (OffsetDateTime.now());
                    home_event_item.getEvent_item_league_team ().getLeague_team_players ().remove (League_Position.MLB_PITCHER);
                    }
                home_team_value_Textfield.setText (team.getName());
                home_pitcher_TextField.setText ("Click to Select");
                home_pitcher = null;
                }
            else if (temp != null)
                {
                if (home_event_item != null)
                    {
                    if (home_event_item.getEvent_item_league_team () == null)
                        {
                        Event_Item_League_Team event_item_league_temp = new Event_Item_League_Team ();
                        home_event_item.setEvent_item_league_team (event_item_league_temp);
                        }
                    home_event_item.getEvent_item_league_team ().setLeague_team (home_league_temp);
                    home_event_item.getEvent_item_league_team ().setTimestamp (OffsetDateTime.now());
                    home_event_item.getEvent_item_league_team ().getLeague_team_players ().remove (League_Position.MLB_PITCHER);
                    }
                home_team_value_Textfield.setText (temp.getTemp_name ());
                home_pitcher_TextField.setText ("Click to Select");
                home_pitcher = null;
                }
            }
        double_header ();
        }
    //---------------------------------------------------------------------------------------------
    public void set_home_team (League_Team home_team, League_Team home_temp)
        {
        if (home_team != null)
            away_team_value_Textfield.setText(home_team.getName());
        else if (home_temp != null)
            away_team_value_Textfield.setText (home_temp.getTemp_name ());
        }
    //---------------------------------------------------------------------------------------------
    public void double_header ()
        {
        //only for mlb
        //then check two teams of event
        if (date_value != null && time_value != null && (home_league_team != null) && (away_league_team != null));
            {
            //see if there is another event on the same day with the same two teams
            for (int i = 0; i < categories.size(); i++)
                {
                Category current_category = categories.get(i);
                //get ctageories on that day
                if (current_category.getDate().equals (date_value))
                    {
                    //System.out.println ("DATE IS THE SAME");
                    for (Event event : category.getEvents ().values ())
                        {
                        if (event.getId () != original_event.getId ())
                            {
                            //check if same two teams
                            TreeMap <Integer, Event_Item> teams = event.getEvent_items();

                            Event_Item local_away_event_item = teams.get(0);
                            League_Team away_team = null;
                            if (local_away_event_item != null)
                                {
                                Event_Item_League_Team away_event_item_league_team = local_away_event_item.getEvent_item_league_team();
                                if (away_event_item_league_team != null)
                                    away_team = local_away_event_item.getEvent_item_league_team().getLeague_team();
                                }

                            Event_Item local_home_event_item = teams.get(1);
                            League_Team home_team = null;
                            if (local_home_event_item != null)
                                {
                                Event_Item_League_Team home_event_item_league_team = local_home_event_item.getEvent_item_league_team();
                                if (home_event_item_league_team != null)
                                    home_team = home_event_item_league_team.getLeague_team();
                                }
                            if (home_team != null && away_team != null && this.home_league_team != null && this.away_league_team != null)
                                {
                                if ( (this.home_league_team.getId() == home_team.getId() && this.away_league_team.getId() == away_team.getId()) ||
                                     (this.home_league_team.getId() == away_team.getId() && this.away_league_team.getId() == home_team.getId())
                                   )
                                    {
                                    //CHECK TIME
                                    //make earliest game 1 and latest game 2
                                    //set both events
                                    LocalTime new_event_time = time_value;
                                    OffsetDateTime current_event_offset_datetime = event.getEvent_time().getTime();
                                    ZonedDateTime zonedDateTime = current_event_offset_datetime.atZoneSameInstant(ZoneId.of("America/Los_Angeles"));
                                    // Extract local date and local time from ZonedDateTime
                                    LocalTime current_event_time = zonedDateTime.toLocalTime();

                                    if (new_event_time.isBefore (current_event_time))
                                        {
                                        double_header = 1;
                                        event.setDouble_header (2);
                                        game1_radio_btn.setSelected (true);
                                        }
                                    else
                                        {
                                        double_header = 2;
                                        event.setDouble_header (1);
                                        game2_radio_btn.setSelected (true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    public void set_categories (ArrayList <Category> categories)
        {
        this.categories = categories;
        }
    //----------------------------------------------------------------------------------------------
    public void initialize_double_header_buttons ()
        {
        ButtonGroup buttonGroup = new ButtonGroup ();
        buttonGroup.add (not_selected_radio_btn);
        buttonGroup.add (game1_radio_btn);
        buttonGroup.add (game2_radio_btn);

        ActionListener listener = (ActionEvent e) ->
            {
            if (not_selected_radio_btn.isSelected())
                {
                double_header = 0;
                System.out.println ("Option 1 selected");
                }
            else if (game1_radio_btn.isSelected())
                {
                double_header = 1;
                System.out.println ("Option 2 selected");
                }
            else if (game2_radio_btn.isSelected())
                {
                double_header = 2;
                System.out.println ("Option 3 selected");
                }
            };

        if (double_header == 0 && league.getSport_id () != Sport.BASEBALL)
            {
            double_header_Label   .setEnabled (false);
            not_selected_radio_btn.setEnabled (false);
            game1_radio_btn       .setEnabled (false);
            game2_radio_btn       .setEnabled (false);
            }
        else
            {
            double_header_Label   .setEnabled (true);
            not_selected_radio_btn.setEnabled (true);
            game1_radio_btn       .setEnabled (true);
            game2_radio_btn       .setEnabled (true);

            not_selected_radio_btn.addActionListener (listener);
            game1_radio_btn       .addActionListener (listener);
            game2_radio_btn       .addActionListener (listener);
            }
        }
    //--------------------------------------------------------------------------------------------------------------------------------------
    public void initialize_pitcher_buttons ()
        {
        }
    //-------------------------------------------------------------------------------------------------------------------------------------
    public void select_away_pitcher ()
        {
        System.out.println ("SELECT AWAY PITCHER FUNCTION");
        if (this.away_league_temp == null && this.away_league_team == null)
            {
            JOptionPane.showMessageDialog (this, "Select a Team to change its Pitcher");
            }
        else
            {
            if (Main.pitchers_dialog == null)
                Main.pitchers_dialog = new PitchersDialog (Main.categories_frame, true, selected_event, away_event_item, away_pitcher, false);
            else
                Main.pitchers_dialog.initialize_public (selected_event, away_event_item, away_pitcher);

            if (!Main.pitchers_dialog.isError ())
                {
                Main.pitchers_dialog.setVisible (true);
                // Dialog box causes program to wait here until it is closed
                if (Main.pitchers_dialog.isSaved ())
                    {
                    away_pitcher = Main.pitchers_dialog.getSelected_pitcher ();
                    if (away_pitcher != null)
                        {
                        away_pitcher_TextField.setText (away_pitcher.getLeague_player ().getPlayer ().getDisplay_name ());
                        League_Team_Player new_pitcher_league_team_player = League_Team_Player.getLeague_team_player_by_ID ().get (away_pitcher.getId ());
                        this.away_event_item.getEvent_item_league_team ().getLeague_team_players ().put (League_Position.MLB_PITCHER, new_pitcher_league_team_player);
                        }
                    else
                        {
                        away_pitcher = null;
                        away_pitcher_TextField.setText ("Click to Select");
                        this.away_event_item.getEvent_item_league_team ().getLeague_team_players ().remove (League_Position.MLB_PITCHER);
                        }
                    }
                }
            }
        }
    //-------------------------------------------------------------------------------------------------------------------------------------
    public void select_home_pitcher ()
        {
        if (this.away_league_temp == null && this.away_league_team == null)
            {
            JOptionPane.showMessageDialog (this, "Select a Team to change its Pitcher");
            }
        else
            {
            if (Main.pitchers_dialog == null)
                Main.pitchers_dialog = new PitchersDialog (Main.categories_frame, true, selected_event, home_event_item, home_pitcher, false);
            else
                Main.pitchers_dialog.initialize_public (selected_event, home_event_item, home_pitcher);

            if (!Main.pitchers_dialog.isError ())
                {
                Main.pitchers_dialog.setVisible (true);
                // Dialog box causes program to wait here until it is closed
                if (Main.pitchers_dialog.isSaved ())
                    {
                    home_pitcher = Main.pitchers_dialog.getSelected_pitcher ();
                    if (home_pitcher != null)
                        {
                        home_pitcher_TextField.setText (home_pitcher.getLeague_player ().getPlayer ().getDisplay_name ());
                        League_Team_Player new_pitcher_league_team_player = League_Team_Player.getLeague_team_player_by_ID ().get (home_pitcher.getId ());
                        this.home_event_item.getEvent_item_league_team ().getLeague_team_players ().put (League_Position.MLB_PITCHER, new_pitcher_league_team_player);
                        }
                    else
                        {
                        home_pitcher = null;
                        home_pitcher_TextField.setText ("Click to Select");
                        this.home_event_item.getEvent_item_league_team ().getLeague_team_players ().remove (League_Position.MLB_PITCHER);
                        }
                    }
                }
            }
        }
    //-------------------------------------------------------------------------------------------------------------------------------------
    public void add_event_database ()
        {
        StringBuilder sql = new StringBuilder ();
        sql.append ("DECLARE @category_id = INT;\n")
           .append ("DECLARE @schedule_id    SMALLINT;\n")
           .append ("DECLARE @sc_sequence    SMALLINT;\n")
           .append ("SELECT @schedule_id = id FROM Schedule AS sch WHERE sch.timestamp = (SELECT MAX(timestamp) FROM Schedule AS sch1 WHERE sch1.date=(SELECT value FROM GlobalValues WHERE name1='schedule' AND name2='current'))\n\n")
           .append ("SELECT @sc_sequence = MAX(sequence) FROM Schedule_Category AS sc WHERE sc.schedule_id = @schedule_id;\n\n")

           .append ("SET @event_id = NULL;\n")
           .append ("SELECT @event_id = id FROM Event WHERE date='")
           .append (selected_event.getDate ())
           .append ("' AND number=")
           .append (selected_event.getNumber ())
           .append (";\n")
           .append ("IF @event_id = NULL\n")
           .append ("BEGIN\n")

                 //add exclude field
           .append ("INSERT INTO Event (date, number, league_id, exclude, double_header, timestamp, updated)\nVALUES ('")
           .append (selected_event.getDate ())
           .append ("', ")
           .append (selected_event.getNumber ())
           .append (", ")
           .append (selected_event.getLeague ().getId ())
           .append (", ")
           .append (selected_event.isExclude ())
           .append (", ")
           .append (selected_event.getDouble_header ())
           .append (", SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET());\n")
           .append ("SET @event_id = SCOPE_IDENTITY();\n")

           .append ("INSERT INTO Event_Time (event_id, timestamp, time, TBA, source_id)\nVALUES (@event_id, SYSDATETIMEOFFSET(), '")
           .append (DateTimeUtils.get_date_time_string_UTC (selected_event.getEvent_time ().getTime ()))
           .append ("', 0, 1);\n")

           .append ("INSERT INTO Category_Event (category_id, event_id)\nVALUES (@category_id, @event_id);\n")

           .append ("INSERT INTO Event_Item (event_id, sequence, timestamp)\nVALUES (@event_id, 0, SYSDATETIMEOFFSET());\n")
           .append ("SET @event_item_id = SCOPE_IDENTITY();\n")

           .append ("INSERT INTO Event_Item (event_id, sequence, timestamp)\nVALUES (@event_id, 1, SYSDATETIMEOFFSET());\n")
           .append ("SET @event_item_id = SCOPE_IDENTITY();\n")

           .append ("SET @category_id = SCOPE_IDENTITY();\n")
           .append ("SET @sc_sequence = @sc_sequence + 1;\n")
           .append ("INSERT INTO Schedule_Category_Event (schedule_id, category_id, event_id, category_sequence) VALUES (@schedule_id, @category_id, @event_id, @sc_sequence);\n")
           .append ("END\n")
           ;
        Debug.print (sql);
        Main.db.executeUpdate (sql.toString ());
        }
    //-------------------------------------------------------------------------------------------------------------------------------------
    public void edit_event_database ()
        {
        boolean event_changed = false;
        StringBuilder sql = new StringBuilder ();

        if (   !selected_event.getDate ().equals (original_event.getDate ())
            || selected_event.getNumber ()        != original_event.getNumber ()
            || selected_event.getDouble_header () != original_event.getDouble_header ()
            || selected_event.isExclude ()        != original_event.isExclude ()
            )
            {
            event_changed = true;
            sql.append ("UPDATE Event")
               .append (" SET date='"        ).append (selected_event.getDate          ())
               .append (  "', number="       ).append (selected_event.getNumber        ())
               .append (   ", double_header=").append (selected_event.getDouble_header ())
               .append (   ", exclude="      ).append ((selected_event.isExclude () ? "1" : "0"))
               .append (   ", updated=SYSDATETIMEOFFSET()")
               .append (" WHERE id="         ).append (selected_event.getId ())
               .append (";\n")
               ;
            original_event.setDate          (selected_event.getDate          ());
            original_event.setNumber        (selected_event.getNumber        ());
            original_event.setDouble_header (selected_event.getDouble_header ());
            original_event.setExclude       (selected_event.isExclude        ());
            }
        if (  !selected_event.getEvent_time ().getTime ().isEqual (original_event.getEvent_time ().getTime ())
            || selected_event.getEvent_time ().isTBA () != original_event.getEvent_time ().isTBA ())
            {
            event_changed = true;
            original_event.setEvent_time (selected_event.getEvent_time ());
            sql.append ("INSERT INTO Event_Time (event_id, timestamp, time, TBA, source_id) VALUES (")
               .append (selected_event.getId ())
               .append (", SYSDATETIMEOFFSET(), '")
               .append (DateTimeUtils.get_date_time_string_UTC (selected_event.getEvent_time ().getTime ()))
               .append ("', ")
               .append (selected_event.getEvent_time ().isTBA () ? "1" : "0")
               .append (", 1);\n")
               ;
            }
        Event_Venue event_venue    = selected_event.getEvent_venue ();
        Event_Venue original_venue = original_event.getEvent_venue ();
        if (event_venue == null)
            {
            if (original_venue != null)
                {
                event_changed = true;
                original_event.setEvent_venue (null);
                }
            }
        else if (event_venue.isNeutral ())
            {
            if (   original_venue == null
                || !original_venue.isNeutral ()
                || original_venue.isOverride () != event_venue.isOverride ()
                || !original_venue.getVenue_name ().equals (event_venue.getVenue_name ())
                || !original_venue.getCity_name  ().equals (event_venue.getCity_name  ()))
                {
                event_changed = true;
                original_event.setEvent_venue (selected_event.getEvent_venue ());
                sql.append ("INSERT INTO Event_Venue (event_id, timestamp, neutral, venue_id, venue_name, city_name, override)\n")
                   .append ("VALUES (")
                   .append (selected_event.getId ())
                   .append (", ")
                   .append ("SYSDATETIMEOFFSET(), ")
                   .append (event_venue.isNeutral () ? "1" : "0")
                   .append (", ")
                   .append (event_venue.getVenue () == null ? "NULL" : event_venue.getVenue ().getId ())
                   .append (", ")
                   .append ("'")
                   .append (event_venue.getVenue_name ().replaceAll ("'", "''"))
                   .append ("', '")
                   .append (event_venue.getCity_name ().replaceAll ("'", "''"))
                   .append ("', ")
                   .append (event_venue.isOverride () ? "1" : "0")
                   .append (");\n")
                   ;
                }
            }
        else
            {
            if (   original_venue != null
                && original_venue.isNeutral ())
                {
                event_changed = true;
                original_event.setEvent_venue (null);
                if (selected_event_venue != null) // if event-venue was there but now we don't want it then delete it
                    {
                    sql.append ("INSERT INTO Event_Venue (event_id, timestamp, neutral, venue_id, venue_name, city_name, override)\n")
                       .append ("VALUES (@event_id, SYSDATETIMEOFFSET(), 0, NULL, NULL, NULL, 0);\n")
                       ;
                    }
                }
            }

        Event_Item local_away_event_item = selected_event.getEvent_items ().get (0);
        Event_Item local_home_event_item = selected_event.getEvent_items ().get (1);
        Event_Item old_away_event_item   = original_event.getEvent_items ().get (0);
        Event_Item old_home_event_item   = original_event.getEvent_items ().get (1);
        if (local_away_event_item.getEvent_item_league_team () != null)
            {
            if (   old_away_event_item.getEvent_item_league_team () == null
                || old_away_event_item.getEvent_item_league_team ().getLeague_team () != local_away_event_item.getEvent_item_league_team ().getLeague_team ())
                {
                event_changed = true;
                old_away_event_item.setEvent_item_league_team (local_away_event_item.getEvent_item_league_team ());
                }
            if (away_pitcher != null)
                {
                League_Team_Player old_away_pitcher = old_away_event_item.getEvent_item_league_team ().getLeague_team_players ().get (League_Position.MLB_PITCHER);
                if (   old_away_pitcher == null
                    || old_away_pitcher.getId () != away_pitcher.getId ())
                    {
                    event_changed = true;
                    old_away_event_item.getEvent_item_league_team ().getLeague_team_players ().put (League_Position.MLB_PITCHER, away_pitcher);
                    }
                }
            }
        if (local_home_event_item.getEvent_item_league_team () != null)
            {
            if (   old_home_event_item.getEvent_item_league_team () == null
                || old_home_event_item.getEvent_item_league_team ().getLeague_team () != local_home_event_item.getEvent_item_league_team ().getLeague_team ())
                {
                event_changed = true;
                old_home_event_item.setEvent_item_league_team (local_home_event_item.getEvent_item_league_team ());
                }
            if (home_pitcher != null)
                {
                League_Team_Player old_home_pitcher = old_home_event_item.getEvent_item_league_team ().getLeague_team_players ().get (League_Position.MLB_PITCHER);
                if (   old_home_pitcher == null
                    || old_home_pitcher.getId () != home_pitcher.getId ())
                    {
                    event_changed = true;
                    old_home_event_item.getEvent_item_league_team ().getLeague_team_players ().put (League_Position.MLB_PITCHER, home_pitcher);
                    }
                }
            }
        if (local_away_event_item.getEvent_item_league_team () != null && this.away_event_item != null)
            {
            if (   old_away_event_item.getEvent_item_league_team () == null
                || old_away_event_item.getEvent_item_league_team ().getLeague_team () != this.away_event_item.getEvent_item_league_team ().getLeague_team ())
                {
                event_changed = true;
                old_away_event_item.setEvent_item_league_team (local_away_event_item.getEvent_item_league_team ());
                }
            }
        if (local_home_event_item.getEvent_item_league_team () != null && this.home_event_item != null)
            {
            if (   old_home_event_item.getEvent_item_league_team () == null
                || old_home_event_item.getEvent_item_league_team ().getLeague_team () != this.home_event_item.getEvent_item_league_team ().getLeague_team ())
                {
                event_changed = true;
                old_home_event_item.setEvent_item_league_team (local_home_event_item.getEvent_item_league_team ());
                }
            }

        if (event_changed)
            {
            Debug.print (sql);
            Main.db.executeUpdate (sql);
            Main.send_schedule_changed_for_event (original_event);
            }
        }
    //-------------------------------------------------------------------------------------------------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        save_Button = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        date_Label = new javax.swing.JLabel();
        event_Panel = new javax.swing.JPanel();
        date_value_Label = new javax.swing.JLabel();
        time_value_Label = new javax.swing.JLabel();
        date_time_Button = new javax.swing.JButton();
        event_number_Label = new javax.swing.JLabel();
        event_number_TextField = new javax.swing.JTextField();
        away_team_value_Textfield = new javax.swing.JTextField();
        home_team_value_Textfield = new javax.swing.JTextField();
        double_header_Label = new javax.swing.JLabel();
        not_selected_radio_btn = new javax.swing.JRadioButton();
        game1_radio_btn = new javax.swing.JRadioButton();
        game2_radio_btn = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        game_number_error_Label = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        away_pitcher_TextField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        home_pitcher_TextField = new javax.swing.JTextField();
        exclude_CheckBox = new javax.swing.JCheckBox();
        away_game_number = new javax.swing.JTextField();
        home_game_number = new javax.swing.JTextField();
        id_Label = new javax.swing.JLabel();
        league_value_Label = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        category_header_TextArea = new javax.swing.JTextArea();
        venue_Panel = new javax.swing.JPanel();
        neutral_CheckBox = new javax.swing.JCheckBox();
        venue_ComboBox = new javax.swing.JComboBox<>();
        venue_Label = new javax.swing.JLabel();
        venue_TextField = new javax.swing.JTextField();
        city_Label = new javax.swing.JLabel();
        city_TextField = new javax.swing.JTextField();
        override_CheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Event");

        save_Button.setText("Save");
        save_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                save_ButtonActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        date_Label.setText(" Date/Time:");
        date_Label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        event_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        date_value_Label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        date_value_Label.setText(" Click calendar");
        date_value_Label.setPreferredSize(new java.awt.Dimension(77, 25));

        time_value_Label.setText("to select ");
        time_value_Label.setMaximumSize(new java.awt.Dimension(214, 251));
        time_value_Label.setPreferredSize(new java.awt.Dimension(48, 25));

        date_time_Button.setMaximumSize(new java.awt.Dimension(3213210, 3213210));
        date_time_Button.setMinimumSize(new java.awt.Dimension(30, 30));
        date_time_Button.setPreferredSize(new java.awt.Dimension(30, 30));
        date_time_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                date_time_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout event_PanelLayout = new javax.swing.GroupLayout(event_Panel);
        event_Panel.setLayout(event_PanelLayout);
        event_PanelLayout.setHorizontalGroup(
            event_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(event_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(date_value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(date_time_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(time_value_Label, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                .addContainerGap())
        );
        event_PanelLayout.setVerticalGroup(
            event_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(event_PanelLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(event_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date_time_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(date_value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(time_value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        event_number_Label.setText("Event Number: ");
        event_number_Label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        event_number_TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        away_team_value_Textfield.setEditable(false);
        away_team_value_Textfield.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        away_team_value_Textfield.setText("Click to Select Away Team");
        away_team_value_Textfield.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                away_team_value_TextfieldMouseClicked(evt);
            }
        });
        away_team_value_Textfield.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                away_team_value_TextfieldActionPerformed(evt);
            }
        });

        home_team_value_Textfield.setEditable(false);
        home_team_value_Textfield.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        home_team_value_Textfield.setText("Click to Select Home Team");
        home_team_value_Textfield.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                home_team_value_TextfieldMouseClicked(evt);
            }
        });
        home_team_value_Textfield.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                home_team_value_TextfieldActionPerformed(evt);
            }
        });

        double_header_Label.setText(" Double header:");
        double_header_Label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        not_selected_radio_btn.setText("Not Selected");

        game1_radio_btn.setText("Game 1");

        game2_radio_btn.setText("Game 2");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Select Teams and Date/Time to update Double Header");

        game_number_error_Label.setText("Error Message");

        away_pitcher_TextField.setEditable(false);
        away_pitcher_TextField.setText("Click to Select Pitcher");
        away_pitcher_TextField.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                away_pitcher_TextFieldMouseClicked(evt);
            }
        });
        away_pitcher_TextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                away_pitcher_TextFieldActionPerformed(evt);
            }
        });

        home_pitcher_TextField.setEditable(false);
        home_pitcher_TextField.setText("Click to Select Pitcher");
        home_pitcher_TextField.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                home_pitcher_TextFieldMouseClicked(evt);
            }
        });
        home_pitcher_TextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                home_pitcher_TextFieldActionPerformed(evt);
            }
        });

        exclude_CheckBox.setText("Exclude");
        exclude_CheckBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                exclude_CheckBoxActionPerformed(evt);
            }
        });

        away_game_number.setEditable(false);
        away_game_number.setText("999");

        home_game_number.setEditable(false);
        home_game_number.setText("999");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(double_header_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(not_selected_radio_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addComponent(game1_radio_btn)
                .addGap(35, 35, 35)
                .addComponent(game2_radio_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(home_game_number)
                    .addComponent(away_game_number))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(away_team_value_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(home_team_value_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(home_pitcher_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(away_pitcher_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(date_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(event_number_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(event_number_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(exclude_CheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(event_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(67, 67, 67))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(game_number_error_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(118, 118, 118))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {away_pitcher_TextField, home_pitcher_TextField});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(date_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(event_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(event_number_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(event_number_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exclude_CheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(game_number_error_Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(away_team_value_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(away_pitcher_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(away_game_number, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(home_team_value_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(home_pitcher_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(home_game_number, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(double_header_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(not_selected_radio_btn)
                    .addComponent(game1_radio_btn)
                    .addComponent(game2_radio_btn))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(10, 10, 10))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {date_Label, event_Panel, event_number_Label, event_number_TextField});

        id_Label.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        id_Label.setText("Event ID: ---");

        league_value_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        league_value_Label.setText("League");

        category_header_TextArea.setEditable(false);
        category_header_TextArea.setColumns(20);
        category_header_TextArea.setLineWrap(true);
        category_header_TextArea.setRows(2);
        category_header_TextArea.setText("Category Header");
        jScrollPane1.setViewportView(category_header_TextArea);

        venue_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Venue"));

        neutral_CheckBox.setText("Neutral");

        venue_ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                venue_ComboBoxActionPerformed(evt);
            }
        });

        venue_Label.setText("Venue:");

        city_Label.setText("City:");

        override_CheckBox.setText("Override");

        javax.swing.GroupLayout venue_PanelLayout = new javax.swing.GroupLayout(venue_Panel);
        venue_Panel.setLayout(venue_PanelLayout);
        venue_PanelLayout.setHorizontalGroup(
            venue_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(venue_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(venue_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(venue_PanelLayout.createSequentialGroup()
                        .addComponent(neutral_CheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(override_CheckBox))
                    .addComponent(venue_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(venue_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(venue_Label)
                    .addComponent(city_Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(venue_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(venue_TextField)
                    .addComponent(city_TextField, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
                .addContainerGap())
        );
        venue_PanelLayout.setVerticalGroup(
            venue_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(venue_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(venue_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(neutral_CheckBox)
                    .addComponent(venue_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(venue_Label)
                    .addComponent(override_CheckBox))
                .addGap(5, 5, 5)
                .addGroup(venue_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(city_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(city_Label)
                    .addComponent(venue_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(venue_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                    .addComponent(id_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(league_value_Label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(save_Button)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(id_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(league_value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(venue_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(save_Button)
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_save_ButtonActionPerformed
    {//GEN-HEADEREND:event_save_ButtonActionPerformed
        if (edit_mode)
            save_fields_edit_event ();
        else if (!game_number_already_exists (event_number_TextField.getText ()))
            save_fields_add_event ();
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void date_time_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_date_time_ButtonActionPerformed
    {//GEN-HEADEREND:event_date_time_ButtonActionPerformed
        if (edit_mode)
            edit_date_time ();
        else
            add_date_time ();
    }//GEN-LAST:event_date_time_ButtonActionPerformed

    private void home_team_value_TextfieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_home_team_value_TextfieldActionPerformed
    {//GEN-HEADEREND:event_home_team_value_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_home_team_value_TextfieldActionPerformed

    private void away_team_value_TextfieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_away_team_value_TextfieldActionPerformed
    {//GEN-HEADEREND:event_away_team_value_TextfieldActionPerformed
    }//GEN-LAST:event_away_team_value_TextfieldActionPerformed

    private void away_team_value_TextfieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_away_team_value_TextfieldMouseClicked
    {//GEN-HEADEREND:event_away_team_value_TextfieldMouseClicked
        //add_away_team ();
    }//GEN-LAST:event_away_team_value_TextfieldMouseClicked

    private void home_team_value_TextfieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_home_team_value_TextfieldMouseClicked
    {//GEN-HEADEREND:event_home_team_value_TextfieldMouseClicked
        //add_home_team ();
    }//GEN-LAST:event_home_team_value_TextfieldMouseClicked

    private void away_pitcher_TextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_away_pitcher_TextFieldActionPerformed
    {//GEN-HEADEREND:event_away_pitcher_TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_away_pitcher_TextFieldActionPerformed

    private void home_pitcher_TextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_home_pitcher_TextFieldActionPerformed
    {//GEN-HEADEREND:event_home_pitcher_TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_home_pitcher_TextFieldActionPerformed

    private void away_pitcher_TextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_away_pitcher_TextFieldMouseClicked
    {//GEN-HEADEREND:event_away_pitcher_TextFieldMouseClicked
        //select_away_pitcher ();
    }//GEN-LAST:event_away_pitcher_TextFieldMouseClicked

    private void home_pitcher_TextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_home_pitcher_TextFieldMouseClicked
    {//GEN-HEADEREND:event_home_pitcher_TextFieldMouseClicked
        //select_home_pitcher ();
    }//GEN-LAST:event_home_pitcher_TextFieldMouseClicked

    private void exclude_CheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exclude_CheckBoxActionPerformed
    {//GEN-HEADEREND:event_exclude_CheckBoxActionPerformed
        selected_event.setExclude (exclude_CheckBox.isSelected ());
    }//GEN-LAST:event_exclude_CheckBoxActionPerformed

    private void venue_ComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_venue_ComboBoxActionPerformed
    {//GEN-HEADEREND:event_venue_ComboBoxActionPerformed
        String venue_name = (String) venue_ComboBox.getSelectedItem ();
        Venue  venue      = Venue.get_venue (venue_name);
        if (venue != null)
            {
            venue_TextField.setText (venue_name);
            Location location = venue.getLocation ();
            if (location == null)
                city_TextField .setText ("");
            else
                {
                String country = location.getCountry ().trim ().toUpperCase ();
                String city_name = location.get_city_state_abbr ();
                city_TextField .setText (city_name);
                }
            }
    }//GEN-LAST:event_venue_ComboBoxActionPerformed
    //---------------------------------------------------------------------------------------------
    public static void main (JFrame parent)
        {
       // java.awt.EventQueue.invokeLater (() ->
            {
            Main.add_event_dialog = new AddEventDialog (parent, true);
            Main.add_event_dialog.addWindowListener (new java.awt.event.WindowAdapter ()
                {
                @Override
                public void windowClosing (java.awt.event.WindowEvent e)
                    {
                    }
                    });
            }//);
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField away_game_number;
    private javax.swing.JTextField away_pitcher_TextField;
    private javax.swing.JTextField away_team_value_Textfield;
    private javax.swing.JTextArea category_header_TextArea;
    private javax.swing.JLabel city_Label;
    private javax.swing.JTextField city_TextField;
    private javax.swing.JLabel date_Label;
    private javax.swing.JButton date_time_Button;
    private javax.swing.JLabel date_value_Label;
    private javax.swing.JLabel double_header_Label;
    private javax.swing.JPanel event_Panel;
    private javax.swing.JLabel event_number_Label;
    private javax.swing.JTextField event_number_TextField;
    private javax.swing.JCheckBox exclude_CheckBox;
    private javax.swing.JRadioButton game1_radio_btn;
    private javax.swing.JRadioButton game2_radio_btn;
    private javax.swing.JLabel game_number_error_Label;
    private javax.swing.JTextField home_game_number;
    private javax.swing.JTextField home_pitcher_TextField;
    private javax.swing.JTextField home_team_value_Textfield;
    private javax.swing.JLabel id_Label;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel league_value_Label;
    private javax.swing.JCheckBox neutral_CheckBox;
    private javax.swing.JRadioButton not_selected_radio_btn;
    private javax.swing.JCheckBox override_CheckBox;
    private javax.swing.JButton save_Button;
    private javax.swing.JLabel time_value_Label;
    private javax.swing.JComboBox<String> venue_ComboBox;
    private javax.swing.JLabel venue_Label;
    private javax.swing.JPanel venue_Panel;
    private javax.swing.JTextField venue_TextField;
    // End of variables declaration//GEN-END:variables
    }
