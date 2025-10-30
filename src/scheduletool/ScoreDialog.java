package scheduletool;

import gsutils.Debug;
import gsutils.Utils;
import gsutils.data.Category;
import gsutils.data.Event;
import gsutils.data.Event_Item;
import gsutils.data.Event_Item_League_Prop;
import gsutils.data.Event_Item_League_Team;
import gsutils.data.Event_Score;
import gsutils.data.Event_Score_Item;
import gsutils.data.League;
import gsutils.data.League_Prop;
import gsutils.data.League_Team;
import gsutils.data.Source;
import gsutils.data.Sport;
import gsutils.data.Trinary;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class ScoreDialog extends javax.swing.JDialog
    {
    static final private boolean SCORE_CORRECTION = true;
    //---------------------------------------------------------------------------------------------------------------
    private Event       event;
    private Event_Score event_score;
    private Category    category;

    private League_Team away_team;
    private League_Prop away_prop;
    private League_Team home_team;
    private League_Prop home_prop;

    private boolean add_score = false;

    private String away_score;
    private String home_score;

    private String status1;
    private String status2;
    private String status0;
    private DefaultComboBoxModel event_statuses_model;

    private boolean divide          = false;
    private boolean saved           = false;

    private int       selected_source_id;
    private ArrayList <Source> sources_all;
    private Trinary away_possession = Trinary.UNSET;

    private boolean override        = false;
    private LinescoreDialog linescore_dialog;
    //---------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------
    public ScoreDialog (java.awt.Frame parent, boolean modal)
        {
        super (parent, modal);
        initComponents();
        initialize_possession_buttons ();
        set_tab_sequence ();
        initialize_source_combobox (Source.ODDSLOGIC);
        }
    //---------------------------------------------------------------------------------------------------------------
    public ScoreDialog (java.awt.Dialog parent, boolean modal)
        {
        super (parent, modal);
        initComponents();
        initialize_possession_buttons ();
        set_tab_sequence ();
        }
    //---------------------------------------------------------------------------------------------------------------
    //status 0 combo box
    public void set_event_status (String first_element)
        {
        //move status0 to bottom and call it event status
        //cancelled, postponed, delay, rain delay
        //add if doesnt exist
        ArrayList <String> event_statuses = new ArrayList <> ();
        //if (first_element.length () > 0)
          //  event_statuses.add (first_element);
        event_statuses.add ("");
        event_statuses.add ("Cancelled");
        event_statuses.add ("Delay");
        event_statuses.add ("Draw");
        event_statuses.add ("Postponed");
        event_statuses.add ("Rain Delay");
        event_statuses.add ("Suspended");
        event_statuses.add ("Tie");

        event_statuses_model = new DefaultComboBoxModel ();
        for (int i = 0; i < event_statuses.size(); i++)
            {
            event_statuses_model.addElement(event_statuses.get(i));
            }
        event_status_ComboBox.setModel(event_statuses_model);
        event_status_ComboBox.setEditable(true);
        //event_status_ComboBox.setEditor(new CustomComboBoxEditor());
        }
    //---------------------------------------------------------------------------------------------------------------
    public void initialize_fields_add_score (Event event, Category category)
        {
        this.add_score = true;
        this.event     = event;
        this.category  = category;

        set_field_widths (event);

        this.league_value_Label  .setText (event.getLeague().getName());
        this.category_value_Label.setText (category.getHeader());
        this.away_value_Label.setText ("");
        this.home_value_Label.setText ("");

        this.away_game_number.setText ("");
        this.home_game_number.setText ("");

        //override = event.getEvent_score ().isOverride ();
        override_CheckBox.setSelected (true);

        score_correction_CheckBox.setSelected (false);

        set_teams (event);
        set_event_status ("");
        setTitle ("Add Score");
        }
    //---------------------------------------------------------------------------------------------------------------
    public void initialize_fields_edit_score (Event event, Category category)
        {
        this.event    = event;
        this.category = category;

        this.away_value_Label    .setText ("");
        this.home_value_Label    .setText ("");
        this.league_value_Label  .setText (event.getLeague().getName());
        this.category_value_Label.setText (category.getHeader());

        this.away_game_number.setText ("" + event.getNumber ());
        this.home_game_number.setText ("" + (event.getNumber () + 1));

        //override = (event.getEvent_score () == null ? false : event.getEvent_score ().isOverride ());
        override_CheckBox.setSelected (true);

        score_correction_CheckBox.setSelected (false);

        saved = false;

        set_teams (event);
        set_event_status ("");
        int sport_id = event.getLeague ().getSport_id ();
        set_field_widths (event);
        Event_Score local_event_score = event.getEvent_score();
        if (local_event_score != null)
            {
            ArrayList <Event_Score_Item> event_score_items = local_event_score.getEvent_score_items();
            String local_away_score = "";
            String local_home_score = "";
            if (event_score_items != null)
                {
                Event_Score_Item away_event_score_item = event_score_items.get (0);
                Event_Score_Item home_event_score_item = event_score_items.get (1);

                if (   sport_id == gsutils.data.Sport.TENNIS
                    || sport_id == gsutils.data.Sport.TABLE_TENNIS)
                    {
                    if (away_event_score_item != null)
                        local_away_score = away_event_score_item.getAddendum ();
                    if (home_event_score_item != null)
                        local_home_score = home_event_score_item.getAddendum ();
                    }
                else
                    {
                    if (away_event_score_item != null)
                        local_away_score = away_event_score_item.getScore ();
                    if (home_event_score_item != null)
                        local_home_score = home_event_score_item.getScore ();
                    }

                if (local_away_score != null)
                    away_score_TextField.setText (local_away_score);

                if (local_home_score != null)
                    home_score_TextField.setText (local_home_score);

                //Status textfields
                String local_status1 = local_event_score.getStatus1 ();
                if (local_status1 != null)
                    status1_TextField.setText (local_status1);
                String local_status2 = local_event_score.getStatus2 ();
                if (local_status2 != null)
                    status2_TextField.setText (local_status2);
                String local_status0 = local_event_score.getStatus0 ();
                if (local_status0 != null)
                    {
                    event_statuses_model.insertElementAt (local_status0, 0);
                    event_status_ComboBox.setModel (event_statuses_model);
                    event_status_ComboBox.setSelectedIndex (0);
                    }
                }
            if (   sport_id == gsutils.data.Sport.TENNIS
                || sport_id == gsutils.data.Sport.TABLE_TENNIS)
                {
                if ((   (local_away_score == null || local_away_score.length () == 0)
                     && (local_home_score == null || local_home_score.length () == 0))
                    || local_event_score.getAway_possession () == null
                    || local_event_score.getAway_possession () == Trinary.UNSET)
                    {
                    none_possession_btn.setSelected (true);
                    away_possession_btn.setSelected (false);
                    home_possession_btn.setSelected (false);
                    away_possession = Trinary.UNSET;
                    }
                else if (local_event_score.getAway_possession () == Trinary.TRUE)
                    {
                    none_possession_btn.setSelected (false);
                    away_possession_btn.setSelected (true);
                    home_possession_btn.setSelected (false);
                    away_possession = Trinary.TRUE;
                    }
                else
                    {
                    none_possession_btn.setSelected (false);
                    away_possession_btn.setSelected (false);
                    home_possession_btn.setSelected (true);
                    away_possession = Trinary.FALSE;
                    }
                }
            else
                {
                none_possession_btn.setSelected (true);
                away_possession_btn.setSelected (false);
                home_possession_btn.setSelected (false);
                away_possession = Trinary.UNSET;
                }
            }
//        possession_panel.setVisible (true);
        setTitle ("Edit Score");
        }
    //----------------------------------------------------------------------------------------
    private void initialize_source_combobox (int source_id)
        {
        if (Main.development)
            {
            TreeMap <Integer, Source> sources = Source.getSources ();
            sources_all = new ArrayList <> ();
            DefaultComboBoxModel sources_model = new DefaultComboBoxModel ();
            int i = 0;
            int index = -1;
            for (Source source : sources.values ())
                {
                sources_model.addElement (source.getName ());
                sources_all.add (source);

                if (source.getId () == source_id)
                    index = i;
                i++;
                }
            source_ComboBox.setModel (sources_model);
            if (index >= 0)
                {
                source_ComboBox.setSelectedIndex (index);
                selected_source_id = source_id;
                }
            }
        else
            {
            source_Label   .setVisible (false);
            source_ComboBox.setVisible (false);
            }
        }
    //----------------------------------------------------------------------------------------
    public void source_selected ()
        {
        int index = source_ComboBox.getSelectedIndex ();
        if (index >= 0)
            selected_source_id = sources_all.get (index).getId ();
        }
    //---------------------------------------------------------------------------------------------
    private void set_field_widths (Event event1)
        {
        //event_score
        //add none for possession buttons
        //event_score.isAway_possession ();
        //0: away   1: home
        //only keep for mens and womens tennis
        int sport_id = event1.getLeague ().getSport_id ();
        if (   sport_id == gsutils.data.Sport.TENNIS
            || sport_id == gsutils.data.Sport.TABLE_TENNIS)
            {
            status_Label     .setVisible (false);
            status1_TextField.setVisible (false);
            //status2_TextField.setVisible (false);
            away_score_TextField.setSize (away_score_TextField.getWidth () * 3, away_score_TextField.getHeight ());
            home_score_TextField.setSize (home_score_TextField.getWidth () * 3, home_score_TextField.getHeight ());
            divide = true;
            }
        else
            {
            status_Label     .setVisible (true);
            status1_TextField.setVisible (true);
            //status2_TextField.setVisible (true);
            if (divide)
                {
                divide = false;
                away_score_TextField.setSize (away_score_TextField.getWidth () / 3, away_score_TextField.getHeight ());
                home_score_TextField.setSize (home_score_TextField.getWidth () / 3, home_score_TextField.getHeight ());
                }
            }
        }
    //---------------------------------------------------------------------------------------------------------------
    private void set_teams (Event event)
        {
        TreeMap <Integer, Event_Item> event_items = event.getEvent_items();
        Event_Item away_item = event_items.get(0);
        boolean away = true;
        set_team_label (away_item, away);

        Event_Item home_item = event_items.get(1);
        set_team_label (home_item, !away);
        //away_team_value_Label.setText(.getLeague_team().getName());

        //Event_Item home_item = event_items.get(1);
        //home_team_value_Label.setText(home_item.getEvent_item_league_team().getLeague_team().getName());
        }
    //---------------------------------------------------------------------------------------------
    private void set_team_label (Event_Item event_item, boolean away)
        {
        Event_Item_League_Team team_item = event_item.getEvent_item_league_team();
        Event_Item_League_Prop team_prop = event_item.getEvent_item_league_prop();

        String text = "";
        if (team_item != null)
            {
            League_Team team = team_item.getLeague_team();
            if (team != null)
                {
                text = team.getName();
                if (away)
                    away_team = team;
                else
                    home_team = team;
                }
            }
        else if (team_prop != null)
            {
            League_Prop prop = team_prop.getLeague_prop();
            if (prop != null)
                {
                text = prop.getProp();
                if (away)
                    away_prop = prop;
                else
                    home_prop = prop;
                }
            }

        if (text.length() > 0)
            {
            if (away)
                away_value_Label.setText(text);
            else
                home_value_Label.setText(text);
            }
        }
    //---------------------------------------------------------------------------------------------
    public void save ()
        {
        boolean process_score = true;
        String temp_status1 = status1_TextField.getText().trim();
        String temp_status2 = status2_TextField.getText().trim();
        String temp_status0 = String.valueOf (event_status_ComboBox.getSelectedItem ());

        if (   temp_status1.length () == 0
            && temp_status2.length () == 0
            && temp_status0.length () == 0)
            {
            int result = JOptionPane.showConfirmDialog (this, "Are you sure you want to save the score with no status?", "No statuses", JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION)
                process_score = false;
            }

        if (process_score)
            {
            Event local_event = new Event (event);
            event_score = new Event_Score ();
            event_score.setSource (Source.getSource_by_ID (selected_source_id));
            event_score.setEvent (event);
            event_score.setTimestamp (OffsetDateTime.now());
            local_event.setEvent_score (event_score);

            this.away_score = away_score_TextField.getText().trim();
            this.home_score = home_score_TextField.getText().trim();

            this.status1 = status1_TextField.getText().trim();
            this.status2 = status2_TextField.getText().trim();
            this.status0 = String.valueOf (event_status_ComboBox.getSelectedItem ());

            event_score.setStatus1 (status1);
            event_score.setStatus2 (status2);
            event_score.setStatus0 (status0);

            ArrayList <Event_Score_Item> event_score_items = event_score.getEvent_score_items();

            Event_Score_Item away_score_item;
            Event_Score_Item home_score_item;
            if (event_score_items.isEmpty())
                {
                away_score_item = new Event_Score_Item ();
                home_score_item = new Event_Score_Item ();
                event_score_items.add (away_score_item);
                event_score_items.add (home_score_item);
                }
            else
                {
                away_score_item = event_score_items.get (0);
                home_score_item = event_score_items.get (1);
                }
            //away & home score item ID?

            int sport_id = event.getLeague ().getSport_id ();
            if (   sport_id == gsutils.data.Sport.TENNIS
                || sport_id == gsutils.data.Sport.TABLE_TENNIS)
                {
                away_score_item.setAddendum (away_score);
                home_score_item.setAddendum (home_score);
                }
            else
                {
                away_score_item.setScore       (away_score);
                home_score_item.setScore       (home_score);
                }

            away_score_item.setEvent_score (event_score);
            home_score_item.setEvent_score (event_score);

            override = override_CheckBox.isSelected ();
            event_score.setAway_possession (away_possession);
            event_score.setOverride        (override);
           // this.status0 = event_status_ComboBox.getSelectedItem() + "";
            System.out.println ("combo box value: " + status0);

            //Main.categories_frame.edit_score (event_score);

            System.out.println ("away score: " + away_score);
            System.out.println ("home score: " + home_score);
            System.out.println ("status 1: "   + status1);
            System.out.println ("status 2: "   + status2);
            System.out.println ("status 0: "   + status0);

            saved = true;

            StringBuilder message_buffer = (new StringBuilder ("SCORES>>>\n"))
                                    .append (gsutils.HDF.create.Scores.get_hdf (Main.name, local_event))
                                    .append ("\n<<<\n")
                                    ;

            if (Main.schedule_frame.isToday ())
                {
                Debug.print (message_buffer);
                Main.score_server_client.send (message_buffer);

                if (score_correction_CheckBox.isSelected ())
                    {
                    if (linescore_dialog == null)
                        linescore_dialog = new LinescoreDialog (this, true);

                    linescore_dialog.initialize (event, SCORE_CORRECTION, Utils.parse_int (away_score), Utils.parse_int (home_score));
                    }
                }
            else
                {
                Main.categories_frame.update_score_on_screen (event, event_score);
                Main.categories_frame.save_score ();
                }

            setVisible (false);
            }
        }
    //---------------------------------------------------------------------------------------------------------------
    public boolean isSaved ()
        {
        return saved;
        }
    //---------------------------------------------------------------------------------------------------------------
    private void initialize_possession_buttons ()
        {
        ButtonGroup buttonGroup = new ButtonGroup ();
        buttonGroup.add (away_possession_btn);
        buttonGroup.add (home_possession_btn);
        buttonGroup.add (none_possession_btn);

        ActionListener listener = (ActionEvent e) ->
            {
            if (away_possession_btn.isSelected())
                away_possession = Trinary.TRUE;
            else if (home_possession_btn.isSelected())
                away_possession = Trinary.FALSE;
            else if (none_possession_btn.isSelected())
                away_possession = Trinary.UNSET;
            };

        away_possession_btn.addActionListener (listener);
        home_possession_btn.addActionListener (listener);
        none_possession_btn.addActionListener (listener);
        }
    //---------------------------------------------------------------------------------------------------------------
//    public void save_score ()
//        {
//        StringBuilder message_buffer;
//        //Event event = Main.schedule.getEvents_by_date_and_number ().get (new Event_Key (event.getDate (), event.getNumber ()));
//        int sport_id = event.getLeague ().getSport_id ();
//        if (   sport_id == gsutils.data.Sport.TENNIS
//            || sport_id == gsutils.data.Sport.TABLE_TENNIS)
//            {
//            int period = 0;
//            if (status2 == null)
//                period = 0;
//            else if (status2.equals ("Final"))
//                {
//                period = 0;
//                }
//            else if (status2.length () > 0 && Character.isDigit (status2.charAt (status2.length ()-1)))
//                {
//                if (status2.length () > 1 && Character.isDigit (status2.charAt (status2.length ()-2)))
//                    period = Integer.parseInt (status2.substring (status2.length ()-2));
//                else
//                    period = Integer.parseInt (status2.substring (status2.length ()-1));
//                }
//
//            String away_score_last_set;
//            String home_score_last_set;
//            if (away_score.contains (","))
//                {
//                int last_comma = away_score.lastIndexOf (',');
//                away_score_last_set = away_score.substring (last_comma + 1);
//                last_comma = home_score.lastIndexOf (',');
//                home_score_last_set = home_score.substring (last_comma + 1);
//                }
//            else
//                {
//                away_score_last_set = away_score;
//                home_score_last_set = home_score;
//                }
//
//            StringBuilder sql = new StringBuilder ();
//            sql.append ("DECLARE @event_score_id INT;\n")
//               .append ("INSERT INTO Event_Score (event_id, source_id, timestamp, feed_timestamp, event_completed, period_completed, period, status1, status2, away_possession, override)\n")
//               .append ("VALUES (")
//               .append (event.getId ())
//               .append (", ")
//               .append (gsutils.data.Source.ODDSLOGIC)
//               .append (", SYSDATETIMEOFFSET()")
//               .append (", SYSDATETIMEOFFSET()")
//               .append (", ")
//               .append (status2 == null ? 0 : status2.equals ("Final") ? 1 : 0)
//               .append (", ")
//               .append (status2 == null ? 0 : status2.equals ("Final") || status2.equals ("End") || status2.equals ("Time") ? 1 : 0)
//               .append (", ")
//               .append (period)
//               .append (", '")
//               .append (status1 == null ? "" : status1)
//               .append ("', '")
//               .append (status2 == null ? "" : status2)
//               .append ("', ")
//               .append (Trinary.db_value (away_possession))
//               .append (", ")
//               .append (override ? "1" : "")
//               .append (");\n")
//               .append ("SET @event_score_id = SCOPE_IDENTITY();\n")
//
//               .append ("INSERT INTO Event_Score_Item (event_score_id, sequence, score, addendum)\n")
//               .append ("VALUES (@event_score_id, 0, '")
//               .append (away_score_last_set)
//               .append ("', '")
//               .append (away_score.replaceAll ("'", "''"))
//               .append ("');\n")
//
//               .append ("INSERT INTO Event_Score_Item (event_score_id, sequence, score, addendum)\n")
//               .append ("VALUES (@event_score_id, 1, '")
//               .append (home_score_last_set)
//               .append ("', '")
//               .append (home_score.replaceAll ("'", "''"))
//               .append ("');\n")
//               ;
//            //Debug.print ("sql>>>\n" + sql.toString () + "\n<<<");
//            //Main.db.executeUpdate (sql.toString ());
//
//            Event_Score local_event_score = event.getEvent_score ();
//            local_event_score.setAway_possession (away_possession);
//            local_event_score.setOverride        (override);
//            Event_Score_Item away_event_score_item = local_event_score.getEvent_score_items ().get (0);
//            Event_Score_Item home_event_score_item = local_event_score.getEvent_score_items ().get (1);
//            away_event_score_item.setAddendum (away_score);
//            home_event_score_item.setAddendum (home_score);
//            //JOptionPane.showMessageDialog (this, "Score NOT YET saved for (" + event_date + ")(" + event_number + ") FIX SQL!!!");
//            }
//        else
//            {
//            int period = 0;
//            if (status2 == null)
//                period = 0;
//            else if (status2.equals ("Time"))
//                {
//                int league_id = event.getLeague ().getId ();
//                switch (league_id)
//                    {
//                    case gsutils.data.League.NFL:
//                    case gsutils.data.League.UFL:
//                    case gsutils.data.League.CFB:
//                    case gsutils.data.League.NBA:
//                        period = 2;
//                        break;
//                    default:
//                        period = 1;
//                        break;
//                    }
//                }
//            else if (status2.length () > 0 && Character.isDigit (status2.charAt (0)))
//                {
//                if (status2.length () > 1 && Character.isDigit (status2.charAt (1)))
//                    period = Integer.parseInt (status2.substring (0, 2));
//                else
//                    period = Integer.parseInt (status2.substring (0, 1));
//                }
//            StringBuilder sql = new StringBuilder ();
//            sql.append ("DECLARE @event_score_id INT;\n")
//               .append ("INSERT INTO Event_Score (event_id, source_id, timestamp, feed_timestamp, event_completed, period_completed, period, status1, status2, status0, away_possession, override)\n")
//               .append ("VALUES (")
//               .append (event.getId ())
//               .append (", ")
//               .append (gsutils.data.Source.ODDSLOGIC)
//               .append (", SYSDATETIMEOFFSET()")
//               .append (", SYSDATETIMEOFFSET()")
//               .append (", ")
//               .append (status2 == null ? 0 : status2.equals ("Final") ? 1 : 0)
//               .append (", ")
//               .append (status2 == null ? 0 : status2.equals ("Final") || status2.equals ("End") || status2.equals ("Time") ? 1 : 0)
//               .append (", ")
//               .append (period)
//               .append (", '")
//               .append (status1 == null ? "" : status1)
//               .append ("', '")
//               .append (status2 == null ? "" : status2)
//               .append ("', '")
//               .append (status0 == null ? "" : status0)
//               .append ("', ")
//               .append (Trinary.db_value (away_possession))
//               .append (",")
//               .append (override ? "0" : "")
//               .append (",);\n")
//               .append ("SET @event_score_id = SCOPE_IDENTITY();\n")
//
//               .append ("INSERT INTO Event_Score_Item (event_score_id, sequence, score, addendum)\n")
//               .append ("VALUES (@event_score_id, 0, '")
//               .append (away_score.replaceAll ("'", "''"))
//               .append ("', NULL);\n")
//
//               .append ("INSERT INTO Event_Score_Item (event_score_id, sequence, score, addendum)\n")
//               .append ("VALUES (@event_score_id, 1, '")
//               .append (home_score.replaceAll ("'", "''"))
//               .append ("', NULL);\n")
//               ;
//            //Debug.print ("sql>>>\n" + sql.toString () + "\n<<<");
//            //Main.db.executeUpdate (sql.toString ());
//
//            Event_Score local_event_score = event.getEvent_score ();
//            if (local_event_score == null)
//                {
//                local_event_score = new Event_Score ();
//                event.setEvent_score (local_event_score);
//                local_event_score.getEvent_score_items ().add (new Event_Score_Item ());
//                local_event_score.getEvent_score_items ().add (new Event_Score_Item ());
//                }
//            local_event_score.setOverride (override);
//            Event_Score_Item away_event_score_item = local_event_score.getEvent_score_items ().get (0);
//            Event_Score_Item home_event_score_item = local_event_score.getEvent_score_items ().get (1);
//            away_event_score_item.setScore (away_score);
//            home_event_score_item.setScore (home_score);
//            //JOptionPane.showMessageDialog (this, "Score NOT YET saved for (" + event_date + ")(" + event_number + ") FIX SQL!!!");
//            }
//
//        message_buffer = (new StringBuilder ("SCORES>>>\n"))
//                                        .append (gsutils.HDF.create.Scores.get_hdf (Main.name, event))
//                                        .append ("\n<<<\n")
//                                        ;
//
//        if (Main.schedule_frame.isToday ())
//            {
//            Debug.print (message_buffer);
//            //Main.schedule_client.send (message_buffer);
//            Main.score_server_client.send (message_buffer);
//            }
//        }
    //-------------------------------------------------------------------------------------------------------------------------------------------
    private void set_tab_sequence ()
        {
        away_game_number.addActionListener ((ActionEvent e) ->
            {
            home_game_number.requestFocusInWindow();
            });

        home_game_number.addActionListener ((ActionEvent e) ->
            {
            away_score_TextField.requestFocusInWindow();
            });

        away_score_TextField.addActionListener ((ActionEvent e) ->
            {
            home_score_TextField.requestFocusInWindow();
            });

        home_score_TextField.addActionListener ((ActionEvent e) ->
            {
            status1_TextField.requestFocusInWindow();
            });

        status1_TextField.addActionListener ((ActionEvent e) ->
            {
            status2_TextField.requestFocusInWindow();
            });

        status2_TextField.addActionListener ((ActionEvent e) ->
            {
            event_status_ComboBox.requestFocusInWindow();
            });

        event_status_ComboBox.addActionListener ((ActionEvent e) ->
            {
            away_score_TextField.requestFocusInWindow();
            });

        }
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    public void parse_score ()
        {
        String away_score_text = away_score_TextField.getText ();
        if (away_score_text.contains (","))
            {
            //String [] away_score_parts
            }
        }
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    private void start_game ()
        {
        away_score_TextField.setText ("0");
        home_score_TextField.setText ("0");
        League league = event.getLeague ();
        if (league.getSport_id () == Sport.BASEBALL)
            {
            status1 = "Top";
            status2 = "1st";
            }
        else if (league.getPeriod_length () > 0)
            {
            status1 = league.getPeriod_length () + ":00";
            switch (league.getPeriods ())
                {
                case 2 -> status2 = "1st H";
                case 3 -> status2 = "1st P";
                case 4 -> status2 = "1st Q";
                }
            }
//        switch (event.getLeague ().getMain_league_id ())
//            {
//            case League.NFL:
//                status1 = "15:00";
//                status2 = "1st Q";
//                break;
//            case League.CFB:
//            case League.FCS:
//                status1 = "15:00";
//                status2 = "1st Q";
//                break;
//            case League.NBA:
//                status1 = "12:00";
//                status2 = "1st Q";
//                break;
//            case League.CBK:
//                status1 = "20:00";
//                status2 = "1st H";
//                break;
//            case League.MLB:
//            case League.CBB:
//                status1 = "Top";
//                status2 = "1st";
//                break;
//            case League.NHL:
//            case League.CHK:
//                status1 = "20:00";
//                status2 = "1st P";
//                break;
//            case League.WNBA:
//                status1 = "10:00";
//                status2 = "1st Q";
//                break;
//            case League.SOCCER:
//                status1 = "45:00";
//                status2 = "1st H";
//                break;
//            }
        status1_TextField.setText (status1);
        status2_TextField.setText (status2);
        }
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    private void halftime ()
        {
//        switch (event.getLeague ().getMain_league_id ())
//            {
//            case League.NFL:
//            case League.UFL:
//            case League.CFB:
//            case League.FCS:
//            case League.NBA:
//            case League.CBK:
//            case League.WNBA:
//            case League.SOCCER:
//                status1 = "Half";
//                status2 = "Time";
//                break;
//            }
                status1 = "Half";
                status2 = "Time";
        status1_TextField.setText (status1);
        status2_TextField.setText (status2);
        }
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    private void end_of_HT ()
        {
        switch (event.getLeague ().getMain_league_id ())
            {
            case League.NFL:
            case League.UFL:
            case League.CFB:
            case League.FCS:
                status1 = "15:00";
                status2 = "3rd Q";
                break;
            case League.NBA:
                status1 = "12:00";
                status2 = "3rd Q";
                break;
            case League.CBK:
                status1 = "20:00";
                status2 = "2nd H";
                break;
            case League.WNBA:
                status1 = "10:00";
                status2 = "3rd Q";
                break;
            case League.SOCCER:
                status1 = "45:00";
                status2 = "2nd H";
                break;
            }
        status1_TextField.setText (status1);
        status2_TextField.setText (status2);
        }
    //---------------------------------------------------------------------------------------------
    private void game_final ()
        {
//        switch (event.getLeague ().getMain_league_id ())
//            {
//            case League.NFL:
//            case League.CFB:
//            case League.FCS:
//            case League.NBA:
//            case League.CBK:
//            case League.WNBA:
//            case League.SOCCER:
//                status1 = "";
//                status2 = "Final";
//                break;
//            }
        status1 = "";
        status2 = "Final";
        status1_TextField.setText (status1);
        status2_TextField.setText (status2);
        }
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    public void linescore_button_selected ()
        {
        if (linescore_dialog == null)
            linescore_dialog = new LinescoreDialog (this, true);

        linescore_dialog.initialize (event);
        }
    //-----------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jSeparator1 = new javax.swing.JSeparator();
        save_Button = new javax.swing.JButton();
        league_value_Label = new javax.swing.JLabel();
        category_value_Label = new javax.swing.JLabel();
        score_Panel = new javax.swing.JPanel();
        away_Label = new javax.swing.JLabel();
        away_score_TextField = new javax.swing.JTextField();
        away_value_Label = new javax.swing.JTextField();
        home_value_Label = new javax.swing.JTextField();
        home_score_TextField = new javax.swing.JTextField();
        away_score_Label = new javax.swing.JLabel();
        status1_TextField = new javax.swing.JTextField();
        status2_TextField = new javax.swing.JTextField();
        status_Label = new javax.swing.JLabel();
        event_status_Label = new javax.swing.JLabel();
        event_status_ComboBox = new javax.swing.JComboBox<>();
        game_number_Label = new javax.swing.JLabel();
        away_game_number = new javax.swing.JTextField();
        home_game_number = new javax.swing.JTextField();
        league_Label = new javax.swing.JLabel();
        away_possession_btn = new javax.swing.JRadioButton();
        home_possession_btn = new javax.swing.JRadioButton();
        none_possession_btn = new javax.swing.JRadioButton();
        halftime_Button = new javax.swing.JButton();
        start_game_Button = new javax.swing.JButton();
        end_of_HT_Button = new javax.swing.JButton();
        final_Button = new javax.swing.JButton();
        override_CheckBox = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        source_ComboBox = new javax.swing.JComboBox<>();
        source_Label = new javax.swing.JLabel();
        score_correction_CheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        save_Button.setText("Save");
        save_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                save_ButtonActionPerformed(evt);
            }
        });

        league_value_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        league_value_Label.setText("League");
        league_value_Label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        category_value_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        category_value_Label.setText("Category");

        score_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        away_Label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        away_Label.setText("Team:");

        away_value_Label.setEditable(false);
        away_value_Label.setText("jTextField1");
        away_value_Label.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                away_value_LabelActionPerformed(evt);
            }
        });

        home_value_Label.setEditable(false);
        home_value_Label.setText("jTextField1");

        away_score_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        away_score_Label.setText("Score:");

        status_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        status_Label.setText("Status:");

        event_status_Label.setText("Event Status: ");

        game_number_Label.setText("GM#");

        away_game_number.setEditable(false);
        away_game_number.setText("999");

        home_game_number.setEditable(false);
        home_game_number.setText("999");

        javax.swing.GroupLayout score_PanelLayout = new javax.swing.GroupLayout(score_Panel);
        score_Panel.setLayout(score_PanelLayout);
        score_PanelLayout.setHorizontalGroup(
            score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(score_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(away_game_number, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(home_game_number, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addComponent(game_number_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(score_PanelLayout.createSequentialGroup()
                        .addComponent(away_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(away_value_Label)
                    .addComponent(home_value_Label))
                .addGap(18, 18, 18)
                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, score_PanelLayout.createSequentialGroup()
                        .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(home_score_TextField, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(away_score_TextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(score_PanelLayout.createSequentialGroup()
                        .addComponent(away_score_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)))
                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(status_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(status1_TextField)
                        .addComponent(status2_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(event_status_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(event_status_Label))
                .addContainerGap())
        );
        score_PanelLayout.setVerticalGroup(
            score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, score_PanelLayout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(game_number_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(away_score_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(status_Label)
                        .addComponent(event_status_Label)
                        .addComponent(away_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, score_PanelLayout.createSequentialGroup()
                        .addComponent(event_status_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))
                    .addGroup(score_PanelLayout.createSequentialGroup()
                        .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(score_PanelLayout.createSequentialGroup()
                                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(away_score_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(status1_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(home_score_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(status2_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(score_PanelLayout.createSequentialGroup()
                                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(away_value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(away_game_number, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(score_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(home_value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(home_game_number, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())))
        );

        league_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        league_Label.setText("Possession:");

        away_possession_btn.setText("Away");
        away_possession_btn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                away_possession_btnActionPerformed(evt);
            }
        });

        home_possession_btn.setText("Home");

        none_possession_btn.setText("None");

        halftime_Button.setText("Halftime");
        halftime_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                halftime_ButtonActionPerformed(evt);
            }
        });

        start_game_Button.setText("Start game");
        start_game_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                start_game_ButtonActionPerformed(evt);
            }
        });

        end_of_HT_Button.setText("End of HT");
        end_of_HT_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                end_of_HT_ButtonActionPerformed(evt);
            }
        });

        final_Button.setText("Final");
        final_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                final_ButtonActionPerformed(evt);
            }
        });

        override_CheckBox.setText("Override");

        jButton1.setText("Linescore");
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });

        source_ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        source_ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                source_ComboBoxActionPerformed(evt);
            }
        });

        source_Label.setText("Source:");

        score_correction_CheckBox.setText("Score CORRECTION");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(score_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(league_value_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(category_value_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(start_game_Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(halftime_Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(end_of_HT_Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(final_Button)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(override_CheckBox)
                                .addGap(49, 49, 49)
                                .addComponent(jButton1)
                                .addGap(50, 50, 50)
                                .addComponent(source_Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(source_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(score_correction_CheckBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(league_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(away_possession_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(23, 23, 23)
                                .addComponent(home_possession_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(none_possession_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(save_Button, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(league_value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(category_value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(score_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(halftime_Button)
                    .addComponent(start_game_Button)
                    .addComponent(end_of_HT_Button)
                    .addComponent(final_Button))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(league_Label)
                    .addComponent(away_possession_btn)
                    .addComponent(home_possession_btn)
                    .addComponent(none_possession_btn)
                    .addComponent(score_correction_CheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(save_Button)
                    .addComponent(override_CheckBox)
                    .addComponent(jButton1)
                    .addComponent(source_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(source_Label))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_ButtonActionPerformed
        save ();
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void away_possession_btnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_away_possession_btnActionPerformed
    {//GEN-HEADEREND:event_away_possession_btnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_away_possession_btnActionPerformed

    private void away_value_LabelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_away_value_LabelActionPerformed
    {//GEN-HEADEREND:event_away_value_LabelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_away_value_LabelActionPerformed

    private void start_game_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_start_game_ButtonActionPerformed
    {//GEN-HEADEREND:event_start_game_ButtonActionPerformed
        start_game ();
    }//GEN-LAST:event_start_game_ButtonActionPerformed

    private void halftime_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_halftime_ButtonActionPerformed
    {//GEN-HEADEREND:event_halftime_ButtonActionPerformed
        halftime ();
    }//GEN-LAST:event_halftime_ButtonActionPerformed

    private void end_of_HT_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_end_of_HT_ButtonActionPerformed
    {//GEN-HEADEREND:event_end_of_HT_ButtonActionPerformed
        end_of_HT ();
    }//GEN-LAST:event_end_of_HT_ButtonActionPerformed

    private void final_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_final_ButtonActionPerformed
    {//GEN-HEADEREND:event_final_ButtonActionPerformed
        game_final ();
    }//GEN-LAST:event_final_ButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
    linescore_button_selected ();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void source_ComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_source_ComboBoxActionPerformed
    {//GEN-HEADEREND:event_source_ComboBoxActionPerformed
        source_selected ();
    }//GEN-LAST:event_source_ComboBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ScoreDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ScoreDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ScoreDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ScoreDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() ->
            {
            ScoreDialog dialog = new ScoreDialog(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e)
                    {
                    System.exit(0);
                    }
                });
            dialog.setVisible(true);
            });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel away_Label;
    private javax.swing.JTextField away_game_number;
    private javax.swing.JRadioButton away_possession_btn;
    private javax.swing.JLabel away_score_Label;
    private javax.swing.JTextField away_score_TextField;
    private javax.swing.JTextField away_value_Label;
    private javax.swing.JLabel category_value_Label;
    private javax.swing.JButton end_of_HT_Button;
    private javax.swing.JComboBox<String> event_status_ComboBox;
    private javax.swing.JLabel event_status_Label;
    private javax.swing.JButton final_Button;
    private javax.swing.JLabel game_number_Label;
    private javax.swing.JButton halftime_Button;
    private javax.swing.JTextField home_game_number;
    private javax.swing.JRadioButton home_possession_btn;
    private javax.swing.JTextField home_score_TextField;
    private javax.swing.JTextField home_value_Label;
    private javax.swing.JButton jButton1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel league_Label;
    private javax.swing.JLabel league_value_Label;
    private javax.swing.JRadioButton none_possession_btn;
    private javax.swing.JCheckBox override_CheckBox;
    private javax.swing.JButton save_Button;
    private javax.swing.JPanel score_Panel;
    private javax.swing.JCheckBox score_correction_CheckBox;
    private javax.swing.JComboBox<String> source_ComboBox;
    private javax.swing.JLabel source_Label;
    private javax.swing.JButton start_game_Button;
    private javax.swing.JTextField status1_TextField;
    private javax.swing.JTextField status2_TextField;
    private javax.swing.JLabel status_Label;
    // End of variables declaration//GEN-END:variables
}
