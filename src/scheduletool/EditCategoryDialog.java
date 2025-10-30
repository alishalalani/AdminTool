/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool;

import gsutils.data.Category;
import gsutils.data.League;
import gsutils.data.Category_Type;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import javax.swing.JFrame;
import scheduletool.datetime.DateDialog;

/**
 *
 * @author samla
 */
public class EditCategoryDialog extends javax.swing.JDialog
    {
    //---------------------------------------------------------------------------------------------
    static public DateDialog start_date_dialog;
    static public DateDialog end_date_dialog;
    //---------------------------------------------------------------------------------------------
    private League    league;
    private LocalDate start_date;
    private LocalDate end_date;
    private String    header_text;
    private Category  selected_category;
    //---------------------------------------------------------------------------------------------
    private final ArrayList <Category_Type> category_types = new ArrayList <> ();
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public EditCategoryDialog (java.awt.Frame parent, boolean modal)
        {
        super (parent, modal);
        initComponents ();

        category_types.add (Category_Type.UNKNOWN);
        category_types.add (Category_Type.TEAM);
        category_types.add (Category_Type.PLAYER);
        category_types.add (Category_Type.ADDED_GAMES);
        category_types.add (Category_Type.EXTRA_GAMES);
        category_types.add (Category_Type.WRITE_IN_GAMES);
        category_types.add (Category_Type.GRAND_SALAMI);
        category_types.add (Category_Type.SERIES_PRICE);
        category_types.add (Category_Type.PROP_TEAM);
        category_types.add (Category_Type.PROP_PLAYER);
        category_types.add (Category_Type.FUTURES);
        category_types.add (Category_Type.IN_GAME_LINES);
        }
    //---------------------------------------------------------------------------------------------
   public void initialize_fields (Category category, League league)
        {
        this.league = league;
        this.selected_category = category;
        this.start_date = category.getDate();
        this.end_date = category.getEnd_date();
        String league_name = category.getLeague().getName();
        league_value_Label.setText(league_name);

        String header = category.getHeader();
        header_TextField.setText(header);

        LocalDate local_start_date = category.getDate();
        int month = local_start_date.getMonthValue();
        int day   = local_start_date.getDayOfMonth();
        int year  = local_start_date.getYear();
        String start_date_text = month + "/" + day + "/" + year;
        start_date_TextField.setText(start_date_text);

        LocalDate local_end_date = category.getEnd_date();
        if (local_end_date != null)
            {
            month = local_end_date.getMonthValue();
            day   = local_end_date.getDayOfMonth();
            year  = local_end_date.getYear();
            String end_date_text = month + "/" + day + "/" + year;
            end_date_TextField.setText(end_date_text);
            }

        int category_id = category.getId();
        id_label.setText("ID: " + category_id);

        //set_prefilled_header_label (date);
        //type
        //System.out.println ("catgeory type: " + category.getCategory_type().name());
        }
    //---------------------------------------------------------------------------------------------
    private void start_date ()
        {
        if (start_date_dialog == null)
            start_date_dialog = new DateDialog (this, true, OffsetDateTime.now ());
        else
            start_date_dialog.initialize_public (OffsetDateTime.now ());
        start_date_dialog.set_is_for_start_date_add_edit (true, AddCategoryDialog.AddOrEdit.EDIT);
        start_date_dialog.setVisible (true);
        }
    //---------------------------------------------------------------------------------------------
    private void end_date ()
        {
        if (end_date_dialog == null)
            end_date_dialog = new DateDialog (this, true, OffsetDateTime.now ());
        else
            end_date_dialog.initialize_public (OffsetDateTime.now ());
        end_date_dialog.set_is_for_start_date_add_edit (false, AddCategoryDialog.AddOrEdit.EDIT);
        end_date_dialog.setVisible (true);
        }
    //---------------------------------------------------------------------------------------------
    private void save ()
        {
        this.header_text = header_TextField.getText();
        String data_text = "";
        data_text += "EDIT_CATEGORY>>>\n";
        data_text += "[categories{league_id,start_date,end_date,header,category_type}]\n";
        data_text += "{";
        data_text += league.getId() + ",";
        data_text += ",";
        data_text += start_date.toEpochDay() + ",";
        data_text += (end_date != null ? (end_date.toEpochDay()) : "") + ",";
        data_text += "\"" +  header_text + "\"" + ",";
        data_text += "\"" + category_types.get(category_type_ComboBox.getSelectedIndex()) + "\"";
        data_text += "}\n";
        data_text += ">>>";

        selected_category.setHeader(header_text);
        selected_category.setDate(start_date);
        selected_category.setEnd_date(end_date);
        //SET CATEGORY TYPE
        //selected_category.set

        Main.schedule_client.send(data_text);
        Main.categories_frame.edit_category (selected_category);

        setVisible (false);
        dispose ();
        }
    //---------------------------------------------------------------------------------------------
    private void cancel ()
        {
        setVisible (false);
        dispose ();
        }
    //---------------------------------------------------------------------------------------------
    public void set_league (League league)
        {
        this.league = league;
        league_value_Label.setText(league.getName());
        }
    //---------------------------------------------------------------------------------------------
    public void set_date_time (LocalDate date, boolean start_date)
        {
        int month = date.getMonthValue();
        int day   = date.getDayOfMonth();
        int year  = date.getYear();
        String date_value_string = month + "/" + day + "/" + year;

        if (start_date)
            {
            this.start_date = date;
            start_date_TextField.setText(date_value_string);
            }
        else
            {
            this.end_date = date;
            end_date_TextField.setText(date_value_string);
            }
        }
    //---------------------------------------------------------------------------------------------
    public void set_prefilled_category_header (Category category)
        {
        String full_category = category.getHeader();
        String [] category_parts = full_category.split(" - ", 2);
        String header_title = category_parts[0];
        String potential_date = category_parts[1];

        boolean add_todays_date = false;
        String [] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : days)
            {
            if (potential_date.contains (day))
                {
                add_todays_date = true;
                break;
                }
            }

        if (add_todays_date)
            {
            ZoneId zone = ZoneId.of ("America/Los_Angeles");
            LocalDate todays_date = LocalDate.now (zone);

            String day_of_week = todays_date.getDayOfWeek () + "";
            String month = todays_date.getMonth () + "";
            int day = todays_date.getDayOfMonth ();
            String date = day_of_week + ", " + month + " " + day;
            header_title += " - " + date;
            }

        header_TextField.setText(header_title);
        }
    //---------------------------------------------------------------------------------------------
    public void set_prefilled_header_label (LocalDate date)
        {
        String [] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        String full_category = selected_category.getHeader();
        System.out.println ("FULL CATEGORY: " + full_category);
        String [] category_parts = full_category.split (" - ");

        String local_header_text = "";
        for (int i = 0; i < category_parts.length; i++)
            {
            System.out.println ("PART: " + category_parts[i]);
            boolean is_date = false;
            for (String day_string : days)
                {
                if (category_parts[i].toLowerCase().contains (day_string))
                    {
                    is_date = true;
                    String day_of_week_uppercase = date.getDayOfWeek() + "";
                    String day_of_week = day_of_week_uppercase.substring(0, 1).toUpperCase() + day_of_week_uppercase.substring(1, day_of_week_uppercase.length()).toLowerCase();
                    String month_uppercase = date.getMonth() + "";
                    String month = month_uppercase.substring(0, 1).toUpperCase() + month_uppercase.substring(1, month_uppercase.length()).toLowerCase();
                    int day = date.getDayOfMonth();
                    System.out.println ("DAY IN ADD CATEGORY: " + day);
                    local_header_text += day_of_week + ", " + month + " " + day;
                    if (i != (category_parts.length - 1))
                        local_header_text += " - ";
                    }
                }
            if (!is_date)
                {
                local_header_text += category_parts[i];
                if (i != (category_parts.length - 1))
                    local_header_text += " - ";
                }
            }

        header_TextField.setText(local_header_text);
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

        start_date_Label = new javax.swing.JLabel();
        start_date_TextField = new javax.swing.JTextField();
        end_date_Label = new javax.swing.JLabel();
        end_date_TextField = new javax.swing.JTextField();
        header_Label = new javax.swing.JLabel();
        header_TextField = new javax.swing.JTextField();
        category_type_Label = new javax.swing.JLabel();
        category_type_ComboBox = new javax.swing.JComboBox<>();
        save_Button = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        league_Label = new javax.swing.JLabel();
        league_value_Label = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        id_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Category");

        start_date_Label.setText("Start Date:");

        start_date_TextField.setEditable(false);
        start_date_TextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                start_date_TextFieldMouseClicked(evt);
            }
        });

        end_date_Label.setText("End Date:");

        end_date_TextField.setEditable(false);
        end_date_TextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                end_date_TextFieldMouseClicked(evt);
            }
        });

        header_Label.setText("Header:");

        category_type_Label.setText("Category Type:");

        category_type_ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Unknown", "Team", "Player", "Added games", "Extra games", "Write-in games", "Grand salami", "Series prices", "Prop team", "Prop player", "Futures" }));

        save_Button.setText("Save");
        save_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_ButtonActionPerformed(evt);
            }
        });

        cancel_Button.setText("Cancel");
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });

        league_Label.setText("League: ");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Select Start Date to prefill Header");

        id_label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        id_label.setText("ID: ---");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(category_type_Label)
                            .addComponent(header_Label))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(header_TextField)
                            .addComponent(category_type_ComboBox, 0, 441, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(save_Button)
                        .addGap(44, 44, 44)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancel_Button))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(league_Label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(start_date_Label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(end_date_Label, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(league_value_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(start_date_TextField)
                                    .addComponent(end_date_TextField)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(id_label, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(id_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(league_Label)
                    .addComponent(league_value_Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(start_date_Label)
                    .addComponent(start_date_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(end_date_Label)
                    .addComponent(end_date_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(header_Label)
                    .addComponent(header_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(category_type_Label)
                    .addComponent(category_type_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(save_Button)
                    .addComponent(cancel_Button)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {league_value_Label, start_date_TextField});

        id_label.getAccessibleContext().setAccessibleName("ID: ");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void start_date_TextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_start_date_TextFieldMouseClicked
    {//GEN-HEADEREND:event_start_date_TextFieldMouseClicked
        start_date ();
    }//GEN-LAST:event_start_date_TextFieldMouseClicked

    private void end_date_TextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_end_date_TextFieldMouseClicked
    {//GEN-HEADEREND:event_end_date_TextFieldMouseClicked
        end_date ();
    }//GEN-LAST:event_end_date_TextFieldMouseClicked

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_save_ButtonActionPerformed
    {//GEN-HEADEREND:event_save_ButtonActionPerformed
        save ();
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancel_ButtonActionPerformed
    {//GEN-HEADEREND:event_cancel_ButtonActionPerformed
        cancel ();
    }//GEN-LAST:event_cancel_ButtonActionPerformed
    //---------------------------------------------------------------------------------------------
    public static void main (JFrame parent)
        {
        java.awt.EventQueue.invokeLater (() ->
            {
            Main.edit_category_dialog = new EditCategoryDialog (parent, true);
            Main.edit_category_dialog.addWindowListener (new java.awt.event.WindowAdapter ()
                {
                @Override
                public void windowClosing (java.awt.event.WindowEvent e)
                    {
                    }
                    });
            });
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel_Button;
    private javax.swing.JComboBox<String> category_type_ComboBox;
    private javax.swing.JLabel category_type_Label;
    private javax.swing.JLabel end_date_Label;
    private javax.swing.JTextField end_date_TextField;
    private javax.swing.JLabel header_Label;
    private javax.swing.JTextField header_TextField;
    private javax.swing.JLabel id_label;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel league_Label;
    private javax.swing.JLabel league_value_Label;
    private javax.swing.JButton save_Button;
    private javax.swing.JLabel start_date_Label;
    private javax.swing.JTextField start_date_TextField;
    // End of variables declaration//GEN-END:variables
    }
