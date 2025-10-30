/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool;

import gsutils.Debug;
import gsutils.data.Event;
import gsutils.data.Event_Item;
import gsutils.data.Event_Item_League_Team;
import gsutils.data.League;
import gsutils.data.League_Team;
import java.util.TreeMap;
import java.sql.ResultSet;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;


public class TeamsDialog extends javax.swing.JDialog
    {
    private       int league_team_id;
    private       int league_temp_id;
    private final TreeMap <String, Integer> teams = new TreeMap <> ();
    private final TreeMap <String, Integer> temps = new TreeMap <> ();
    private boolean   saved;
    private boolean   remove;

    //for editing teams
    private Event_Item  event_item;
    private League_Team selected_team;
    private League_Team selected_temp;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public TeamsDialog (java.awt.Frame parent, boolean modal, int league_id, boolean for_event)
        {
        super (parent, modal);
        initComponents ();
        initialize (league_id);
        }
    //---------------------------------------------------------------------------------------------
    public TeamsDialog (java.awt.Frame parent, boolean modal, int league_id, Event_Item event_item, League_Team selected_team, League_Team selected_temp, boolean for_event)
        {
        super (parent, modal);

        this.event_item    = event_item;
        this.selected_team = selected_team;
        this.selected_temp = selected_temp;
        initComponents ();
        initialize (league_id);

        if (event_item.getEvent_item_league_team () != null)
            override_CheckBox.setSelected (event_item.getEvent_item_league_team ().isOverride ());
        }

    //---------------------------------------------------------------------------------------------
    public void initialize_public (int league_id)
        {
        initialize (league_id);
        }
    //---------------------------------------------------------------------------------------------
    private void initialize (int league_id)
        {
        league_team_id = 0;
        league_temp_id = 0;
        setTitle ("Teams for " + League.get_league (league_id).getName ());
        DefaultListModel teams_model = new DefaultListModel ();
        DefaultListModel temps_model = new DefaultListModel ();
        teams.clear ();
        temps.clear ();
        League league = League.get_league (league_id);
        while (league_id != league.getMain_league_id ())
            {
            league_id = league.getMain_league_id ();
            league = League.get_league (league_id);
            }

        for (League_Team league_team : League_Team.getLeague_team_by_ID ().values ())
            {
            if (league_team.isActive ())
                {
                int team_league_id = league_team.getLeague ().getId ();
                League team_league = League.get_league (team_league_id);
                team_league_id = team_league.getMain_league_id ();
                if (team_league_id == league_id)
                    {
                    if (league_team.getTemp_name () == null)
                        teams.put (league_team.getName (), league_team.getId ());
                    else
                        temps.put (league_team.getName (), league_team.getId ());
                    }
                }
            }
        //System.out.println ("TEAMS SIZE: " + teams.size());
        int index = -1;
        int i = 0;
        for (String name : teams.keySet ())
            {
            if (selected_team != null && name.equals (selected_team.getName ()))
                index = i;
            teams_model.addElement (name);
            i++;
            }
        teams_List.setModel (teams_model);

        if (index >=0)
            {
            teams_List.ensureIndexIsVisible (index);
            teams_List.setSelectedIndex (index);
            }

        index = -1;
        i = 0;
        for (String name : temps.keySet ())
            {
            if (selected_temp != null && name.equals (selected_temp.getTemp_name ()))
                index = i;
            temps_model.addElement (name);
            i++;
            }
        temp_List.setModel (temps_model);
        if (index >=0)
            {
            temp_List.ensureIndexIsVisible (index);
            temp_List.setSelectedIndex (index);
            }
        }
    //---------------------------------------------------------------------------------------------
    public void select_team ()
        {
        String team_name = (String) teams_List.getSelectedValue ();
        if (team_name == null)
            {
            JOptionPane.showMessageDialog (this, "Please select a team");
            }
        else
            {
            league_team_id = teams.get (team_name);
            saved = true;
            Debug.print ("team (" + league_team_id + ")(" + team_name + ")");
            setVisible (false);
            }
        }
    //---------------------------------------------------------------------------------------------
    public void select_temp ()
        {
        String temp_name = (String) temp_List.getSelectedValue ();
        if (temp_name == null)
            {
            JOptionPane.showMessageDialog (this, "Please select a temp");
            }
        else
            {
            league_temp_id = temps.get (temp_name);
            saved = true;
            Debug.print ("temp (" + league_temp_id + ")(" + temp_name + ")");
            setVisible (false);
            }
        }
    //---------------------------------------------------------------------------------------------
    public void save ()
        {
        //System.out.println ("IN SAVE CHANGES FUCNTION");
        if (teams_List.getSelectedIndex() >= 0)
            {
            String team_name = (String) teams_List.getSelectedValue ();

            int override = 0;
            if (override_CheckBox.isSelected ())
                override = 1;

            int new_league_team_id = teams.get (team_name);
            if (new_league_team_id != league_team_id)
                {
                league_team_id = new_league_team_id;
                StringBuilder sql = new StringBuilder ()
                    .append ("DECLARE @event_item_id INT;\n")
                    .append ("DECLARE @league_team_id INT;\n")
                    .append ("DECLARE @old_league_team_id INT;\n")
                    .append ("DECLARE @event_item_league_team_id INT;\n")
                    .append ("DECLARE @override INT;\n")
                    .append ("SET @event_item_id = ")
                    .append (event_item.getId ())
                    .append (";\n")
                    .append ("SET @league_team_id = ")
                    .append (league_team_id)
                    .append (";\n")
                    .append ("SET @override = ")
                    .append (override)
                    .append (";\n")
                    .append ("SET @old_league_team_id = NULL;\n")
                    .append ("SELECT TOP 1 @old_league_team_id = league_team_id FROM Event_Item_League_Team WHERE event_item_id = @event_item_id ORDER BY timestamp DESC\n")
                    .append ("IF @league_team_id != @old_league_team_id\n")
                    .append ("BEGIN\n")
                    .append ("INSERT INTO Event_Item_League_Team (event_item_id, league_team_id, timestamp, override)\n")
                    .append ("VALUES (@event_item_id, @league_team_id, SYSDATETIMEOFFSET(), @override);\n")
                    .append ("END\n")
                    .append ("SET @event_item_league_team_id = SCOPE_IDENTITY();\n")
                    .append ("SELECT @event_item_league_team_id AS event_item_league_team_id;\n")
                    ;
                try
                    {
                    int event_item_league_team_id = 0;
                    ResultSet rs = Main.db.executeQuery (sql);
                    if (rs != null)
                        {
                        rs.next ();
                        event_item_league_team_id = rs.getInt ("event_item_league_team_id");
                        }
                    Event_Item_League_Team event_item_league_team = event_item.getEvent_item_league_team ();
                    if (event_item_league_team == null)
                        {
                        event_item_league_team = new Event_Item_League_Team ();
                        event_item_league_team.setId (event_item_league_team_id);
                        event_item_league_team.setEvent_item (event_item);
                        event_item.setEvent_item_league_team (event_item_league_team);
                        }
                    event_item_league_team.setLeague_team (League_Team.get_league_team_by_ID (league_team_id));
                    Main.send_schedule_changed_for_event (event_item.getEvent ());
                    }
                catch (Exception e)
                    {
                    Debug.print (":  " + e);
                    e.printStackTrace ();
                    }
                }
            }
        else
            {
            String temp_name = (String) temp_List.getSelectedValue ();
            int new_league_temp_id = temps.get (temp_name);
            if (new_league_temp_id != league_temp_id)
                {
                league_temp_id = new_league_temp_id;
                StringBuilder sql = new StringBuilder ()
                    .append ("DECLARE @event_item_id INT;\n")
                    .append ("DECLARE @league_temp_id INT;\n")
                    .append ("DECLARE @old_league_temp_id INT;\n")
                    .append ("SET @event_item_id = ")
                    .append (event_item.getId ())
                    .append (";\n")
                    .append ("SET @league_temp_id = ")
                    .append (league_temp_id)
                    .append (";\n")
                    .append ("SET @old_league_temp_id = NULL;\n")
                    .append ("SELECT TOP 1 @old_league_temp_id = league_team_id FROM Event_Item_League_Team WHERE event_item_id = @event_item_id ORDER BY timestamp DESC")
                    .append ("IF @league_temp_id != @old_league_temp_id\n")
                    .append ("BEGIN\n")
                    .append ("INSERT INTO Event_Item_League_Team (event_item_id, league_team_id, timestamp)\n")
                    .append ("VALUES (@event_item_id, @league_temp_id, SYSDATETIMEOFFSET());\n")
                    .append ("END\n")
                    ;
                Main.db.executeUpdate (sql);
                Event_Item_League_Team event_item_league_temp = event_item.getEvent_item_league_team ();
                if (event_item_league_temp == null)
                    {
                    event_item_league_temp = new Event_Item_League_Team ();
                    event_item_league_temp.setEvent_item (event_item);
                    event_item.setEvent_item_league_team (event_item_league_temp);
                    }
                event_item_league_temp.setLeague_team (League_Team.get_league_team_by_ID (league_temp_id));
                Main.send_schedule_changed_for_event (event_item.getEvent ());
                }
            //select_temp ();
            }
        saved = true;
        setVisible (false);
        }
    //---------------------------------------------------------------------------------------------
    public int getSelected_team ()
        {
        return league_team_id;
        }
    //---------------------------------------------------------------------------------------------
    public int getSelected_temp ()
        {
        return league_temp_id;
        }
    //---------------------------------------------------------------------------------------------
    public League_Team getSelected_team_object ()
        {
        return selected_team;
        }
//---------------------------------------------------------------------------------------------
    public League_Team getSelected_temp_object ()
        {
        return selected_temp;
        }
//---------------------------------------------------------------------------------------------
    public boolean isSaved ()
        {
        return saved;
        }
    //---------------------------------------------------------------------------------------------
    public boolean isRemoved ()
        {
        return remove;
        }
    //---------------------------------------------------------------------------------------------
    private void remove_team ()
        {
        remove = true;
        saved  = false;
        setVisible (false);
        }
    //---------------------------------------------------------------------------------------------
    private void remove_temp ()
        {
        remove = true;
        saved  = false;
        setVisible (false);
        }
    //---------------------------------------------------------------------------------------------
    public void team_selected ()
        {
        int index = teams_List.getSelectedIndex();
        if (temp_List.getSelectedIndex() >= 0)
            {
            temp_List.clearSelection();
            }
        if (index >= 0)
            teams_List.setSelectedIndex(index);
        }
    //---------------------------------------------------------------------------------------------
    public void temp_selected ()
        {
        int index = temp_List.getSelectedIndex();
        if (teams_List.getSelectedIndex() >= 0)
            {
            teams_List.clearSelection();
            }
        if (index >= 0)
            temp_List.setSelectedIndex(index);
        }
//---------------------------------------------------------------------------------------------
    public void send_hdf ()
        {
        }
    //---------------------------------------------------------------------------------------------
    public void set_item_team_and_temp (Event_Item event_item, League_Team selected_team, League_Team selected_temp)
        {
        this.event_item    = event_item;
        this.selected_team = selected_team;
        this.selected_temp = selected_temp;
        }
    //---------------------------------------------------------------------------------------------
    public void remove_team_from_database (Event selected_event, Event_Item selected_event_item)
        {
        Event_Item_League_Team event_item_league_team = selected_event_item.getEvent_item_league_team ();
        if (event_item_league_team == null)
            {
            Event_Item_League_Team event_item_league_temp = selected_event_item.getEvent_item_league_team ();
            if (event_item_league_temp != null)
                {
                String team_name = event_item_league_temp.getLeague_team ().getTemp_name ();
                String sql = "DELETE FROM Event_Item_League_Team WHERE event_item_id = " + selected_event_item.getId ();
                Main.db.executeUpdate (sql);
                selected_event_item.setEvent_item_league_team (null);
                Main.send_schedule_changed_for_event (selected_event);
                JOptionPane.showMessageDialog (this, "Team (" + team_name + ") removed!");
                }
            }
        else
            {
            String team_name = event_item_league_team.getLeague_team ().getName ();
            String sql =   "DELETE FROM Event_Item_League_Team_Player WHERE event_item_league_team_id IN (SELECT id FROM Event_Item_League_Team WHERE event_item_id = " + selected_event_item.getId () + ");"
                       + "\nDELETE FROM Event_Item_League_Team WHERE event_item_id = " + selected_event_item.getId ()
                       ;
            Main.db.executeUpdate (sql);
            selected_event_item.setEvent_item_league_team (null);
            Main.send_schedule_changed_for_event (selected_event);
            JOptionPane.showMessageDialog (this, "Team (" + team_name + ") removed!");
            }
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

        jScrollPane1 = new javax.swing.JScrollPane();
        teams_List = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        temp_List = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        remove_team_Button = new javax.swing.JButton();
        remove_temp_Button = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        save_Button = new javax.swing.JButton();
        override_CheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        teams_List.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                teams_ListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(teams_List);

        temp_List.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                temp_ListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(temp_List);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Teams:");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Temps:");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        remove_team_Button.setText("Remove team");
        remove_team_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                remove_team_ButtonActionPerformed(evt);
            }
        });

        remove_temp_Button.setText("Remove temp");
        remove_temp_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                remove_temp_ButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Select either a team or temp name");

        save_Button.setText("Save");
        save_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                save_ButtonActionPerformed(evt);
            }
        });

        override_CheckBox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        override_CheckBox.setText("Override");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(remove_team_Button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(override_CheckBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(remove_temp_Button)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(save_Button)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(161, 161, 161)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(remove_team_Button)
                    .addComponent(remove_temp_Button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(save_Button)
                    .addComponent(override_CheckBox))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void remove_team_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_remove_team_ButtonActionPerformed
    {//GEN-HEADEREND:event_remove_team_ButtonActionPerformed
        remove_team ();
    }//GEN-LAST:event_remove_team_ButtonActionPerformed

    private void remove_temp_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_remove_temp_ButtonActionPerformed
    {//GEN-HEADEREND:event_remove_temp_ButtonActionPerformed
        remove_temp ();
    }//GEN-LAST:event_remove_temp_ButtonActionPerformed

    private void teams_ListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_teams_ListValueChanged
        // TODO add your handling code here:
        team_selected ();
    }//GEN-LAST:event_teams_ListValueChanged

    private void temp_ListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_temp_ListValueChanged
        // TODO add your handling code here:
        temp_selected ();
    }//GEN-LAST:event_temp_ListValueChanged

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_ButtonActionPerformed
        // TODO add your handling code here:
        save ();
    }//GEN-LAST:event_save_ButtonActionPerformed

//    public static void main (int league_id)
//        {
//        java.awt.EventQueue.invokeLater (() ->
//            {
//            if (Main.teams_dialog == null)
//                Main.teams_dialog = new TeamsDialog (new javax.swing.JFrame (), true);
//            else
//                Main.teams_dialog.league_id = league_id;
//
//            Main.teams_dialog.addWindowListener (new java.awt.event.WindowAdapter ()
//                {
//                @Override
//                public void windowClosing (java.awt.event.WindowEvent e)
//                    {
//                    Main.teams_dialog.dispose ();
//                    }
//                    });
//            Main.teams_dialog.initialize ();
//            Main.teams_dialog.setVisible (true);
//            });
//        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox override_CheckBox;
    private javax.swing.JButton remove_team_Button;
    private javax.swing.JButton remove_temp_Button;
    private javax.swing.JButton save_Button;
    private javax.swing.JList<String> teams_List;
    private javax.swing.JList<String> temp_List;
    // End of variables declaration//GEN-END:variables
    }
