/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool;

import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.HDF.create.Lines;
import gsutils.Utils;
import gsutils.data.Event;
import gsutils.data.Event_Item;
import gsutils.data.Event_Item_League_Prop;
import gsutils.data.Event_Item_League_Team;
import gsutils.data.League;
import gsutils.data.League_Prop;
import gsutils.data.League_Team;
import gsutils.data.Odds_Sportsbook;
import gsutils.data.Sport;
import gsutils.data.Sportsbook;
import gsutils.lines.Event_Line_Type;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.Enumeration;
import java.util.TreeMap;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

/**
 *
 * @author samla
 */
public class LinesDialog extends javax.swing.JDialog
    {
    static private       boolean send_header     = true;
    //---------------------------------------------------------------------------------------------
    private final Event event;
    private final Event_Item away_event_item;
    private final Event_Item home_event_item;
    private final TreeMap <Integer, Odds_Sportsbook> sportsbooks_map = new TreeMap <> ();
    //---------------------------------------------------------------------------------------------
    private boolean   saved;
    private boolean   error;
    //---------------------------------------------------------------------------------------------
    private League_Team away_team;
    private League_Prop away_prop;
    private League_Team home_team;
    private League_Prop home_prop;
    private Odds_Sportsbook selected_sportsbook;
    private int         selected_period_id = 0;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public LinesDialog (java.awt.Frame parent, boolean modal, Event event)
        {
        super (parent, modal);
        initComponents ();
        this.event = event;
        away_event_item = event.getEvent_items ().get (0);
        home_event_item = event.getEvent_items ().get (1);
        error = false;
        initialize ();
        disable_fields ();
        }
    //---------------------------------------------------------------------------------------------
    public boolean initialize_public ()
        {
        boolean successful = initialize ();
        error = !successful;
        return (successful);
        }
    //---------------------------------------------------------------------------------------------
    private boolean initialize ()
        {
        set_sportsbooks ();
        set_game_numbers ();
        set_teams ();
        populate_lines ();
        return (true);
        }
    //---------------------------------------------------------------------------------------------------------------
    private void set_sportsbooks ()
        {
        int index = 0;
        int default_sportsbook_index = 0;
        sportsbooks_map.clear ();

        TreeMap <String, Odds_Sportsbook> sort_sportsbooks = new TreeMap <> ();
        for (Odds_Sportsbook odds_sportsbook : Odds_Sportsbook.getSportsbook_map ().values ())
            {
            String name_to_use = odds_sportsbook.getName_to_use ();
            sort_sportsbooks.put (name_to_use, odds_sportsbook);
            }

        DefaultListModel model = new DefaultListModel ();
        for (Odds_Sportsbook odds_sportsbook : sort_sportsbooks.values ())
            {
            model.addElement (odds_sportsbook.getName_to_use ());
            if (odds_sportsbook.getSportsbook ().getId () == 41)
                {
                default_sportsbook_index = index;
                selected_sportsbook = odds_sportsbook;
                }
            sportsbooks_map.put (index++, odds_sportsbook);
            }
        sportsbook_List.setModel (model);
        sportsbook_List.setSelectedIndex (default_sportsbook_index);
        }
    //---------------------------------------------------------------------------------------------------------------
    private void set_game_numbers ()
        {
        date_Label.setText (DateTimeUtils.get_date_display_string (event.getDate ()));
        away_game_number_Label.setText ("" + event.getNumber ());
        home_game_number_Label.setText ("" + (event.getNumber () + 1));
        }
    //---------------------------------------------------------------------------------------------------------------
    private void set_teams ()
        {
        TreeMap <Integer, Event_Item> event_items = event.getEvent_items();
        Event_Item away_item = event_items.get(0);
        boolean away = true;
        set_team_label (away_item, away);

        Event_Item home_item = event_items.get(1);
        set_team_label (home_item, !away);
        }
    //---------------------------------------------------------------------------------------------
    private void set_team_label (Event_Item event_item, boolean away)
        {
        Event_Item_League_Team team_item = event_item.getEvent_item_league_team();
        Event_Item_League_Prop team_prop = event_item.getEvent_item_league_prop ();

        String text = "";
        if (team_item != null)
            {
            League_Team team = team_item.getLeague_team ();
            if (team != null)
                {
                text = team.getName ();
                if (away)
                    away_team = team;
                else
                    home_team = team;
                }
            }
        else if (team_prop != null)
            {
            League_Prop prop = team_prop.getLeague_prop ();
            if (prop != null)
                {
                text = prop.getProp ();
                if (away)
                    away_prop = prop;
                else
                    home_prop = prop;
                }
            }

        if (text.length () > 0)
            {
            if (away)
                away_team_Label.setText (text);
            else
                home_team_Label.setText (text);
            }
        }
    //---------------------------------------------------------------------------------------------
    private void period_changed ()
        {
        selected_period_id = Utils.parse_int (get_selected_radio_button (period_buttonGroup).getActionCommand ());
        populate_lines ();
        }
    //---------------------------------------------------------------------------------------------
    private void sportsbook_selected ()
        {
        int index = sportsbook_List.getSelectedIndex ();
        selected_sportsbook = sportsbooks_map.get (index);
        populate_lines ();
        enable_fields ();
        }
    //---------------------------------------------------------------------------------------------
    private void populate_lines ()
        {
        StringBuilder sql = new StringBuilder ()
            .append ("EXEC usp_Event_Lines '")
            .append (event.getDate ())
            .append ("', ")
            .append (event.getNumber ())
            .append (", ")
            .append (selected_sportsbook.getSportsbook ().getId ())
            .append (", ")
            .append (selected_period_id)
            ;
        Debug.print (sql);
        try
            {
            ResultSet rs = Main.db.executeQuery (sql);
            while (rs.next ())
                {
                int    sequence          = rs.getInt    ("sequence");
                double event_total       = rs.getDouble ("event_total");
                int    event_total_over  = rs.getInt    ("event_total_over");
                int    event_total_under = rs.getInt    ("event_total_under");
                double spread            = rs.getDouble ("spread");
                int    price             = rs.getInt    ("price");
                int    moneyline         = rs.getInt    ("ml");
                double team_total        = rs.getDouble ("tt");
                int    team_total_over   = rs.getInt    ("tt_over");
                int    team_total_under  = rs.getInt    ("tt_under");
                String string            = rs.getString ("string");

                switch (sequence)
                    {
                    case 0:
                        away_moneyline_TextField.setText (moneyline == 0 ? "" : "" + moneyline);
                        away_string_TextField   .setText (string == null ? "" : string);
                        if (price == 0)
                            {
                            away_spread_TextField.setText ("");
                            away_price_TextField .setText ("");
                            }
                        else
                            {
                            away_spread_TextField.setText ("" + spread);
                            away_price_TextField .setText ("" + price);
                            }
                        if (team_total_over == 0)
                            {
                            away_team_total_points_TextField.setText ("");
                            away_team_total_over_TextField  .setText ("");
                            away_team_total_under_TextField .setText ("");
                            }
                        else
                            {
                            away_team_total_points_TextField.setText ("" + team_total);
                            away_team_total_over_TextField  .setText ("" + team_total_over);
                            away_team_total_under_TextField .setText ("" + team_total_under);
                            }
                        if (event_total_over == 0)
                            {
                            total_points_TextField.setText ("");
                            total_over_TextField  .setText ("");
                            total_under_TextField .setText ("");
                            }
                        else
                            {
                            total_points_TextField.setText ("" + event_total);
                            total_over_TextField  .setText ("" + event_total_over);
                            total_under_TextField .setText ("" + event_total_under);
                            }
                        break;
                    case 1:
                        home_moneyline_TextField.setText (moneyline == 0 ? "" : "" + moneyline);
                        home_string_TextField   .setText (string == null ? "" : string);
                        if (price == 0)
                            {
                            home_spread_TextField.setText ("");
                            home_price_TextField .setText ("");
                            }
                        else
                            {
                            home_spread_TextField.setText ("" + spread);
                            home_price_TextField .setText ("" + price);
                            }
                        if (team_total_over == 0)
                            {
                            home_team_total_points_TextField.setText ("");
                            home_team_total_over_TextField  .setText ("");
                            home_team_total_under_TextField .setText ("");
                            }
                        else
                            {
                            home_team_total_points_TextField.setText ("" + team_total);
                            home_team_total_over_TextField  .setText ("" + team_total_over);
                            home_team_total_under_TextField .setText ("" + team_total_under);
                            }
                        if (event_total_over == 0)
                            {
                            total_points_TextField.setText ("");
                            total_over_TextField  .setText ("");
                            total_under_TextField .setText ("");
                            }
                        else
                            {
                            total_points_TextField.setText ("" + event_total);
                            total_over_TextField  .setText ("" + event_total_over);
                            total_under_TextField .setText ("" + event_total_under);
                            }
                        break;
                    }
                }
            }
        catch (Exception e)
            {
            Debug.print (e.toString ());
            e.printStackTrace ();
            }
        }
    //---------------------------------------------------------------------------------------------
    private JRadioButton get_selected_radio_button (javax.swing.ButtonGroup buttonGroup)
        {
        Enumeration<AbstractButton> abstractButtons = buttonGroup.getElements ();
        while (abstractButtons.hasMoreElements ())
            {
            JRadioButton radioButton = (JRadioButton) abstractButtons.nextElement();
            if (radioButton.isSelected ())
                return (radioButton);
            }
        return (null);
        }
    //---------------------------------------------------------------------------------------------
    public void save ()
        {
        saved = true;
        error = false;
        StringBuilder changes = new StringBuilder ("{");
        changes.append (DateTimeUtils.get_current_pacific_time ())
               .append (",")
               .append ((java.time.Instant.now().getEpochSecond()) / 1000)
               .append ("\n")
               ;
        int changes_header_length = changes.length ();
        long now_millis = OffsetDateTime.now ().toInstant ().toEpochMilli ();

        double total         = Utils.parse_double (total_points_TextField          .getText ());
        int    total_over    = Utils.parse_int    (total_over_TextField            .getText ());
        int    total_under   = Utils.parse_int    (total_under_TextField           .getText ());
        double away_spread   = Utils.parse_double (away_spread_TextField           .getText ());
        int    away_price    = Utils.parse_int    (away_price_TextField            .getText ());
        double home_spread   = Utils.parse_double (home_spread_TextField           .getText ());
        int    home_price    = Utils.parse_int    (home_price_TextField            .getText ());
        int    away_ml       = Utils.parse_int    (away_moneyline_TextField        .getText ());
        int    home_ml       = Utils.parse_int    (home_moneyline_TextField        .getText ());
        double away_tt       = Utils.parse_double (away_team_total_points_TextField.getText ());
        int    away_tt_over  = Utils.parse_int    (away_team_total_over_TextField  .getText ());
        int    away_tt_under = Utils.parse_int    (away_team_total_under_TextField .getText ());
        double home_tt       = Utils.parse_double (home_team_total_points_TextField.getText ());
        int    home_tt_over  = Utils.parse_int    (home_team_total_over_TextField  .getText ());
        int    home_tt_under = Utils.parse_int    (home_team_total_under_TextField .getText ());

        String away_string = away_string_TextField.getText ().trim ();
        String home_string = home_string_TextField.getText ().trim ();

        StringBuilder sql = new StringBuilder ();
        // Total
        sql .append ("EXEC usp_Insert_Event_Total ")
            .append (event.getId ())
            .append (", ")
            .append (selected_sportsbook.getSportsbook ().getId ())
            .append (", ")
            .append (selected_period_id)
            .append (", 0, ")
            .append (now_millis)
            .append (", ")
            .append (total_points_TextField.getText ().trim ().length () == 0 ? "0" : total_points_TextField.getText ())
            .append (", ")
            .append (total_over_TextField.getText ().trim ().length () == 0 ? "0" : total_over_TextField.getText ())
            .append (", ")
            .append (total_under_TextField.getText ().trim ().length () == 0 ? "0" : total_under_TextField.getText ())
            .append (";\n")
            ;
        changes.append (Lines.HDF_line
                    ( event.getDate ().toString ().replaceAll ("-", "")
                    , event.getNumber ()
                    , 0
                    , selected_sportsbook.getSportsbook ().getId ()
                    , selected_period_id
                    , !Lines.LAST_NUMBER
                    , !Lines.OPENER
                    , now_millis
                    , Event_Line_Type.TOTAL.ordinal ()
                    , new BigDecimal (total)
                    , total_over
                    , total_under
                    , null
                    ));
        // Away spread
        sql .append ("EXEC usp_Insert_Event_Item_Spread ")
            .append (away_event_item.getId ())
            .append (", ")
            .append (selected_sportsbook.getSportsbook ().getId ())
            .append (", ")
            .append (selected_period_id)
            .append (", 0, ")
            .append (now_millis)
            .append (", ")
            .append (away_spread_TextField.getText ().trim ().length () == 0 ? "0" : away_spread_TextField.getText ())
            .append (", ")
            .append (away_price_TextField.getText ().trim ().length () == 0 ? "0" : away_price_TextField.getText ())
            .append (";\n")
            ;
        changes.append (Lines.HDF_line
                    ( event.getDate ().toString ().replaceAll ("-", "")
                    , event.getNumber ()
                    , 0
                    , selected_sportsbook.getSportsbook ().getId ()
                    , selected_period_id
                    , !Lines.LAST_NUMBER
                    , !Lines.OPENER
                    , now_millis
                    , Event_Line_Type.SPREAD.ordinal ()
                    , new BigDecimal (away_spread)
                    , away_price
                    , null
                    , null
                    ));
        // Home spread
        sql .append ("EXEC usp_Insert_Event_Item_Spread ")
            .append (home_event_item.getId ())
            .append (", ")
            .append (selected_sportsbook.getSportsbook ().getId ())
            .append (", ")
            .append (selected_period_id)
            .append (", 0, ")
            .append (now_millis)
            .append (", ")
            .append (home_spread_TextField.getText ().trim ().length () == 0 ? "0" : home_spread_TextField.getText ())
            .append (", ")
            .append (home_price_TextField.getText ().trim ().length () == 0 ? "0" : home_price_TextField.getText ())
            .append (";\n")
            ;
        changes.append (Lines.HDF_line
                    ( event.getDate ().toString ().replaceAll ("-", "")
                    , event.getNumber ()
                    , 1
                    , selected_sportsbook.getSportsbook ().getId ()
                    , selected_period_id
                    , !Lines.LAST_NUMBER
                    , !Lines.OPENER
                    , now_millis
                    , Event_Line_Type.SPREAD.ordinal ()
                    , new BigDecimal (home_spread)
                    , home_price
                    , null
                    , null
                    ));
        // Away ML
        sql .append ("EXEC usp_Insert_Event_Item_ML ")
            .append (away_event_item.getId ())
            .append (", ")
            .append (selected_sportsbook.getSportsbook ().getId ())
            .append (", ")
            .append (selected_period_id)
            .append (", 0, ")
            .append (now_millis)
            .append (", ")
            .append (away_moneyline_TextField.getText ().trim ().length () == 0 ? "0" : away_moneyline_TextField.getText ())
            .append (";\n")
            ;
        changes.append (Lines.HDF_line
                    (event.getDate ().toString ().replaceAll ("-", "")
                    , event.getNumber ()
                    , 0
                    , selected_sportsbook.getSportsbook ().getId ()
                    , selected_period_id
                    , !Lines.LAST_NUMBER
                    , !Lines.OPENER
                    , now_millis
                    , Event_Line_Type.ML.ordinal ()
                    , null
                    , away_ml
                    , null
                    , null
                    ));
        // Home ML
        sql .append ("EXEC usp_Insert_Event_Item_ML ")
            .append (home_event_item.getId ())
            .append (", ")
            .append (selected_sportsbook.getSportsbook ().getId ())
            .append (", ")
            .append (selected_period_id)
            .append (", 0, ")
            .append (OffsetDateTime.now ().toInstant ().toEpochMilli ())
            .append (", ")
            .append (home_moneyline_TextField.getText ().trim ().length () == 0 ? "0" : home_moneyline_TextField.getText ())
            .append (";\n")
            ;
        changes.append (Lines.HDF_line
                    (event.getDate ().toString ().replaceAll ("-", "")
                    , event.getNumber ()
                    , 1
                    , selected_sportsbook.getSportsbook ().getId ()
                    , selected_period_id
                    , !Lines.LAST_NUMBER
                    , !Lines.OPENER
                    , now_millis
                    , Event_Line_Type.ML.ordinal ()
                    , null
                    , home_ml
                    , null
                    , null
                    ));

        // Away team-total
        sql .append ("EXEC usp_Insert_Event_Item_TT ")
            .append (away_event_item.getId ())
            .append (", ")
            .append (selected_sportsbook.getSportsbook ().getId ())
            .append (", ")
            .append (selected_period_id)
            .append (", 0, ")
            .append (OffsetDateTime.now ().toInstant ().toEpochMilli ())
            .append (", ")
            .append (away_team_total_points_TextField.getText ().trim ().length () == 0 ? "0" : away_team_total_points_TextField.getText ())
            .append (", ")
            .append (away_team_total_over_TextField.getText ().trim ().length () == 0 ? "0" : away_team_total_over_TextField.getText ())
            .append (", ")
            .append (away_team_total_under_TextField.getText ().trim ().length () == 0 ? "0" : away_team_total_under_TextField.getText ())
            .append (";\n")
            ;
        changes.append (Lines.HDF_line
                    ( event.getDate ().toString ().replaceAll ("-", "")
                    , event.getNumber ()
                    , 0
                    , selected_sportsbook.getSportsbook ().getId ()
                    , selected_period_id
                    , !Lines.LAST_NUMBER
                    , !Lines.OPENER
                    , now_millis
                    , Event_Line_Type.TT.ordinal ()
                    , new BigDecimal (away_tt)
                    , away_tt_over
                    , away_tt_under
                    , null
                    ));
        // Home team-total
        sql .append ("EXEC usp_Insert_Event_Item_TT ")
            .append (home_event_item.getId ())
            .append (", ")
            .append (selected_sportsbook.getSportsbook ().getId ())
            .append (", ")
            .append (selected_period_id)
            .append (", 0, ")
            .append (OffsetDateTime.now ().toInstant ().toEpochMilli ())
            .append (", ")
            .append (home_team_total_points_TextField.getText ().trim ().length () == 0 ? "0" : home_team_total_points_TextField.getText ())
            .append (", ")
            .append (home_team_total_over_TextField.getText ().trim ().length () == 0 ? "0" : home_team_total_over_TextField.getText ())
            .append (", ")
            .append (home_team_total_under_TextField.getText ().trim ().length () == 0 ? "0" : home_team_total_under_TextField.getText ())
            .append (";\n")
            ;
        changes.append (Lines.HDF_line
                    ( event.getDate ().toString ().replaceAll ("-", "")
                    , event.getNumber ()
                    , 1
                    , selected_sportsbook.getSportsbook ().getId ()
                    , selected_period_id
                    , !Lines.LAST_NUMBER
                    , !Lines.OPENER
                    , now_millis
                    , Event_Line_Type.TT.ordinal ()
                    , new BigDecimal (home_tt)
                    , home_tt_over
                    , home_tt_under
                    , null
                    ));

        League league = League.get_league (event.getLeague ().getId ());
        Sport  sport  = Sport.get_sport (league.getSport_id ());
        int event_line_type;
        boolean ML;
        if (sport.isML ())
            {
            event_line_type = Event_Line_Type.STRING_ML.ordinal ();
            ML = true;
            }
        else
            {
            event_line_type = Event_Line_Type.STRING_spread_or_total.ordinal ();
            ML = false;
            }
        // Away string
        sql .append ("EXEC usp_Insert_Event_Item_String ")
            .append (away_event_item.getId ())
            .append (",")
            .append (ML ? 1 : 0)
            .append (",")
            .append (selected_sportsbook.getSportsbook ().getId ())
            .append (",")
            .append (selected_period_id)
            .append (", 0, '")
            .append (OffsetDateTime.now ().toInstant ().toEpochMilli ())
            .append ("','")
            .append (away_string.replaceAll ("'", "''"))
            .append ("',")
            .append ("NULL")
            .append (",")
            .append ("NULL")
            .append (",")
            .append ("NULL")
            .append (";\n")
            ;
        changes.append (Lines.HDF_line
                    ( event.getDate ().toString ().replaceAll ("-", "")
                    , event.getNumber ()
                    , 0
                    , selected_sportsbook.getSportsbook ().getId ()
                    , selected_period_id
                    , !Lines.LAST_NUMBER
                    , !Lines.OPENER
                    , now_millis
                    , event_line_type
                    , null
                    , null
                    , null
                    , away_string
                    ));
        // Home string
        sql .append ("EXEC usp_Insert_Event_Item_String ")
            .append (home_event_item.getId ())
            .append (",")
            .append (ML ? 1 : 0)
            .append (",")
            .append (selected_sportsbook.getSportsbook ().getId ())
            .append (",")
            .append (selected_period_id)
            .append (", 0, '")
            .append (OffsetDateTime.now ().toInstant ().toEpochMilli ())
            .append ("','")
            .append (home_string.replaceAll ("'", "''"))
            .append ("',")
            .append ("NULL")
            .append (",")
            .append ("NULL")
            .append (",")
            .append ("NULL")
            .append (";\n")
            ;
        changes.append ( // saving scores in a column for lines as strings
            Lines.HDF_line
                ( event.getDate ().toString ().replaceAll ("-", "")
                , event.getNumber ()
                , 1
                , selected_sportsbook.getSportsbook ().getId ()
                , selected_period_id
                , !Lines.LAST_NUMBER
                , !Lines.OPENER
                , now_millis
                , event_line_type
                , null
                , null
                , null
                , home_string
                ));

        Debug.print (sql);
        Main.db.executeUpdate (sql);

        StringBuilder local_buffer = new StringBuilder ();
        if (changes.length () > changes_header_length)
            {
            if (send_header)
                {
                local_buffer.append ("LINES-HEADER>>>\n");
                local_buffer.append (Lines.get_header ());
                local_buffer.append ("\n<<<\n");
                send_header = false;
                changes.append ("}");
                }

            local_buffer.append ("LINES>>>\n");
            local_buffer.append (changes).append ("}");
            local_buffer.append ("\n<<<\n");
            }
        Main.schedule_client.send (local_buffer);
        setVisible (false);
        }
    //---------------------------------------------------------------------------------------------
    public void cancel ()
        {
        saved = false;
        error = false;
        setVisible (false);
        }
    //---------------------------------------------------------------------------------------------
    public boolean isSaved ()
        {
        return saved;
        }
    //---------------------------------------------------------------------------------------------
    public boolean isError ()
        {
        return error;
        }
    //---------------------------------------------------------------------------------------------
    private void enable_fields ()
        {
        enable_disable_fields (true);
        }
    //---------------------------------------------------------------------------------------------
    private void disable_fields ()
        {
        enable_disable_fields (false);
        }
    //---------------------------------------------------------------------------------------------
    private void enable_disable_fields (boolean enabled)
        {
        // Periods
        period_Panel                    .setEnabled (enabled);
        game_RadioButton                .setEnabled (enabled);
        h1_RadioButton                  .setEnabled (enabled);
        h2_RadioButton                  .setEnabled (enabled);
        q1_RadioButton                  .setEnabled (enabled);
        q2_RadioButton                  .setEnabled (enabled);
        q3_RadioButton                  .setEnabled (enabled);
        q4_RadioButton                  .setEnabled (enabled);
        // Labels
        date_Label                      .setEnabled (enabled);
        moneylines_Label                .setEnabled (enabled);
        spread_Label                    .setEnabled (enabled);
        price_Label                     .setEnabled (enabled);
        team_totals_Label               .setEnabled (enabled);
        total_Label                     .setEnabled (enabled);
        string_Label                    .setEnabled (enabled);
        // Event
        away_game_number_Label          .setEnabled (enabled);
        away_team_Label                 .setEnabled (enabled);
        home_game_number_Label          .setEnabled (enabled);
        home_team_Label                 .setEnabled (enabled);
        // data
        away_moneyline_TextField        .setEnabled (enabled);
        home_moneyline_TextField        .setEnabled (enabled);
        away_spread_TextField           .setEnabled (enabled);
        away_price_TextField            .setEnabled (enabled);
        home_spread_TextField           .setEnabled (enabled);
        home_price_TextField            .setEnabled (enabled);
        away_team_total_points_TextField.setEnabled (enabled);
        away_team_total_over_TextField  .setEnabled (enabled);
        away_team_total_under_TextField .setEnabled (enabled);
        home_team_total_points_TextField.setEnabled (enabled);
        home_team_total_over_TextField  .setEnabled (enabled);
        home_team_total_under_TextField .setEnabled (enabled);
        away_string_TextField           .setEnabled (enabled);
        home_string_TextField           .setEnabled (enabled);
        total_points_TextField          .setEnabled (enabled);
        total_over_TextField            .setEnabled (enabled);
        total_under_TextField           .setEnabled (enabled);
        }
    //---------------------------------------------------------------------------------------------
    private void clear_all_lines ()
        {
        away_moneyline_TextField        .setText ("");
        home_moneyline_TextField        .setText ("");
        away_spread_TextField           .setText ("");
        away_price_TextField            .setText ("");
        home_spread_TextField           .setText ("");
        home_price_TextField            .setText ("");
        away_team_total_points_TextField.setText ("");
        away_team_total_over_TextField  .setText ("");
        away_team_total_under_TextField .setText ("");
        home_team_total_points_TextField.setText ("");
        home_team_total_over_TextField  .setText ("");
        home_team_total_under_TextField .setText ("");
        away_string_TextField           .setText ("");
        home_string_TextField           .setText ("");
        total_points_TextField          .setText ("");
        total_over_TextField            .setText ("");
        total_under_TextField           .setText ("");
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

        period_buttonGroup = new javax.swing.ButtonGroup();
        line_type_buttonGroup = new javax.swing.ButtonGroup();
        period_Panel = new javax.swing.JPanel();
        game_RadioButton = new javax.swing.JRadioButton();
        h1_RadioButton = new javax.swing.JRadioButton();
        h2_RadioButton = new javax.swing.JRadioButton();
        q1_RadioButton = new javax.swing.JRadioButton();
        q2_RadioButton = new javax.swing.JRadioButton();
        q3_RadioButton = new javax.swing.JRadioButton();
        q4_RadioButton = new javax.swing.JRadioButton();
        save_Button = new javax.swing.JButton();
        moneylines_Label = new javax.swing.JLabel();
        away_moneyline_TextField = new javax.swing.JTextField();
        home_moneyline_TextField = new javax.swing.JTextField();
        spread_Label = new javax.swing.JLabel();
        away_spread_TextField = new javax.swing.JTextField();
        away_price_TextField = new javax.swing.JTextField();
        price_Label = new javax.swing.JLabel();
        home_price_TextField = new javax.swing.JTextField();
        home_spread_TextField = new javax.swing.JTextField();
        away_team_Label = new javax.swing.JLabel();
        home_team_Label = new javax.swing.JLabel();
        away_team_total_points_TextField = new javax.swing.JTextField();
        home_team_total_points_TextField = new javax.swing.JTextField();
        away_team_total_over_TextField = new javax.swing.JTextField();
        away_team_total_under_TextField = new javax.swing.JTextField();
        home_team_total_over_TextField = new javax.swing.JTextField();
        home_team_total_under_TextField = new javax.swing.JTextField();
        team_totals_Label = new javax.swing.JLabel();
        total_Label = new javax.swing.JLabel();
        total_points_TextField = new javax.swing.JTextField();
        total_over_TextField = new javax.swing.JTextField();
        total_under_TextField = new javax.swing.JTextField();
        away_game_number_Label = new javax.swing.JLabel();
        home_game_number_Label = new javax.swing.JLabel();
        date_Label = new javax.swing.JLabel();
        sportsbook_Panel = new javax.swing.JPanel();
        sportsbook_ScrollPane = new javax.swing.JScrollPane();
        sportsbook_List = new javax.swing.JList<>();
        away_string_TextField = new javax.swing.JTextField();
        home_string_TextField = new javax.swing.JTextField();
        string_Label = new javax.swing.JLabel();
        clear_all_lines_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        period_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Period"));

        period_buttonGroup.add(game_RadioButton);
        game_RadioButton.setSelected(true);
        game_RadioButton.setText("Game");
        game_RadioButton.setActionCommand("0");
        game_RadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                period_RadioButtonActionPerformed(evt);
            }
        });

        period_buttonGroup.add(h1_RadioButton);
        h1_RadioButton.setText("1st H");
        h1_RadioButton.setActionCommand("1");
        h1_RadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                period_RadioButtonActionPerformed(evt);
            }
        });

        period_buttonGroup.add(h2_RadioButton);
        h2_RadioButton.setText("2nd H");
        h2_RadioButton.setActionCommand("2");
        h2_RadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                period_RadioButtonActionPerformed(evt);
            }
        });

        period_buttonGroup.add(q1_RadioButton);
        q1_RadioButton.setText("1st Q");
        q1_RadioButton.setActionCommand("3");
        q1_RadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                period_RadioButtonActionPerformed(evt);
            }
        });

        period_buttonGroup.add(q2_RadioButton);
        q2_RadioButton.setText("2nd Q");
        q2_RadioButton.setActionCommand("4");
        q2_RadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                period_RadioButtonActionPerformed(evt);
            }
        });

        period_buttonGroup.add(q3_RadioButton);
        q3_RadioButton.setText("3rd Q");
        q3_RadioButton.setActionCommand("5");
        q3_RadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                period_RadioButtonActionPerformed(evt);
            }
        });

        period_buttonGroup.add(q4_RadioButton);
        q4_RadioButton.setText("4th Q");
        q4_RadioButton.setActionCommand("6");
        q4_RadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                period_RadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout period_PanelLayout = new javax.swing.GroupLayout(period_Panel);
        period_Panel.setLayout(period_PanelLayout);
        period_PanelLayout.setHorizontalGroup(
            period_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(period_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(period_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(game_RadioButton)
                    .addComponent(h1_RadioButton)
                    .addComponent(h2_RadioButton)
                    .addComponent(q1_RadioButton)
                    .addComponent(q2_RadioButton)
                    .addComponent(q3_RadioButton)
                    .addComponent(q4_RadioButton))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        period_PanelLayout.setVerticalGroup(
            period_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(period_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(game_RadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(h1_RadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(h2_RadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(q1_RadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(q2_RadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(q3_RadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(q4_RadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        save_Button.setText("Save");
        save_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                save_ButtonActionPerformed(evt);
            }
        });

        moneylines_Label.setText("Moneylines");

        spread_Label.setText("Spread");

        price_Label.setText("Price");

        away_team_Label.setText("away-team");

        home_team_Label.setText("home-team");

        team_totals_Label.setText("Team-totals");

        total_Label.setText("Total");

        away_game_number_Label.setText("away#");

        home_game_number_Label.setText("home#");

        sportsbook_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Sportsbook"));

        sportsbook_List.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sportsbook_List.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                sportsbook_ListMousePressed(evt);
            }
        });
        sportsbook_List.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                sportsbook_ListValueChanged(evt);
            }
        });
        sportsbook_ScrollPane.setViewportView(sportsbook_List);

        javax.swing.GroupLayout sportsbook_PanelLayout = new javax.swing.GroupLayout(sportsbook_Panel);
        sportsbook_Panel.setLayout(sportsbook_PanelLayout);
        sportsbook_PanelLayout.setHorizontalGroup(
            sportsbook_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sportsbook_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sportsbook_ScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                .addContainerGap())
        );
        sportsbook_PanelLayout.setVerticalGroup(
            sportsbook_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sportsbook_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sportsbook_ScrollPane)
                .addContainerGap())
        );

        string_Label.setText("String");

        clear_all_lines_Button.setText("Clear all lines");
        clear_all_lines_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                clear_all_lines_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(total_points_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(total_over_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(total_under_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(save_Button))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(date_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(29, 29, 29))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(away_game_number_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(home_game_number_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(away_team_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(home_team_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(sportsbook_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(home_moneyline_TextField)
                                            .addComponent(moneylines_Label, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(away_moneyline_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(spread_Label)
                                                    .addComponent(away_spread_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(home_spread_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(price_Label)
                                                    .addComponent(away_price_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(home_price_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(home_team_total_points_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(home_team_total_over_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(home_team_total_under_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(home_string_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(layout.createSequentialGroup()
                                                                .addComponent(away_team_total_points_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(away_team_total_over_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(away_team_total_under_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addComponent(team_totals_Label))
                                                        .addGap(18, 18, 18)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(string_Label)
                                                            .addComponent(away_string_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                            .addComponent(clear_all_lines_Button)))
                                    .addComponent(period_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(total_Label))
                        .addGap(0, 71, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {away_moneyline_TextField, away_price_TextField, away_spread_TextField, away_string_TextField, away_team_total_over_TextField, away_team_total_points_TextField, away_team_total_under_TextField, home_moneyline_TextField, home_price_TextField, home_spread_TextField, home_string_TextField, home_team_total_over_TextField, home_team_total_points_TextField, home_team_total_under_TextField, total_over_TextField, total_points_TextField, total_under_TextField});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(period_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sportsbook_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spread_Label)
                            .addComponent(price_Label)
                            .addComponent(team_totals_Label)
                            .addComponent(moneylines_Label)
                            .addComponent(string_Label))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(away_spread_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(away_price_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(away_team_total_points_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(away_team_total_over_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(away_team_total_under_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(away_string_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(home_spread_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(home_price_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(home_team_total_points_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(home_team_total_over_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(home_team_total_under_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(home_string_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(date_Label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(away_team_Label)
                            .addComponent(away_moneyline_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(away_game_number_Label))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(home_moneyline_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(home_team_Label)
                            .addComponent(home_game_number_Label))))
                .addGap(23, 23, 23)
                .addComponent(total_Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(total_points_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(total_over_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(total_under_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(save_Button)
                    .addComponent(clear_all_lines_Button))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_save_ButtonActionPerformed
    {//GEN-HEADEREND:event_save_ButtonActionPerformed
        save ();
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void sportsbook_ListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_sportsbook_ListValueChanged
    {//GEN-HEADEREND:event_sportsbook_ListValueChanged
        //sportsbook_selected ();
    }//GEN-LAST:event_sportsbook_ListValueChanged

    private void period_RadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_period_RadioButtonActionPerformed
    {//GEN-HEADEREND:event_period_RadioButtonActionPerformed
        period_changed ();
    }//GEN-LAST:event_period_RadioButtonActionPerformed

    private void sportsbook_ListMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_sportsbook_ListMousePressed
    {//GEN-HEADEREND:event_sportsbook_ListMousePressed
        sportsbook_selected ();
    }//GEN-LAST:event_sportsbook_ListMousePressed

    private void clear_all_lines_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clear_all_lines_ButtonActionPerformed
    {//GEN-HEADEREND:event_clear_all_lines_ButtonActionPerformed
        clear_all_lines ();
    }//GEN-LAST:event_clear_all_lines_ButtonActionPerformed
    //---------------------------------------------------------------------------------------------
    public static void main (JFrame parent, Event event)
        {
       // java.awt.EventQueue.invokeLater (() ->
            {
            Main.lines_dialog = new LinesDialog (parent, true, event);
            Main.lines_dialog.addWindowListener (new java.awt.event.WindowAdapter ()
                {
                @Override
                public void windowClosing (java.awt.event.WindowEvent e)
                    {
                    }
                    });
            }//);
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel away_game_number_Label;
    private javax.swing.JTextField away_moneyline_TextField;
    private javax.swing.JTextField away_price_TextField;
    private javax.swing.JTextField away_spread_TextField;
    private javax.swing.JTextField away_string_TextField;
    private javax.swing.JLabel away_team_Label;
    private javax.swing.JTextField away_team_total_over_TextField;
    private javax.swing.JTextField away_team_total_points_TextField;
    private javax.swing.JTextField away_team_total_under_TextField;
    private javax.swing.JButton clear_all_lines_Button;
    private javax.swing.JLabel date_Label;
    private javax.swing.JRadioButton game_RadioButton;
    private javax.swing.JRadioButton h1_RadioButton;
    private javax.swing.JRadioButton h2_RadioButton;
    private javax.swing.JLabel home_game_number_Label;
    private javax.swing.JTextField home_moneyline_TextField;
    private javax.swing.JTextField home_price_TextField;
    private javax.swing.JTextField home_spread_TextField;
    private javax.swing.JTextField home_string_TextField;
    private javax.swing.JLabel home_team_Label;
    private javax.swing.JTextField home_team_total_over_TextField;
    private javax.swing.JTextField home_team_total_points_TextField;
    private javax.swing.JTextField home_team_total_under_TextField;
    private javax.swing.ButtonGroup line_type_buttonGroup;
    private javax.swing.JLabel moneylines_Label;
    private javax.swing.JPanel period_Panel;
    private javax.swing.ButtonGroup period_buttonGroup;
    private javax.swing.JLabel price_Label;
    private javax.swing.JRadioButton q1_RadioButton;
    private javax.swing.JRadioButton q2_RadioButton;
    private javax.swing.JRadioButton q3_RadioButton;
    private javax.swing.JRadioButton q4_RadioButton;
    private javax.swing.JButton save_Button;
    private javax.swing.JList<String> sportsbook_List;
    private javax.swing.JPanel sportsbook_Panel;
    private javax.swing.JScrollPane sportsbook_ScrollPane;
    private javax.swing.JLabel spread_Label;
    private javax.swing.JLabel string_Label;
    private javax.swing.JLabel team_totals_Label;
    private javax.swing.JLabel total_Label;
    private javax.swing.JTextField total_over_TextField;
    private javax.swing.JTextField total_points_TextField;
    private javax.swing.JTextField total_under_TextField;
    // End of variables declaration//GEN-END:variables
    }
