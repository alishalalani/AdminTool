/*
 * Ctrl-click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Ctrl-click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool.restart_programs;

import gsutils.Debug;
import gsutils.data.Sport;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.util.TreeSet;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import scheduletool.Main;
import scheduletool.ProgramName;

/**
 *
 * @author samla
 */
public class RestartPrograms extends javax.swing.JDialog
    {
    private final int buttonWidth = 75;
    private final int labelWidth = 125;
    private final int spacing = 10;
    //---------------------------------------------------------------------------------------------
    private JPanel panel;
    private JButton[] buttons;
    private JLabel[] labels;
    //---------------------------------------------------------------------------------------------
    Sport              selected_sport       = null;
    RestartLeague      selected_league      = null;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public RestartPrograms (java.awt.Frame parent, boolean modal)
        {
        super (parent, modal);
        setTitle ("Restart programs");
        initComponents ();
        refresh_start_times ();
        create_buttons ();
        initialize_sports_combobox ();
        setSize (750, 750);
        start_league_only_CheckBox.setSelected (true);
        //create_league_list ();
        }
    //---------------------------------------------------------------------------------------------
    private void restart_programs (String process)
        {
        Debug.print ("Selected sport (" + selected_sport + ")");
        Debug.print ("Selected league (" + selected_league + ")");
        if (start_league_only_CheckBox.isSelected ())
            {
            if (selected_league == null)
                JOptionPane.showMessageDialog (this, "You must select a league");
            else
                {
                Debug.print ("Selected league (" + selected_league.league_value () + ")");
                Main.schedule_client.send ("RESTART:" + ProgramName.map.get (process).internal_name + ":" + selected_league.getId () + "\n");
                }
            }
        else
            Main.schedule_client.send ("RESTART:" + ProgramName.map.get (process).internal_name + "\n");

        start_league_only_CheckBox.setSelected (true);
        }
    //---------------------------------------------------------------------------------------------
    private void refresh_start_times ()
        {
        String sql;
        sql = """
              SELECT g1.name1, g1.value as name, g2.value as start_time
                FROM GlobalValues as g1
                JOIN Globalvalues as g2 on g2.name1 = g1.name1 and g2.name2 = 'start_time'
               WHERE g1.name2='name'
              """;
        ProgramName.map.clear ();
        try
            {
            ResultSet rs = Main.db.executeQuery (sql);
            if (rs != null)
                {
                while (rs.next ())
                    {
                    String internal_name = rs.getString ("name1");
                    String viewing_name  = rs.getString ("name");
                    String start_time    = rs.getString ("start_time");
                    ProgramName.map.put (viewing_name, new ProgramName (viewing_name, internal_name, start_time));
                    }
                }
            }
        catch (Exception e)
            {
            Debug.print ("Exception (" + e + ")");
            }
        }
    //---------------------------------------------------------------------------------------------
    private void create_buttons ()
        {
        GridBagConstraints gbc = new GridBagConstraints ();
        gbc.insets = new Insets (3, 3, 3, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel = buttons_Panel;
        panel.setBorder (new LineBorder (Color.BLACK));
        panel.setLayout (new GridBagLayout ());
        for (ProgramName entry : ProgramName.map.values ())
            {
            JButton button = new JButton (entry.viewing_name);
            button.setPreferredSize (new Dimension (150, 23));
            button.setMinimumSize   (new Dimension (150, 23));
            button.setMaximumSize   (new Dimension (150, 23));
            button.addActionListener ((ActionEvent e) ->
                {
                Debug.print ("Restarting (" + e.getActionCommand () + ")");
                restart_programs (e.getActionCommand ());
                });
            JLabel label = new JLabel (entry.start_time);
            label.setPreferredSize (new Dimension (125, 23));
            gbc.gridy++;
            panel.add (button, gbc);
            gbc.gridx++;
            panel.add (label, gbc);
            gbc.gridx = 0;
            }
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        JButton close_button = new JButton ("Close");
        close_button.addActionListener ((ActionEvent e) ->
            {
            dispose ();
            });
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        add (close_button, gbc);
        //add_sport_combo_box ();
        //pack ();
        }
    //---------------------------------------------------------------------------------------------
    private void initialize_sports_combobox ()
        {
        sport_ComboBox.removeAllItems ();
        for (Sport sport : Sport.getSports ().values ())
            sport_ComboBox.addItem (sport.getName ());
        }
    //---------------------------------------------------------------------------------------------
    private void sport_changed ()
        {
        String sport_name = (String) sport_ComboBox.getSelectedItem ();
        Sport sport;
        if (sport_name == null)
            sport = Sport.getSports_by_name ().firstEntry ().getValue ();
        else
            sport = Sport.getSports_by_name ().get (sport_name);
        //if (selected_sport != null && selected_sport.getId () == sport.getId ())
        //    return;
        selected_sport = sport;
        Debug.print ("Sport selected (" + sport.getName () + ")");
        TreeSet <RestartLeague> leagues = new TreeSet <> ();
        for (gsutils.data.League league : selected_sport.getLeagues ().values ())
            leagues.add (new RestartLeague (league));

        DefaultListModel <RestartLeague> league_model = new DefaultListModel <> ();
        league_model.clear ();
        //System.out.println (function + ":  before for");
        for (RestartLeague league : leagues)
            league_model.addElement (league);
        leagues_List.setModel (league_model);

        selected_league = null;
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
        java.awt.GridBagConstraints gridBagConstraints;

        buttons_Panel = new javax.swing.JPanel();
        sport_Panel = new javax.swing.JPanel();
        sport_Label = new javax.swing.JLabel();
        sport_ComboBox = new javax.swing.JComboBox<>();
        league_Label = new javax.swing.JLabel();
        league_ScrollPane = new javax.swing.JScrollPane();
        leagues_List = new javax.swing.JList();
        close_Button = new javax.swing.JButton();
        refresh_Button = new javax.swing.JButton();
        start_league_only_CheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Restart programs");
        addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                formComponentResized(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        buttons_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        buttons_Panel.setPreferredSize(new java.awt.Dimension(200, 200));

        javax.swing.GroupLayout buttons_PanelLayout = new javax.swing.GroupLayout(buttons_Panel);
        buttons_Panel.setLayout(buttons_PanelLayout);
        buttons_PanelLayout.setHorizontalGroup(
            buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        buttons_PanelLayout.setVerticalGroup(
            buttons_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 589, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(buttons_Panel, gridBagConstraints);

        sport_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sport_Panel.setPreferredSize(new java.awt.Dimension(200, 200));

        sport_Label.setText("Sport:");

        sport_ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                sport_ComboBoxActionPerformed(evt);
            }
        });

        league_Label.setText("League:");

        leagues_List.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                leagues_ListValueChanged(evt);
            }
        });
        league_ScrollPane.setViewportView(leagues_List);

        close_Button.setText("Close");
        close_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                close_ButtonActionPerformed(evt);
            }
        });

        refresh_Button.setText("Refresh");
        refresh_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                refresh_ButtonActionPerformed(evt);
            }
        });

        start_league_only_CheckBox.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        start_league_only_CheckBox.setText("Start league only");

        javax.swing.GroupLayout sport_PanelLayout = new javax.swing.GroupLayout(sport_Panel);
        sport_Panel.setLayout(sport_PanelLayout);
        sport_PanelLayout.setHorizontalGroup(
            sport_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sport_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sport_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(league_ScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                    .addGroup(sport_PanelLayout.createSequentialGroup()
                        .addGroup(sport_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(sport_PanelLayout.createSequentialGroup()
                                .addComponent(sport_Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sport_ComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(sport_PanelLayout.createSequentialGroup()
                                .addComponent(league_Label)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sport_PanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(refresh_Button))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sport_PanelLayout.createSequentialGroup()
                                .addComponent(start_league_only_CheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(close_Button)))
                        .addContainerGap())))
        );
        sport_PanelLayout.setVerticalGroup(
            sport_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sport_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sport_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sport_Label)
                    .addComponent(sport_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(league_Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(league_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sport_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(close_Button)
                    .addComponent(start_league_only_CheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refresh_Button)
                .addContainerGap(185, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(sport_Panel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void close_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_close_ButtonActionPerformed
    {//GEN-HEADEREND:event_close_ButtonActionPerformed
        setVisible (false);
    }//GEN-LAST:event_close_ButtonActionPerformed

    private void refresh_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_refresh_ButtonActionPerformed
    {//GEN-HEADEREND:event_refresh_ButtonActionPerformed
        refresh_start_times ();
    }//GEN-LAST:event_refresh_ButtonActionPerformed

    private void sport_ComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sport_ComboBoxActionPerformed
    {//GEN-HEADEREND:event_sport_ComboBoxActionPerformed
        sport_changed ();
    }//GEN-LAST:event_sport_ComboBoxActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentResized
    {//GEN-HEADEREND:event_formComponentResized
        Debug.print ("width (" + getWidth () + ") height (" + getHeight () + ")");
    }//GEN-LAST:event_formComponentResized

    private void leagues_ListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_leagues_ListValueChanged
    {//GEN-HEADEREND:event_leagues_ListValueChanged
        selected_league = (RestartLeague) leagues_List.getSelectedValue ();
    }//GEN-LAST:event_leagues_ListValueChanged

    public static void main ()
        {
        java.awt.EventQueue.invokeLater (() ->
            {
            RestartPrograms dialog = new RestartPrograms (new javax.swing.JFrame (), true);
            dialog.addWindowListener (new java.awt.event.WindowAdapter ()
                {
                @Override
                public void windowClosing (java.awt.event.WindowEvent e)
                    {
                    dialog.dispose ();
                    }
                });
            dialog.setVisible (true);
            });
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttons_Panel;
    private javax.swing.JButton close_Button;
    private javax.swing.JLabel league_Label;
    private javax.swing.JScrollPane league_ScrollPane;
    private javax.swing.JList leagues_List;
    private javax.swing.JButton refresh_Button;
    private javax.swing.JComboBox<String> sport_ComboBox;
    private javax.swing.JLabel sport_Label;
    private javax.swing.JPanel sport_Panel;
    private javax.swing.JCheckBox start_league_only_CheckBox;
    // End of variables declaration//GEN-END:variables
    }
