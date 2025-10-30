/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool;

import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.data.Event_Item_League_Team;
import gsutils.data.Category;
import gsutils.data.Event;
import gsutils.data.Event_Key;
import gsutils.data.Event_Time;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import scheduletool.datetime.DateTimeDialog;

public class EventTimesDialog extends javax.swing.JDialog
    {
    private final TreeMap <Integer, Event> selected_events_map = new TreeMap <> ();
    //---------------------------------------------------------------------------------------------
    private Category selected_category;
    private ArrayList <Event> events_all;
    private OffsetDateTime offset_date_time = null;
    //---------------------------------------------------------------------------------------------
    private boolean saved = false;
    //----------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------
    public EventTimesDialog (java.awt.Frame parent, boolean modal)
        {
        super (parent, modal);
        initComponents ();
        }
    //----------------------------------------------------------------------------------------------------------------------------------
    public void initialize_fields (Category category)
        {
        this.selected_category = category;

        initialize_events_table ();

        selected_date_time_TextField.setText ("");

         // Set column widths
        TableColumnModel columnModel = events_Table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth (175); // Widen the "Date" column
        columnModel.getColumn(1).setPreferredWidth ( 75);
        columnModel.getColumn(2).setPreferredWidth (100);
        }
    //----------------------------------------------------------------------------------------------------------------------------------
    public void initialize_events_table ()
        {
        String[] columnNames = {"Date/Time", "Event Number", "Away Team", "Home Team"};
//        DefaultTableModel events_table_model = new DefaultTableModel(columnNames, 0);
        DefaultTableModel events_table_model = (DefaultTableModel) events_Table.getModel ();
        events_table_model.setRowCount (0);
        events_table_model.setColumnIdentifiers (columnNames);

        TreeMap <Event_Key, Event> events = selected_category.getEvents ();
        events_all = new ArrayList <> ();

        int i = 0;
        for (Event event : events.values ())
            {
            events_all.add (event);

            Event_Time event_time = event.getEvent_time ();
            String date_time;
            if (event_time == null || event_time.isTBA ())
                date_time = "TBA";
            else
                date_time = DateTimeUtils.get_pacific_date_time_string_for_viewing (event_time.getTime ());
                //date_time = DateTimeUtils.get_date_time_string_with_zone (event_time.getTime (), DateTimeUtils.pacific_zone_ID);
            int event_number = event.getNumber ();
            Event_Item_League_Team away_event_item_league_team = event.getEvent_items ().get (0).getEvent_item_league_team ();
            Event_Item_League_Team home_event_item_league_team = event.getEvent_items ().get (1).getEvent_item_league_team ();
            String away_team = (away_event_item_league_team == null ? "" : away_event_item_league_team.getLeague_team ().getName ());
            String home_team = (home_event_item_league_team == null ? "" : home_event_item_league_team.getLeague_team ().getName ());

            Object[] row = {date_time, event_number, away_team, home_team};
            events_table_model.addRow(row);

            i++;
            }

        events_Table.setModel (events_table_model);
        }
    //----------------------------------------------------------------------------------------------------------------------------------
    public void date_time_button_selected ()
        {
        int [] selected_rows = events_Table.getSelectedRows ();
        if (selected_rows.length == 0)
            JOptionPane.showMessageDialog (this, "Select an event to change its date/time");
        else
            {
            Event first_selected_event = events_all.get (selected_rows [0]);
            if (Main.date_and_time_dialog == null)
                Main.date_and_time_dialog = new DateTimeDialog (this, true, first_selected_event, true);
            else
                Main.date_and_time_dialog.initialize_public (first_selected_event);
            }

        Main.date_and_time_dialog.setVisible (true);
        if (Main.date_and_time_dialog.isSaved ())
            {
            //get time and set it for all selected events
            offset_date_time = Main.date_and_time_dialog.get_selected_offset_datetime ();
            //set textfield time
            selected_date_time_TextField.setText (getOffset_date_time ().toLocalDateTime ().toString ());
            }
        }
    //----------------------------------------------------------------------------------------------------------------------------------
    public void select_all_events ()
        {
        // Ensure that the eventsTable is not null and has rows
        if (events_Table != null && events_Table.getRowCount() > 0)
            {
            // Select all rows in the table
            events_Table.setRowSelectionInterval (0, events_Table.getRowCount() - 1);
            }
        else
            {
            // Handle the case when the table is empty or null
            Debug.print ("No events to select or table is not initialized.");
            }
        }
    //----------------------------------------------------------------------------------------------------------------------------------
    public void deselect_all_events ()
        {
        events_Table.setRowSelectionInterval (0, 0);
        }
    //----------------------------------------------------------------------------------------------------------------------------------
    public void save ()
        {
        saved = true;

        selected_events_map.clear ();
        int [] selected_rows = events_Table.getSelectedRows ();
        for (int i = 0; i < selected_rows.length; i++)
            {
            Event event = events_all.get (selected_rows [i]);
            selected_events_map.put (event.getId (), event);
            }

        setVisible (false);
        }
    //----------------------------------------------------------------------------------------------------------------------------------
    public boolean isSaved ()
        {
        return saved;
        }
    //----------------------------------------------------------------------------------------------------------------------------------
    public OffsetDateTime getOffset_date_time ()
        {
        return offset_date_time;
        }
    //----------------------------------------------------------------------------------------------------------------------------------
    public TreeMap <Integer, Event> getSelected_events_map ()
        {
        return selected_events_map;
        }
    //----------------------------------------------------------------------------------------------------------------------------------
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
        events_Table = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        select_all_Button = new javax.swing.JButton();
        deselect_all_Button = new javax.swing.JButton();
        selected_date_time_TextField = new javax.swing.JTextField();
        select_date_time_Button = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        save_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Event Times For Category");

        events_Table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        events_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {
                "Date/Time", "Event Number", "Team1", "Team2"
            }
        ));
        jScrollPane1.setViewportView(events_Table);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Events");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        select_all_Button.setText("Select All");
        select_all_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                select_all_ButtonActionPerformed(evt);
            }
        });

        deselect_all_Button.setText("Deselect All");
        deselect_all_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deselect_all_ButtonActionPerformed(evt);
            }
        });

        selected_date_time_TextField.setText("Date/Time");

        select_date_time_Button.setText("Select Date/TIme");
        select_date_time_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                select_date_time_ButtonActionPerformed(evt);
            }
        });

        save_Button.setText("Save");
        save_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                save_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(select_all_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(deselect_all_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSeparator2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(save_Button, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(selected_date_time_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(select_date_time_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(76, 76, 76)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(select_all_Button)
                    .addComponent(deselect_all_Button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(select_date_time_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selected_date_time_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(save_Button)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void select_date_time_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_select_date_time_ButtonActionPerformed
    {//GEN-HEADEREND:event_select_date_time_ButtonActionPerformed
        date_time_button_selected ();
    }//GEN-LAST:event_select_date_time_ButtonActionPerformed

    private void deselect_all_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deselect_all_ButtonActionPerformed
    {//GEN-HEADEREND:event_deselect_all_ButtonActionPerformed
        deselect_all_events ();
    }//GEN-LAST:event_deselect_all_ButtonActionPerformed

    private void select_all_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_select_all_ButtonActionPerformed
    {//GEN-HEADEREND:event_select_all_ButtonActionPerformed
        select_all_events ();
    }//GEN-LAST:event_select_all_ButtonActionPerformed

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_save_ButtonActionPerformed
    {//GEN-HEADEREND:event_save_ButtonActionPerformed
        save ();
    }//GEN-LAST:event_save_ButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main (String args[])
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
            java.util.logging.Logger.getLogger (EventTimesDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (InstantiationException ex)
            {
            java.util.logging.Logger.getLogger (EventTimesDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (IllegalAccessException ex)
            {
            java.util.logging.Logger.getLogger (EventTimesDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
            {
            java.util.logging.Logger.getLogger (EventTimesDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater (() ->
            {
            EventTimesDialog dialog = new EventTimesDialog (new javax.swing.JFrame (), true);
            dialog.addWindowListener (new java.awt.event.WindowAdapter ()
                {
                @Override
                public void windowClosing (java.awt.event.WindowEvent e)
                    {
                    System.exit (0);
                    }
                    });
            dialog.setVisible (true);
            });
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deselect_all_Button;
    private javax.swing.JTable events_Table;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton save_Button;
    private javax.swing.JButton select_all_Button;
    private javax.swing.JButton select_date_time_Button;
    private javax.swing.JTextField selected_date_time_TextField;
    // End of variables declaration//GEN-END:variables
    }
