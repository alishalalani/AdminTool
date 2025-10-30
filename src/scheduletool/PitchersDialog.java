
package scheduletool;

import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.data.Event;
import gsutils.data.Event_Item;
import gsutils.data.Event_Item_League_Team;
import gsutils.data.League_Player;
import gsutils.data.League_Position;
import gsutils.data.League_Team;
import gsutils.data.League_Team_Player;
import gsutils.data.Player;
import java.sql.ResultSet;
import java.util.TreeMap;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;


public class PitchersDialog extends javax.swing.JDialog
    {
    private final TreeMap <Integer, League_Team_Player> players_by_id            = new TreeMap <> ();
    private final TreeMap <String , League_Team_Player> players_id_by_name       = new TreeMap <> ();
    private final TreeMap <String , League_Team_Player> players_id_by_last_first = new TreeMap <> ();
    //---------------------------------------------------------------------------------------------
    // pitcher is position 1 in treemap
    //---------------------------------------------------------------------------------------------
    private boolean   saved;
    private boolean   error;
    //---------------------------------------------------------------------------------------------
    private Event              event;
    private Event_Item         event_item;
    private League_Team_Player original_pitcher;
    private League_Team_Player selected_pitcher;
    private boolean            original_override;

    private ArrayList <League_Team_Player> player_items;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public PitchersDialog (java.awt.Frame parent, boolean modal, Event event, Event_Item event_item, League_Team_Player pitcher, boolean override)
        {
        super (parent, modal);
        initComponents ();

        setTitle ("Select Pitcher");

        this.event             = event;
        this.event_item        = event_item;
        this.original_pitcher  = pitcher;
        this.original_override = override;

        saved          = false;
        error          = false;

        boolean successful = initialize (event_item.getId());
        error = !successful;
        }
    //---------------------------------------------------------------------------------------------
    public boolean initialize_public (Event event, Event_Item event_item, League_Team_Player pitcher)
        {
        this.event            = event;
        this.event_item       = event_item;
        this.original_pitcher = pitcher;

        boolean successful = initialize (event_item.getId());

        error = !successful;
        return (successful);
        }
    //---------------------------------------------------------------------------------------------
    private boolean initialize (int event_item_id)
        {
        override_CheckBox.setSelected (original_override);

        player_items = new ArrayList <> ();
        try
            {
            players_by_id           .clear ();
            players_id_by_name      .clear ();
            players_id_by_last_first.clear ();

            TreeMap <Integer, Event_Item> event_items = Main.schedule.getEvent_items_by_id ();
            Event_Item local_event_item = event_items.get (event_item_id);
            Event_Item_League_Team event_item_league_team = local_event_item.getEvent_item_league_team ();
            League_Team league_team = event_item_league_team.getLeague_team ();

            league_team = League_Team.getLeague_team_by_ID ().get (league_team.getId ());

            for (Integer id : league_team.getLeague_team_player_by_ID ().keySet ())
                {
                int current_player_id = id;
                if (current_player_id == 0)
                    {
                    JOptionPane.showMessageDialog (this, "No pitchers found!");
                    cancel ();
                    return (false);
                    }

                League_Team_Player league_team_player = league_team.getLeague_team_player_by_ID ().get (id);
                League_Player      league_player      = league_team_player.getLeague_player ();
                Player             player             = league_player.getPlayer ();

                players_by_id           .put (league_team_player.getId (), league_team_player);
                players_id_by_name      .put (player.getDisplay_name (), league_team_player);
                String last_first_key = player.getLast_name () + ", " + player.getFirst_name () + " (" + player.getDisplay_name () + ")";
                players_id_by_last_first.put (last_first_key, league_team_player);
                }

            System.out.println ("players by id size: " + players_by_id.size ());

            // Figure out where in the ArrayList the original-pitcher is
            int i = 0;
            int pitcher_index = -1;

            DefaultListModel model = new DefaultListModel ();
            for (String last_first : players_id_by_last_first.keySet ())
                {
                League_Team_Player league_team_player = players_id_by_last_first.get (last_first);
                player_items.add (league_team_player);
//if (league_team_player.getLeague_player ().getPlayer ().getLast_name ().toUpperCase ().equals ("BRADISH"))
//    System.out.println ("debug");
                model.addElement (last_first);
                int pitcher_id = league_team_player.getId ();
                if (original_pitcher != null && pitcher_id == original_pitcher.getId ())
                    pitcher_index = i;
                i++;
                }
            pitchers_List.setModel (model);

            if (pitcher_index >= 0)
                {
                pitchers_List.setSelectedIndex (pitcher_index);
                pitchers_List.ensureIndexIsVisible (pitcher_index);
                }

            System.out.println ("PITCHER INDEX: " + pitcher_index);
            }
        catch (Exception e)
            {
            String message = "Exception (" + e + ")";
            Debug.print (message);
            e.printStackTrace ();
            JOptionPane.showMessageDialog (this, message);
            cancel ();
            return (false);
            }
        return (true);
        }
    //---------------------------------------------------------------------------------------------
    // not currently used
    //---------------------------------------------------------------------------------------------
    /*
    private boolean initialize_db (int event_item_id)
        {
        selected_pitcher = null;
        player_items = new ArrayList <> ();
        TreeSet <String> set = new TreeSet <> ();
        String sql =   "SELECT p.id           AS player_id"
                   + "\n     , p.display_name AS player_display_name"
                   + "\n     , p.first_name   AS player_first_name"
                   + "\n     , p.last_name    AS player_last_name"
                   + "\n     , p.left_handed  AS player_left_handed"
                   + "\n     , ltp.id         AS league_team_player_id"
                   + "\n  FROM Event_Item AS ei"
                   + "\n  LEFT OUTER JOIN Event_Item_League_Team AS eilt ON eilt.event_item_id = ei.id AND eilt.timestamp = (SELECT MAX(timestamp) FROM Event_Item_League_Team AS eilt1 WHERE eilt1.event_item_id = ei.id)"
                   + "\n  LEFT OUTER JOIN League_Team AS lt ON lt.id = eilt.league_team_id"
                   + "\n  LEFT OUTER JOIN League_Team_Player AS ltp ON ltp.league_team_id = lt.id"
                   + "\n  LEFT OUTER JOIN League_Player AS lp ON lp.id = ltp.league_player_id"
                   + "\n  LEFT OUTER JOIN Player AS p ON p.id = lp.player_id"
                   + "\n WHERE ei.id = " + event_item_id
                   ;
        try
            {
            players_by_id     .clear ();
            players_id_by_name.clear ();
            Debug.print ("SQL>>>\n" + sql + "\n<<<");
            ResultSet rs = Main.db.executeQuery (sql);
            while (rs.next ())
                {
                int current_player_id = rs.getInt    ("player_id");
                if (current_player_id == 0)
                    {
                    JOptionPane.showMessageDialog (this, "No pitchers found!");
                    cancel ();
                    return (false);
                    }
                this.player_item = new Player_Item ();
                player_item.setId           (current_player_id);
                player_item.setDisplay_name (rs.getString ("player_display_name"));
                player_item.setFirst_name   (rs.getString ("player_first_name"));
                player_item.setLast_name    (rs.getString ("player_last_name"));
                player_item.setLeft_handed  (rs.getBoolean ("player_left_handed"));
                player_item.setLeague_team_player_id (rs.getInt ("league_team_player_id"));

                players_by_id.put (player_item.getId (), player_item);
                players_id_by_name.put (player_item.getDisplay_name (), player_item.getId ());
                player_items.add (player_item);
                }

            System.out.println ("players by id size: " + players_by_id.size ());

            //setting pitcher
            TreeMap <Integer, League_Team_Player> players =  event_item.getEvent_item_league_team().getLeague_team_players();
            League_Team_Player league_team_player = players.get(1);
            //System.out.println ("league team player ID: " + league_team_player.getId());
            Player pitcher = null;
            if (league_team_player != null)
                {
                League_Player league_player = league_team_player.getLeague_player();
                if (league_player != null)
                    {
                    //System.out.println ("league player ID: " + league_player.getId());
                    pitcher = league_player.getPlayer();
                    }
                }
            //System.out.println ("pitcher name: " + pitcher.getDisplay_name());
            //System.out.println ("pitcher id: " + pitcher.getId());

            int i = 0;
            int pitcher_index = -1;

            DefaultListModel model = new DefaultListModel ();
            for (Player player : players_by_id.values ())
                {
                model.addElement (player.getDisplay_name ());
                //pitcher is position 1 in treemap

                if (pitcher != null && player.getId() == pitcher.getId())
                    {
                    pitcher_index = i;
                    //break;
                    }
                i++;
                }
            pitchers_List.setModel (model);
            for (String name : set)
                model.addElement (name);
            pitchers_List.setModel (model);

            if (pitcher_index >= 0)
                {
                pitchers_List.setSelectedIndex(pitcher_index);
                pitchers_List.ensureIndexIsVisible(pitcher_index);
                }

            System.out.println ("PITCHER INDEX: " + pitcher_index);
            }
        catch (Exception e)
            {
            String message = "Exception (" + e + ")";
            Debug.print (message);
            e.printStackTrace ();
            JOptionPane.showMessageDialog (this, message);
            cancel ();
            return (false);
            }
        return (true);
        }
    */
    //---------------------------------------------------------------------------------------------
    public void save ()
        {
        if (pitchers_List.getSelectedIndex () < 0)
            {
            JOptionPane.showMessageDialog (this, "Please select a pitcher");
            }
        else
            {
            int index = pitchers_List.getSelectedIndex ();
            selected_pitcher = player_items.get (index);
            boolean override = override_CheckBox.isSelected ();

            System.out.println ("selected pitcher: " + selected_pitcher.getLeague_player ().getPlayer ().getDisplay_name ());

            if (   original_pitcher == null
                || selected_pitcher.getId () != original_pitcher.getId ()
                || override != original_override
                )
                {
                StringBuilder sql = new StringBuilder ()
                    .append ("DECLARE @event_id INT;\n")
                    .append ("DECLARE @event_item_id INT;\n")
                    .append ("DECLARE @league_team_id INT;\n")
                    .append ("DECLARE @league_team_player_id INT;\n")
                    .append ("\n")
                    .append ("DECLARE @league_player_id INT;\n")
                    .append ("DECLARE @event_item_league_team_id INT;\n")
                    .append ("DECLARE @event_item_league_team_player_id INT;\n")
                    .append ("DECLARE @event_item_league_team_player_override INT;\n")
                    .append ("DECLARE @current_player_id INT;\n")
                    .append ("DECLARE @player_id INT;\n")
                    .append ("DECLARE @red BIT = 0;\n")
                    .append ("\n")
                    .append ("SET @event_id = ")
                    .append (event.getId ())
                    .append (";\n")
                    .append ("SET @event_item_id = ")
                    .append (event_item.getId ())
                    .append (";\n")
                    .append ("SET @league_team_id = ")
                    .append (selected_pitcher.getLeague_team ().getId ())
                    .append (";\n")
                    .append ("SET @league_team_player_id = ")
                    .append (selected_pitcher.getId ())
                    .append (";\n")
                    .append ("SET @event_item_league_team_player_override = ")
                    .append (override ? 1: 0)
                    .append (";\n")
                    .append ("\n")
                    .append ("SELECT @event_item_league_team_id = id FROM Event_Item_League_Team WHERE event_item_id = @event_item_id AND league_team_id = @league_team_id AND timestamp = (SELECT MAX(timestamp) FROM Event_Item_League_Team AS eilt1 WHERE eilt1.event_item_id = @event_item_id AND league_team_id = @league_team_id);\n")
                    .append ("\n")
                    .append ("SELECT @league_player_id = ltp.league_player_id\n")
                    .append ("FROM League_Team_Player as ltp\n")
                    .append ("WHERE ltp.id = @league_team_player_id\n")
                    .append ("\n")
                    .append ("SELECT @player_id = lp.player_id\n")
                    .append ("FROM League_Player as lp\n")
                    .append ("WHERE lp.id = @league_player_id\n")
                    .append ("\n")
                    .append ("SET @event_item_league_team_id = NULL;\n")
                    .append ("SELECT @event_item_league_team_id = id FROM Event_Item_League_Team WHERE event_item_id = @event_item_id AND league_team_id = @league_team_id AND timestamp = (SELECT MAX(timestamp) FROM Event_Item_League_Team AS eilt1 WHERE eilt1.event_item_id = @event_item_id AND league_team_id = @league_team_id);\n")
                    .append ("IF @event_item_league_team_id IS NULL\n")
                    .append ("  BEGIN\n")
                    .append ("  INSERT INTO Event_Item_League_Team (event_item_id, league_team_id, timestamp)\n")
                    .append ("  VALUES (@event_item_id, @league_team_id, SYSDATETIMEOFFSET());\n")
                    .append ("  SET @event_item_league_team_id = SCOPE_IDENTITY();\n")
                    .append ("  UPDATE Event SET updated = SYSDATETIMEOFFSET() WHERE id = @event_id;\n")
                    .append ("  END\n")
                    .append ("SET @event_item_league_team_player_id = NULL;\n")
                    .append ("SET @current_player_id = NULL;\n")
                    .append ("SELECT @current_player_id = p.id\n")
                    .append ("  FROM Event_Item_League_Team_Player AS eiltp\n")
                    .append ("  JOIN League_Team_Player as ltp on ltp.id = eiltp.league_team_player_id\n")
                    .append ("  JOIN League_Player as lp on lp.id = ltp.league_player_id\n")
                    .append ("  JOIN Player as p on p.id = lp.player_id\n")
                    .append (" WHERE event_item_league_team_id = @event_item_league_team_id\n")
                    .append ("   AND league_team_player_id = @league_team_player_id\n")
                    .append ("   AND timestamp = (SELECT MAX(timestamp) FROM Event_Item_League_Team_Player AS eiltp1 WHERE eiltp1.event_item_league_team_id = @event_item_league_team_id AND eiltp1.league_position_id = 1);\n")
                    .append ("IF @current_player_id IS NULL OR @player_id != @current_player_id\n")
                    .append ("  BEGIN\n")
                    .append ("  INSERT INTO Event_Item_League_Team_Player (event_item_league_team_id, league_team_player_id, league_position_id, timestamp, override)\n")
                    .append ("  VALUES (@event_item_league_team_id, @league_team_player_id, 1, SYSDATETIMEOFFSET(), @event_item_league_team_player_override);\n")
                    .append ("  SET @event_item_league_team_player_id = SCOPE_IDENTITY();\n")
                    .append ("  IF @current_player_id IS NOT NULL\n")
                    .append ("    SET @red = 1;\n")
                    .append ("  END\n")
                    .append ("\n")
                    .append ("SELECT @event_item_league_team_player_id AS event_item_league_team_player_id, @red AS red;")
                    ;
                Debug.print (sql);

                saved = true;
//                send_hdf ();
                try
                    {
                    int event_item_league_team_player_id = 0;
                    boolean red = false;
                    ResultSet rs = Main.db.executeQuery (sql.toString ());
                    if (rs != null)
                        {
                        if (rs.next ())
                            {
                            event_item_league_team_player_id = rs.getInt ("event_item_league_team_player_id");
                            red = rs.getBoolean ("red");
                            Debug.print ("\n\n\nevent_item_league_team_player_id (" + event_item_league_team_player_id + ") red (" + red + ")\n\n");
                            }
                        else
                            Debug.print ("\n\n\nNo RS next - event_item_league_team_player_id (" + event_item_league_team_player_id + ") red (" + red + ")\n\n");
                        }
                    else
                        Debug.print ("\n\n\nRS IS NULL event_item_league_team_player_id (" + event_item_league_team_player_id + ") red (" + red + ")\n\n");

                    Event_Item_League_Team event_item_league_team = event_item.getEvent_item_league_team ();
                    if (event_item_league_team != null)
                        {
                        League_Team_Player new_league_team_player = League_Team_Player.getLeague_team_player_by_ID ().get (selected_pitcher.getId ());
                        if (new_league_team_player != null)
                            {
                            event_item_league_team.getLeague_team_players ().put (League_Position.MLB_PITCHER, new_league_team_player);
                            Main.send_schedule_changed_for_event (event);
                            }
                        }
                    }
                catch (Exception e)
                    {
                    Debug.print ("" + e);
                    e.printStackTrace ();
                    }
                }
            setVisible (false);
            }
        }
    //---------------------------------------------------------------------------------------------
    private void cancel ()
        {
        saved = false;
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
    public void send_hdf ()
        {
        String data_text = "";
        data_text += "SET_PITCHER>>>\n";
        data_text += "[pitchers{updated,pitcher{event_id,event_item_id,league_team_id,league_team_player_id}}]\n";
        data_text += "{";
        data_text += DateTimeUtils.get_current_pacific_time();
        data_text += "{";
        data_text += this.event.getId () + ",";
        data_text += this.event_item.getId() + ",";
        data_text += this.event_item.getEvent_item_league_team().getLeague_team ().getId () + ",";
        data_text += (selected_pitcher == null ? "" : selected_pitcher.getId ());
        data_text += "}";
        data_text += "}\n";
        data_text += "<<<";
        Main.schedule_client.send (data_text);
        System.out.println (data_text);
        }
//---------------------------------------------------------------------------------------------
    public void remove_pitcher ()
        {
        original_pitcher = null;
        selected_pitcher = null;
        pitchers_List.clearSelection ();
        Event_Item_League_Team event_item_league_team = event_item.getEvent_item_league_team ();
        event_item_league_team.getLeague_team_players ().clear ();
        StringBuilder sql = new StringBuilder ();
        sql.append ("DELETE FROM Event_Item_League_Team_Player WHERE event_item_league_team_id = ")
           .append (event_item_league_team.getId ())
           .append (";\n")
           ;
        Debug.print (sql);
        Main.db.executeUpdate (sql.toString ());
        Main.send_schedule_changed_for_event (event);
        saved = true;
        setVisible (false);
        }
    //---------------------------------------------------------------------------------------------
    public League_Team_Player getSelected_pitcher ()
        {
        return selected_pitcher;
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

        pitchers_ScrollPane = new javax.swing.JScrollPane();
        pitchers_List = new javax.swing.JList<>();
        save_Button = new javax.swing.JButton();
        select_pitcher_Label = new javax.swing.JLabel();
        remove_pitcher_Button = new javax.swing.JButton();
        override_CheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pitchers_List.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        pitchers_ScrollPane.setViewportView(pitchers_List);

        save_Button.setText("Save");
        save_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                save_ButtonActionPerformed(evt);
            }
        });

        select_pitcher_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        select_pitcher_Label.setText("Select Pitcher");
        select_pitcher_Label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        remove_pitcher_Button.setText("Remove Pitcher");
        remove_pitcher_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                remove_pitcher_ButtonActionPerformed(evt);
            }
        });

        override_CheckBox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        override_CheckBox.setText("Override");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pitchers_ScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(remove_pitcher_Button)
                        .addGap(18, 18, 18)
                        .addComponent(override_CheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(save_Button))
                    .addComponent(select_pitcher_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(select_pitcher_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pitchers_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(save_Button)
                    .addComponent(remove_pitcher_Button)
                    .addComponent(override_CheckBox))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_save_ButtonActionPerformed
    {//GEN-HEADEREND:event_save_ButtonActionPerformed
        save ();
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void remove_pitcher_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_remove_pitcher_ButtonActionPerformed
    {//GEN-HEADEREND:event_remove_pitcher_ButtonActionPerformed
        remove_pitcher ();
    }//GEN-LAST:event_remove_pitcher_ButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox override_CheckBox;
    private javax.swing.JList<String> pitchers_List;
    private javax.swing.JScrollPane pitchers_ScrollPane;
    private javax.swing.JButton remove_pitcher_Button;
    private javax.swing.JButton save_Button;
    private javax.swing.JLabel select_pitcher_Label;
    // End of variables declaration//GEN-END:variables

    }
