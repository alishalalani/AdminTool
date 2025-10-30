/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool;

import gsutils.Debug;
import gsutils.data.League;
import gsutils.data.League_Prop;
import gsutils.data.League_Team;
import java.util.TreeMap;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author samla
 */
public class TeamsDialogCopy extends javax.swing.JDialog
    {
    private       int league_team_id;
    private       int league_prop_id;
    private final TreeMap <String, Integer> teams = new TreeMap <> ();
    private final TreeMap <String, Integer> props = new TreeMap <> ();
    private boolean   saved;
    private boolean   remove;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public TeamsDialogCopy (java.awt.Frame parent, boolean modal, int league_id)
        {
        super (parent, modal);
        initComponents ();
        initialize (league_id);
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
        league_prop_id = 0;
        setTitle ("Teams for " + League.get_league (league_id).getName ());
        DefaultListModel model = new DefaultListModel ();
        teams.clear ();
        League league = League.get_league (league_id);
        while (league_id != league.getMain_league_id ())
            {
            league_id = league.getMain_league_id ();
            league = League.get_league (league_id);
            }
        for (League_Team league_team : League_Team.getLeague_team_by_ID ().values ())
            {
            if (league_team.getLeague ().getId () == league_id)
                teams.put (league_team.getName (), league_team.getId ());
            }
        for (String name : teams.keySet ())
            model.addElement (name);
        teams_List.setModel (model);

        model = new DefaultListModel ();
        props.clear ();
        for (League_Prop league_prop : League_Prop.getLeague_prop_by_ID ().values ())
            {
            if (league_prop.getLeague ().getId () == league_id)
                props.put (league_prop.getProp (), league_prop.getId ());
            }
        for (String name : props.keySet ())
            model.addElement (name);
        props_List.setModel (model);
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
    public void select_prop ()
        {
        String prop_name = (String) props_List.getSelectedValue ();
        if (prop_name == null)
            {
            JOptionPane.showMessageDialog (this, "Please select a prop");
            }
        else
            {
            league_prop_id = props.get (prop_name);
            saved = true;
            Debug.print ("prop (" + league_prop_id + ")(" + prop_name + ")");
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
    public int getSelected_team ()
        {
        return league_team_id;
        }
    //---------------------------------------------------------------------------------------------
    public int getSelected_prop ()
        {
        return league_prop_id;
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
    private void remove_prop ()
        {
        remove = true;
        saved  = false;
        setVisible (false);
        }
    //---------------------------------------------------------------------------------------------
    public void team_selected ()
        {
        System.out.println ("TEAM SELECTED !!!!!!!!!!!!!!!!");
        if (props_List.getSelectedIndex() >= 0)
            {
            props_List.clearSelection();
            }
        }
    //---------------------------------------------------------------------------------------------
    public void prop_selected ()
        {
        if (teams_List.getSelectedIndex() >= 0)
            {
            teams_List.clearSelection();
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
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        teams_List = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        props_List = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        select_team_Button = new javax.swing.JButton();
        select_prop_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        remove_team_Button = new javax.swing.JButton();
        remove_prop_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        teams_List.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                teams_ListPropertyChange(evt);
            }
        });
        teams_List.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                teams_ListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(teams_List);

        props_List.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                props_ListPropertyChange(evt);
            }
        });
        props_List.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                props_ListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(props_List);

        jLabel1.setText("Teams:");

        jLabel2.setText("Props:");

        select_team_Button.setText("Select team");
        select_team_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                select_team_ButtonActionPerformed(evt);
            }
        });

        select_prop_Button.setText("Select prop");
        select_prop_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                select_prop_ButtonActionPerformed(evt);
            }
        });

        cancel_Button.setText("Cancel");
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });

        remove_team_Button.setText("Remove team");
        remove_team_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remove_team_ButtonActionPerformed(evt);
            }
        });

        remove_prop_Button.setText("Remove prop");
        remove_prop_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remove_prop_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(select_team_Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(remove_team_Button)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(select_prop_Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(remove_prop_Button))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cancel_Button))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jScrollPane1, jScrollPane2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(cancel_Button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(select_team_Button)
                    .addComponent(select_prop_Button)
                    .addComponent(remove_team_Button)
                    .addComponent(remove_prop_Button))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void select_team_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_select_team_ButtonActionPerformed
    {//GEN-HEADEREND:event_select_team_ButtonActionPerformed
        select_team ();
    }//GEN-LAST:event_select_team_ButtonActionPerformed

    private void select_prop_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_select_prop_ButtonActionPerformed
    {//GEN-HEADEREND:event_select_prop_ButtonActionPerformed
        select_prop ();
    }//GEN-LAST:event_select_prop_ButtonActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancel_ButtonActionPerformed
    {//GEN-HEADEREND:event_cancel_ButtonActionPerformed
        cancel ();
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void remove_team_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_remove_team_ButtonActionPerformed
    {//GEN-HEADEREND:event_remove_team_ButtonActionPerformed
        remove_team ();
    }//GEN-LAST:event_remove_team_ButtonActionPerformed

    private void remove_prop_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_remove_prop_ButtonActionPerformed
    {//GEN-HEADEREND:event_remove_prop_ButtonActionPerformed
        remove_prop ();
    }//GEN-LAST:event_remove_prop_ButtonActionPerformed

    private void teams_ListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_teams_ListValueChanged
        // TODO add your handling code here:
      //  team_selected ();
    }//GEN-LAST:event_teams_ListValueChanged

    private void props_ListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_props_ListValueChanged
        // TODO add your handling code here:
      //  prop_selected ();
    }//GEN-LAST:event_props_ListValueChanged

    private void teams_ListPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_teams_ListPropertyChange
        // TODO add your handling code here:
        team_selected ();
    }//GEN-LAST:event_teams_ListPropertyChange

    private void props_ListPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_props_ListPropertyChange
        // TODO add your handling code here:
        prop_selected ();
    }//GEN-LAST:event_props_ListPropertyChange

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
    private javax.swing.JButton cancel_Button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> props_List;
    private javax.swing.JButton remove_prop_Button;
    private javax.swing.JButton remove_team_Button;
    private javax.swing.JButton select_prop_Button;
    private javax.swing.JButton select_team_Button;
    private javax.swing.JList<String> teams_List;
    // End of variables declaration//GEN-END:variables
    }
