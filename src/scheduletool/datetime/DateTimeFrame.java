/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package scheduletool.datetime;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author samla
 */
public class DateTimeFrame extends javax.swing.JFrame
    {
    static final private String months[] =
        {
        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
        };
    //---------------------------------------------------------------------------------------------
    private LocalDate selected_date;
    private LocalTime selected_time;
    private LocalDate date;
    private LocalTime time;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public DateTimeFrame (LocalDate date, LocalTime time)
        {
        this.selected_date = date;
        this.selected_time = time;
        this.date = date;
        this.time = time;
        initComponents ();
        set_calendar ();
        }

    //---------------------------------------------------------------------------------------------
    private void set_calendar ()
        {
        set_title ();

        int    hour   = time.getHour ();
        int    minute = time.getMinute ();
        String hour_string;
        String minute_string;
        String ampm_string;

        if (hour < 12)
            ampm_string = "am";
        else
            {
            ampm_string = "pm";
            if (hour > 12)
                hour -= 12;
            }

        if (hour == 0)
            hour_string = "12";
        else if (hour >= 10)
            hour_string = "" + hour;
        else
            hour_string = "0" + hour;

        int minute2 = (minute / 5) * 5;
        if (minute2 >= 10)
            minute_string = "" + minute2;
        else
            minute_string = "0" + minute2;

        hour_ComboBox  .setSelectedItem (hour_string);
        minute_ComboBox.setSelectedItem (minute_string);
        ampm_ComboBox  .setSelectedItem (ampm_string);

        int month = date.getMonthValue ();
        int day   = date.getDayOfMonth ();
        int year  = date.getYear ();

        LocalDate first_of_the_month = LocalDate.of (year, month, 1);
        DayOfWeek day_of_week = first_of_the_month.getDayOfWeek ();
        int weekday_of_first = (day_of_week.ordinal () + 1) % 7;
        System.out.println ("first_of_the_month " + first_of_the_month);
//        day_of_week = date.getDayOfWeek ();
//        int weekday = (day_of_week.ordinal () + 1) % 7;
        YearMonth yearMonthObject = YearMonth.of (year, month);
        int days_in_month = yearMonthObject.lengthOfMonth ();

        month_Label.setText (months [month - 1]);
        year_Label.setText ("" + year);

        calendar_Table.setDefaultRenderer (String.class, new DateCellRenderer ());
        calendar_Table.setRowHeight (DateCellRenderer.height ());

        DefaultTableModel table_model = (DefaultTableModel) calendar_Table.getModel ();

        int date_of_month = 1;
//        boolean next_month = false;
        for (int row = 0; row < 6; row++)
            {
            for (int column = 0; column < 7; column++)
                {
                if (row == 0 && column < weekday_of_first)
                    table_model.setValueAt ("", row, column);
                else if (date_of_month > days_in_month)
                    {
                    table_model.setValueAt ("", row, column);
//                    next_month = true;
//                    date_of_month = 1;
//                    table_model.setValueAt ("" + date_of_month, row, column);
//                    date_of_month++;
                    }
                else
                    {
                    if (date_of_month == day)
                        calendar_Table.changeSelection (row, column, true, false);
                    table_model.setValueAt ("" + date_of_month, row, column);
                    date_of_month++;
                    }
                }
            }
        }

    //---------------------------------------------------------------------------------------------
    private void previous_month ()
        {
        calendar_Table.clearSelection ();
        date = date.plusMonths (-1);
        set_calendar ();
        }

    //---------------------------------------------------------------------------------------------
    private void next_month ()
        {
        calendar_Table.clearSelection ();
        date = date.plusMonths (1);
        set_calendar ();
        }

    //---------------------------------------------------------------------------------------------
    private void previous_year ()
        {
        calendar_Table.clearSelection ();
        date = date.plusYears (-1);
        set_calendar ();
        }

    //---------------------------------------------------------------------------------------------
    private void next_year ()
        {
        calendar_Table.clearSelection ();
        date = date.plusYears (1);
        set_calendar ();
        }

    //---------------------------------------------------------------------------------------------
    private void select_date (int row, int col)
        {
        DefaultTableModel table_model = (DefaultTableModel) calendar_Table.getModel ();
        String value = (String) calendar_Table.getValueAt (row, col);
        if (value.length () > 0)
            {
            date = LocalDate.of (date.getYear (), date.getMonthValue (), Integer.parseInt (value));
            set_title ();
            }
        }

    //---------------------------------------------------------------------------------------------
    private void select ()
        {
        String function = "DateTimeFrame.select";
        selected_date = date;
        int hour = Integer.parseInt ((String) hour_ComboBox.getSelectedItem ());
        int minute = Integer.parseInt ((String) minute_ComboBox.getSelectedItem ());
        String ampm = (String) ampm_ComboBox.getSelectedItem ();
        if (ampm.equals ("pm"))
            {
            if (hour != 12)
                hour += 12;
            }
        else if (hour == 12)
            hour -= 12;
        selected_time = LocalTime.of (hour, minute);
        System.out.println (function + ":  date (" + selected_date + ") time (" + selected_time + ")");
        setVisible (false);
        }

    //---------------------------------------------------------------------------------------------
    private void cancel ()
        {
        setVisible (false);
        }

    //---------------------------------------------------------------------------------------------
    private void set_title ()
        {
        //setTitle (date + " " + time.toString ().substring (0, 5));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("hh:mma");
        String time_string = time.format (formatter).toLowerCase ();
        if (time_string.startsWith ("0"))
            time_string = time_string.substring (1);
        setTitle (date + " " + time_string);
        }

    //---------------------------------------------------------------------------------------------
    private void set_time ()
        {
        String function = "DateTimeFrame.set_time";
        int hour = Integer.parseInt ((String) hour_ComboBox.getSelectedItem ());
        int minute = Integer.parseInt ((String) minute_ComboBox.getSelectedItem ());
        String ampm = (String) ampm_ComboBox.getSelectedItem ();
        if (ampm.equals ("pm"))
            {
            if (hour != 12)
                hour += 12;
            }
        else if (hour == 12)
            hour -= 12;
        time = LocalTime.of (hour, minute);
        set_title ();
        }

    //---------------------------------------------------------------------------------------------
    public LocalDate getSelected_date ()
        {
        return selected_date;
        }

    //---------------------------------------------------------------------------------------------
    public LocalTime getSelected_time ()
        {
        return selected_time;
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
        calendar_Table = new javax.swing.JTable();
        month_Label = new javax.swing.JLabel();
        year_Label = new javax.swing.JLabel();
        previous_month_Label = new javax.swing.JLabel();
        previous_year_Label = new javax.swing.JLabel();
        next_year_Label = new javax.swing.JLabel();
        next_month_Label = new javax.swing.JLabel();
        select_Button = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        hour_ComboBox = new javax.swing.JComboBox<>();
        minute_ComboBox = new javax.swing.JComboBox<>();
        ampm_ComboBox = new javax.swing.JComboBox<>();
        cancel_Button = new javax.swing.JButton();

        calendar_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String []
            {
                "S", "M", "T", "W", "T", "F", "S"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean []
            {
                false, false, false, false, false, false, false
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
        calendar_Table.setCellSelectionEnabled(true);
        calendar_Table.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                calendar_TableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(calendar_Table);

        month_Label.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        month_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        month_Label.setText("month");

        year_Label.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        year_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        year_Label.setText("year");

        previous_month_Label.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        previous_month_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        previous_month_Label.setText("<");
        previous_month_Label.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        previous_month_Label.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                previous_month_LabelMouseClicked(evt);
            }
        });

        previous_year_Label.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        previous_year_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        previous_year_Label.setText("<<");
        previous_year_Label.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        previous_year_Label.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                previous_year_LabelMouseClicked(evt);
            }
        });

        next_year_Label.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        next_year_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        next_year_Label.setText(">>");
        next_year_Label.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        next_year_Label.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                next_year_LabelMouseClicked(evt);
            }
        });

        next_month_Label.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        next_month_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        next_month_Label.setText(">");
        next_month_Label.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        next_month_Label.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                next_month_LabelMouseClicked(evt);
            }
        });

        select_Button.setText("Select");
        select_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                select_ButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Time:");

        hour_ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
        hour_ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                hour_ComboBoxActionPerformed(evt);
            }
        });

        minute_ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55" }));
        minute_ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                minute_ComboBoxActionPerformed(evt);
            }
        });

        ampm_ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "am", "pm" }));
        ampm_ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ampm_ComboBoxActionPerformed(evt);
            }
        });

        cancel_Button.setText("Cancel");
        cancel_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancel_ButtonActionPerformed(evt);
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hour_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(minute_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ampm_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(select_Button)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cancel_Button))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(previous_year_Label, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                                .addComponent(previous_month_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(month_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(year_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(next_year_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(next_month_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(previous_month_Label)
                    .addComponent(month_Label)
                    .addComponent(next_month_Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(next_year_Label, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(previous_year_Label)
                        .addComponent(year_Label)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(hour_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minute_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ampm_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(select_Button)
                    .addComponent(cancel_Button))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void calendar_TableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_calendar_TableMouseClicked
    {//GEN-HEADEREND:event_calendar_TableMouseClicked
        int row = calendar_Table.rowAtPoint (evt.getPoint ());
        int col = calendar_Table.columnAtPoint (evt.getPoint ());
        select_date (row, col);
    }//GEN-LAST:event_calendar_TableMouseClicked

    private void previous_month_LabelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_previous_month_LabelMouseClicked
    {//GEN-HEADEREND:event_previous_month_LabelMouseClicked
        previous_month ();
    }//GEN-LAST:event_previous_month_LabelMouseClicked

    private void previous_year_LabelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_previous_year_LabelMouseClicked
    {//GEN-HEADEREND:event_previous_year_LabelMouseClicked
        previous_year ();
    }//GEN-LAST:event_previous_year_LabelMouseClicked

    private void next_month_LabelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_next_month_LabelMouseClicked
    {//GEN-HEADEREND:event_next_month_LabelMouseClicked
        next_month ();
    }//GEN-LAST:event_next_month_LabelMouseClicked

    private void next_year_LabelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_next_year_LabelMouseClicked
    {//GEN-HEADEREND:event_next_year_LabelMouseClicked
        next_year ();
    }//GEN-LAST:event_next_year_LabelMouseClicked

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancel_ButtonActionPerformed
    {//GEN-HEADEREND:event_cancel_ButtonActionPerformed
        cancel ();
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void select_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_select_ButtonActionPerformed
    {//GEN-HEADEREND:event_select_ButtonActionPerformed
        select ();
    }//GEN-LAST:event_select_ButtonActionPerformed

    private void ampm_ComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ampm_ComboBoxActionPerformed
    {//GEN-HEADEREND:event_ampm_ComboBoxActionPerformed
        set_time ();
    }//GEN-LAST:event_ampm_ComboBoxActionPerformed

    private void minute_ComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_minute_ComboBoxActionPerformed
    {//GEN-HEADEREND:event_minute_ComboBoxActionPerformed
        set_time ();
    }//GEN-LAST:event_minute_ComboBoxActionPerformed

    private void hour_ComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_hour_ComboBoxActionPerformed
    {//GEN-HEADEREND:event_hour_ComboBoxActionPerformed
        set_time ();
    }//GEN-LAST:event_hour_ComboBoxActionPerformed

    public static void main (LocalDate date, LocalTime time)
        {
        java.awt.EventQueue.invokeLater (() ->
            {
            DateTimeFrame date_and_time_frame = new DateTimeFrame (date, time);
            date_and_time_frame.setVisible (true);
            });
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ampm_ComboBox;
    private javax.swing.JTable calendar_Table;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JComboBox<String> hour_ComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> minute_ComboBox;
    private javax.swing.JLabel month_Label;
    private javax.swing.JLabel next_month_Label;
    private javax.swing.JLabel next_year_Label;
    private javax.swing.JLabel previous_month_Label;
    private javax.swing.JLabel previous_year_Label;
    private javax.swing.JButton select_Button;
    private javax.swing.JLabel year_Label;
    // End of variables declaration//GEN-END:variables
    }
