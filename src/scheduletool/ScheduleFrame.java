/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduletool;

import scheduletool.restart_programs.RestartPrograms;
import scheduletool.table.ScheduleRecord;
import gsutils.DateTimeUtils;
import gsutils.HDF.ScheduleChanged;
import java.awt.Color;
import java.time.Instant;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ScheduleFrame extends javax.swing.JFrame
    {
    private boolean today                = false;
    private int     selected_schedule_id = 0;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public ScheduleFrame ()
        {
        initComponents ();
        setTitle (Main.name + " " + Main.version);
        try
            {
            Main.originalImage = ImageIO.read (getClass().getResourceAsStream ("/scheduletool/resources/calendar.jpg")); // Replace "your_image_path.jpg" with the actual path to your image
            }
        catch (Exception e)
            {
            e.printStackTrace ();
            Main.originalImage = null;
            }
        }
    //---------------------------------------------------------------------------------------------
    public void initialize ()
        {
        if (Main.development)
            {
            database_Label.setText ("development");
            database_Label.setForeground (Color.green);
            }
        else
            {
            database_Label.setText ("PRODUCTION");
            database_Label.setForeground (Color.red);
            }
        ScheduleRecord.read_table ();
        Object columnNames[] = {"ID", "Day", "Date"};
        DefaultTableModel model = new DefaultTableModel (columnNames, 0);
        for (ScheduleRecord record : ScheduleRecord.records.values ())
            {
            String display_string = DateTimeUtils.get_date_display_string (record.getDate ());
            String day = display_string.substring (0, display_string.indexOf (','));
            String date = record.getDate ().toString ();
            Object rowData[] = {record.getId (), day, date};
            model.addRow (rowData);
            }
        schedule_Table.setModel(model);
        schedule_Table.setRowSelectionInterval (0, 0);
        }
    //---------------------------------------------------------------------------------------------
    public int getSelected_schedule_id ()
        {
        return (selected_schedule_id);
        }
    //---------------------------------------------------------------------------------------------
    public boolean isToday ()
        {
        return (today);
        }
    //---------------------------------------------------------------------------------------------
    private void categories ()
        {
        int row = schedule_Table.getSelectedRow ();
        if (row < 0)
            JOptionPane.showMessageDialog (this, "Please select a row for which you want Categories");
        else
            {
            today = (row == 0);
            selected_schedule_id = (Integer) schedule_Table.getValueAt (row, 0);
            System.out.println ("selected_schedule_id " + selected_schedule_id);
            if (Main.categories_frame == null)
                {
                CategoriesFrame.main ();
//                while (!CategoriesFrame.initialized)
//                    {
//                    SleepFrame.sleep (250);
//                    }
//                Main.categories_frame.initialize (Main.schedule_frame.getSelected_schedule_id ());
                }
            else
                {
                Main.categories_frame.setVisible (true);
                Main.categories_frame.initialize (Main.schedule_frame.getSelected_schedule_id ());
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    public static void switch_servers ()
        {
        Main.development = !Main.development;
        Main.schedule_client.setDone (true);
        Main.restart ();
        }
    //---------------------------------------------------------------------------------------------
    private void force_schedule_change ()
        {
        Instant instant = Instant.now ();
        String sql = "INSERT INTO schedule_active (timestamp, schedule_id)\n"
                   + "SELECT SYSDATETIMEOFFSET(), id FROM Schedule WHERE date = (SELECT value FROM GlobalValues WHERE name1='schedule' AND name2='current');\n"
                   + "UPDATE GlobalValues SET value='1' WHERE name1='schedule' AND name2='force';\n"
                   + "UPDATE GlobalValues SET value='" + instant.getEpochSecond() + "' WHERE name1='schedule' AND name2='forced_time';"
                   ;
        Main.db.executeUpdate (sql);

        ScheduleChanged schedule_changed = ScheduleChanged.factory ();
        schedule_changed.set_from (Main.name);
        schedule_changed.set_forced (true);

        gsutils.HDF.create.ScheduleChanged schedule_changed_to_send = new gsutils.HDF.create.ScheduleChanged ();
        schedule_changed_to_send.setScheduleChanged (schedule_changed);
        schedule_changed_to_send.process ();

        String message = "SCHEDULE_CHANGED>>>\n" + schedule_changed_to_send.getHDF_data ().toString () + "\n<<<\n";
        //Debug.print (message);
        Main.schedule_client.send (message);
        }
    //---------------------------------------------------------------------------------------------
    private void restart_programs ()
        {
        RestartPrograms.main ();
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        schedule_Table = new javax.swing.JTable();
        categories_Button = new javax.swing.JButton();
        exit_Button = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        database_Label = new javax.swing.JLabel();
        force_schedule_change_Button = new javax.swing.JButton();
        restart_programs_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Schedule");

        jLabel1.setText("Schedule date:");

        schedule_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String []
            {
                "Day", "Date"
            }
        )
        {
            boolean[] canEdit = new boolean []
            {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(schedule_Table);

        categories_Button.setText("Categories");
        categories_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                categories_ButtonActionPerformed(evt);
            }
        });

        exit_Button.setText("EXIT");
        exit_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                exit_ButtonActionPerformed(evt);
            }
        });

        jButton1.setText("New Schedule");

        database_Label.setForeground(new java.awt.Color(255, 0, 0));
        database_Label.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                database_LabelMouseClicked(evt);
            }
        });

        force_schedule_change_Button.setText("Force Schedule Change");
        force_schedule_change_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                force_schedule_change_ButtonActionPerformed(evt);
            }
        });

        restart_programs_Button.setText("Restart programs");
        restart_programs_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                restart_programs_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(categories_Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(force_schedule_change_Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(restart_programs_Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(exit_Button, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                            .addComponent(database_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(restart_programs_Button))
                    .addComponent(database_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(categories_Button)
                    .addComponent(exit_Button)
                    .addComponent(force_schedule_change_Button))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void categories_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_categories_ButtonActionPerformed
    {//GEN-HEADEREND:event_categories_ButtonActionPerformed
        categories ();
    }//GEN-LAST:event_categories_ButtonActionPerformed

    private void exit_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exit_ButtonActionPerformed
    {//GEN-HEADEREND:event_exit_ButtonActionPerformed
        Main.exit ();
    }//GEN-LAST:event_exit_ButtonActionPerformed

    private void database_LabelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_database_LabelMouseClicked
    {//GEN-HEADEREND:event_database_LabelMouseClicked
        switch_servers ();
    }//GEN-LAST:event_database_LabelMouseClicked

    private void force_schedule_change_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_force_schedule_change_ButtonActionPerformed
    {//GEN-HEADEREND:event_force_schedule_change_ButtonActionPerformed
        force_schedule_change ();
    }//GEN-LAST:event_force_schedule_change_ButtonActionPerformed

    private void restart_programs_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_restart_programs_ButtonActionPerformed
    {//GEN-HEADEREND:event_restart_programs_ButtonActionPerformed
        restart_programs ();
    }//GEN-LAST:event_restart_programs_ButtonActionPerformed
    public static void main ()
        {
        java.awt.EventQueue.invokeLater (() ->
            {
            Main.schedule_frame = new ScheduleFrame ();
            Main.schedule_frame.initialize ();
            Main.schedule_frame.setVisible (true);
            
            //Main.loading_frame.setVisible (true);             
            //Debug.print ("width: " + Main.schedule_frame.getWidth () + " Y: " + Main.schedule_frame.getY ());
            //Main.loading_frame.setLocation (Main.schedule_frame.getWidth (), Main.schedule_frame.getY ());
            });
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton categories_Button;
    private javax.swing.JLabel database_Label;
    private javax.swing.JButton exit_Button;
    private javax.swing.JButton force_schedule_change_Button;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton restart_programs_Button;
    private javax.swing.JTable schedule_Table;
    // End of variables declaration//GEN-END:variables
    }
