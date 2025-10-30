/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool.datetime;

import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.data.Event;
import gsutils.data.Event_Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import scheduletool.Main;

/**
 *
 * @author samla
 */
public class DateTimeDialog extends javax.swing.JDialog
    {
    //---------------------------------------------------------------------------------------------
    private Event          selected_event;
    private LocalDate      original_date;
    private LocalTime      original_time;
    private LocalDate      selected_date; // temporary date
    private LocalTime      selected_time; // temporary time
    private boolean        TBA;
    private boolean        saved;
    private boolean        save_changes;

    private OffsetDateTime offset_date_time;
    //---------------------------------------------------------------------------------------------
    public DateTimeDialog (java.awt.Frame parent, boolean modal, Event selected_event, boolean save_changes)
        {
        super (parent, modal);
        initComponents ();
        this.selected_event = selected_event;
        this.save_changes   = save_changes;
        initialize ();

        ZoneId zone = ZoneId.of ("America/Los_Angeles");
        LocalDate todays_date = LocalDate.now(zone);
        set_date_value_label (todays_date);

        Event_Time event_time = selected_event.getEvent_time ();
        if (event_time != null)
            override_CheckBox.setSelected (event_time.isOverride ());
        }
    //---------------------------------------------------------------------------------------------
    public DateTimeDialog (java.awt.Dialog parent, boolean modal, Event selected_event, boolean save_changes)
        {
        super (parent, modal);
        initComponents ();
        this.selected_event = selected_event;
        this.save_changes   = save_changes;
        initialize ();

        ZoneId zone = ZoneId.of ("America/Los_Angeles");
        LocalDate todays_date = LocalDate.now(zone);
        set_date_value_label (todays_date);

        override_CheckBox.setSelected (selected_event.getEvent_time ().isOverride ());
        }
    //---------------------------------------------------------------------------------------------
    private void set_date_value_label (LocalDate date)
        {
        String day_of_week_uppercase = date.getDayOfWeek() + "";
        String day_of_week = day_of_week_uppercase.substring(0, 1).toUpperCase() + day_of_week_uppercase.substring(1, day_of_week_uppercase.length()).toLowerCase();
        String month_uppercase = date.getMonth() + "";
        String month = month_uppercase.substring(0, 1).toUpperCase() + month_uppercase.substring(1, month_uppercase.length()).toLowerCase();
        int day = date.getDayOfMonth();
        display_date_Label.setText(day_of_week + ", " + month + " " + day);
        }
    //---------------------------------------------------------------------------------------------
    public void initialize_public (Event selected_event)
        {
        this.selected_event = selected_event;
        calendar_Table.clearSelection ();
        initialize ();
        }
    //---------------------------------------------------------------------------------------------
    private void initialize ()
        {
        Event_Time event_time = selected_event.getEvent_time ();
        if (event_time != null)
            initialize (DateTimeUtils.pacific_to_local (event_time.getTime ()), event_time.isTBA ());
        else
            initialize (LocalDateTime.now (), true);
        }
    //---------------------------------------------------------------------------------------------
    private void initialize (LocalDateTime local_date_time, boolean TBA)
        {
        LocalDate local_date = local_date_time.toLocalDate ();
        LocalTime local_time = local_date_time.toLocalTime ();
        initialize (local_date, local_time, TBA);
        }
    //---------------------------------------------------------------------------------------------
    private void initialize (LocalDate local_date, LocalTime local_time, boolean TBA)
        {
        saved = false;
        this.original_date = local_date;
        this.original_time = local_time;
        this.selected_date = local_date;
        this.selected_time = local_time;
        this.TBA  = TBA;
        set_calendar ();
        }
    //---------------------------------------------------------------------------------------------
    private void set_calendar ()
        {
        set_title ();

        int    hour   = selected_time.getHour ();
        int    minute = selected_time.getMinute ();
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

        if (minute >= 10)
            minute_string = "" + minute;
        else
            minute_string = "0" + minute;

        hour_ComboBox   .setSelectedItem (hour_string);
        minute_TextField.setText         (minute_string);
        ampm_ComboBox   .setSelectedItem (ampm_string);

        TBA_CheckBox.setSelected (TBA);

        int month = selected_date.getMonthValue ();
        int day   = selected_date.getDayOfMonth ();
        int year  = selected_date.getYear ();

        LocalDate first_of_the_month = LocalDate.of (year, month, 1);
        DayOfWeek day_of_week = first_of_the_month.getDayOfWeek ();
        int weekday_of_first = (day_of_week.ordinal () + 1) % 7;
        System.out.println ("first_of_the_month " + first_of_the_month);
//        day_of_week = date.getDayOfWeek ();
//        int weekday = (day_of_week.ordinal () + 1) % 7;
        YearMonth yearMonthObject = YearMonth.of (year, month);
        int days_in_month = yearMonthObject.lengthOfMonth ();

        month_Label.setText (DateTimeUtils.months [month - 1]);
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
        selected_date = selected_date.plusMonths (-1);
        set_calendar ();
        }
    //---------------------------------------------------------------------------------------------
    private void next_month ()
        {
        calendar_Table.clearSelection ();
        selected_date = selected_date.plusMonths (1);
        set_calendar ();
        }
    //---------------------------------------------------------------------------------------------
    private void previous_year ()
        {
        calendar_Table.clearSelection ();
        selected_date = selected_date.plusYears (-1);
        set_calendar ();
        }
    //---------------------------------------------------------------------------------------------
    private void next_year ()
        {
        calendar_Table.clearSelection ();
        selected_date = selected_date.plusYears (1);
        set_calendar ();
        }
    //---------------------------------------------------------------------------------------------
    private void select_date (int row, int col)
        {
        String value = (String) calendar_Table.getValueAt (row, col);
        if (value.length () > 0)
            {
            selected_date = LocalDate.of (selected_date.getYear (), selected_date.getMonthValue (), Integer.parseInt (value));
            set_date_value_label (selected_date);
            set_title ();
            }
        }
    //---------------------------------------------------------------------------------------------
    private void select ()
        {
        String  minute              = minute_TextField.getText ();
        boolean minutes_only_digits = minute.matches ("\\d+");

        if (!minutes_only_digits)
            JOptionPane.showMessageDialog (this, "Please enter digits in the minute textfield");
        else
            {
            int minute_int = Integer.parseInt (minute);
            if (minute_int < 0 || minute_int > 59)
                JOptionPane.showMessageDialog (this, "Please enter minutes between 0 and 59");
            else
                {
                int    hour = Integer.parseInt ((String) hour_ComboBox.getSelectedItem ());
                String ampm = (String) ampm_ComboBox.getSelectedItem ();

                if (ampm.equals ("pm"))
                    {
                    if (hour != 12)
                        hour += 12;
                    }
                else if (hour == 12)
                    hour -= 12;

                selected_time = LocalTime.of (hour, minute_int);
                TBA = TBA_CheckBox.isSelected ();
                saved = true;

                int override = 0;
                if (override_CheckBox.isSelected ())
                    override = 1;

                Debug.print ("date (" + selected_date + ") time (" + selected_time + ")");

                LocalDateTime dateTime = LocalDateTime.of (selected_date, selected_time);
                long epoch_date_time = dateTime.atZone (ZoneId.of ("America/Los_Angeles")).toEpochSecond();
                offset_date_time = DateTimeUtils.get_pacific_date_time_from_seconds (epoch_date_time);

                Event_Time event_time = selected_event.getEvent_time ();
                if (event_time == null)
                    {
                    event_time = new Event_Time ();
                    event_time.setTime (offset_date_time);
                    }
                if (save_changes)
                    {
                    if (event_time.getTime () != null && (!event_time.getTime ().isEqual (offset_date_time) || event_time.isTBA () != TBA))
                        {
                        event_time.setTime (offset_date_time);
                        event_time.setTBA (TBA);
                        StringBuilder sql = new StringBuilder ()
                               .append ("INSERT INTO Event_Time (event_id, timestamp, time, TBA, override, source_id) VALUES (")
                               .append (selected_event.getId ())
                               .append (", SYSDATETIMEOFFSET(), '")
                               .append (DateTimeUtils.get_date_time_string_UTC (offset_date_time))
                               .append ("', ")
                               .append (TBA ? "1" : "0")
                               .append (",")
                               .append (override)
                               .append (", 1);\n")
                               ;
                        Debug.print ("printing event time sqL: " + sql);
                        Main.db.executeUpdate (sql);
                        Main.send_schedule_changed_for_event (selected_event);
                        }
                    }
               // if (add_event_dialog != null)
                 //   add_event_dialog.set_date_time (selected_date, selected_time);
                setVisible (false);
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    private void cancel ()
        {
        saved = false;
        setVisible (false);
        }
    //---------------------------------------------------------------------------------------------
    private void set_title ()
        {
        //setTitle (date + " " + time.toString ().substring (0, 5));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("hh:mma");
        String time_string = selected_time.format (formatter).toLowerCase ();
        if (time_string.startsWith ("0"))
            time_string = time_string.substring (1);
        setTitle (selected_date + " " + time_string);
        }
    //---------------------------------------------------------------------------------------------
    private void set_time ()
        {
        String  minute              = minute_TextField.getText ();
        boolean minutes_only_digits = minute.matches ("\\d+");

        if (minute.length () == 0)
            {

            }
        else if (!minutes_only_digits)
            JOptionPane.showMessageDialog (this, "Please enter digits in the minute textfield");
        else
            {
            int minute_int = Integer.parseInt (minute);
            if (minute_int < 0 || minute_int > 59)
                JOptionPane.showMessageDialog (this, "Please enter minutes between 0 and 59");
            else
                {
                int    hour = Integer.parseInt ((String) hour_ComboBox.getSelectedItem ());
                String ampm = (String) ampm_ComboBox.getSelectedItem ();

                if (ampm.equals ("pm"))
                    {
                    if (hour != 12)
                        hour += 12;
                    }
                else if (hour == 12)
                    hour -= 12;

                selected_time = LocalTime.of (hour, minute_int);
                set_title ();
                }
            }
        }
    //---------------------------------------------------------------------------------------------
    public LocalDate getOriginal_date ()
        {
        return original_date;
        }
    //---------------------------------------------------------------------------------------------
    public LocalTime getOriginal_time ()
        {
        return original_time;
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
    public OffsetDateTime get_selected_offset_datetime ()
        {
        return this.offset_date_time;
        }
    //---------------------------------------------------------------------------------------------
    public boolean isSaved ()
        {
        return saved;
        }
    //---------------------------------------------------------------------------------------------
    public boolean isTBA ()
        {
        return TBA_CheckBox.isSelected ();
        }
    //---------------------------------------------------------------------------------------------
    public boolean isOverride ()
        {
        return override_CheckBox.isSelected ();
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
        ampm_ComboBox = new javax.swing.JComboBox<>();
        cancel_Button = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        display_date_Label = new javax.swing.JLabel();
        TBA_CheckBox = new javax.swing.JCheckBox();
        override_CheckBox = new javax.swing.JCheckBox();
        minute_TextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Pacific Time:");

        hour_ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
        hour_ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                hour_ComboBoxActionPerformed(evt);
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

        display_date_Label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        display_date_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        display_date_Label.setText(" ");

        TBA_CheckBox.setText("TBA");

        override_CheckBox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        override_CheckBox.setText("Override");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(override_CheckBox))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(hour_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(minute_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ampm_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TBA_CheckBox))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(select_Button)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cancel_Button))
                            .addGroup(layout.createSequentialGroup()
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
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(display_date_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(display_date_Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(override_CheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hour_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ampm_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TBA_CheckBox)
                    .addComponent(minute_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(select_Button)
                    .addComponent(cancel_Button))
                .addContainerGap())
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

    private void next_year_LabelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_next_year_LabelMouseClicked
    {//GEN-HEADEREND:event_next_year_LabelMouseClicked
        next_year ();
    }//GEN-LAST:event_next_year_LabelMouseClicked

    private void next_month_LabelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_next_month_LabelMouseClicked
    {//GEN-HEADEREND:event_next_month_LabelMouseClicked
        next_month ();
    }//GEN-LAST:event_next_month_LabelMouseClicked

    private void select_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_select_ButtonActionPerformed
    {//GEN-HEADEREND:event_select_ButtonActionPerformed
        select ();
    }//GEN-LAST:event_select_ButtonActionPerformed

    private void hour_ComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_hour_ComboBoxActionPerformed
    {//GEN-HEADEREND:event_hour_ComboBoxActionPerformed
        set_time ();
    }//GEN-LAST:event_hour_ComboBoxActionPerformed

    private void ampm_ComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ampm_ComboBoxActionPerformed
    {//GEN-HEADEREND:event_ampm_ComboBoxActionPerformed
        set_time ();
    }//GEN-LAST:event_ampm_ComboBoxActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancel_ButtonActionPerformed
    {//GEN-HEADEREND:event_cancel_ButtonActionPerformed
        cancel ();
    }//GEN-LAST:event_cancel_ButtonActionPerformed

//    public static void main (JFrame frame, boolean modal, LocalDate date, LocalTime time)
//        {
//        java.awt.EventQueue.invokeLater (() ->
//            {
//            Main.date_and_time_dialog = new DateTimeDialog (frame, modal, date, time);
//            Main.date_and_time_dialog.addWindowListener (new java.awt.event.WindowAdapter ()
//                {
//                @Override
//                public void windowClosing (java.awt.event.WindowEvent e)
//                    {
//                    Main.date_and_time_dialog.saved = false;
//                    Main.date_and_time_dialog.setVisible (false);
//                    }
//                    });
//            Main.date_and_time_dialog.setVisible (true);
//            });
//        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox TBA_CheckBox;
    private javax.swing.JComboBox<String> ampm_ComboBox;
    private javax.swing.JTable calendar_Table;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JLabel display_date_Label;
    private javax.swing.JComboBox<String> hour_ComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField minute_TextField;
    private javax.swing.JLabel month_Label;
    private javax.swing.JLabel next_month_Label;
    private javax.swing.JLabel next_year_Label;
    private javax.swing.JCheckBox override_CheckBox;
    private javax.swing.JLabel previous_month_Label;
    private javax.swing.JLabel previous_year_Label;
    private javax.swing.JButton select_Button;
    private javax.swing.JLabel year_Label;
    // End of variables declaration//GEN-END:variables
    }
