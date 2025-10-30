/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool;

import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.data.Category;
import gsutils.data.League;
import gsutils.data.Category_Type;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import scheduletool.datetime.DateDialog;

/**
 *
 * @author samla
 */
public class AddCategoryDialog extends javax.swing.JDialog
    {
    //---------------------------------------------------------------------------------------------
    static public DateDialog start_date_dialog;
    static public DateDialog end_date_dialog;

    private League    league;
    private LocalDate start_date;
    private LocalDate end_date;
    private String    header_text;
    private String    category_type_string;
    //---------------------------------------------------------------------------------------------
    private ArrayList <String> category_type_strings = new ArrayList <> ();
    private ArrayList <Category_Type> category_types = new ArrayList <> ();
    private Category category_sample;
    //---------------------------------------------------------------------------------------------
    private Category selected_category;
    public enum AddOrEdit {ADD, EDIT};
    private AddOrEdit add_or_edit = AddOrEdit.ADD;
    //0 = add
    //1 = edit
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public AddCategoryDialog (java.awt.Frame parent, boolean modal)
        {
        super (parent, modal);
        initComponents ();

        //Unknown
        }
    //---------------------------------------------------------------------------------------------
    //for editing category
    public void initialize_fields_edit_category (Category category, League league)
        {
        setTitle ("Edit Category");
        this.add_or_edit = AddOrEdit.EDIT;
        this.league = league;
        this.selected_category = category;
        this.start_date = category.getDate();
        this.end_date = category.getEnd_date();
        String league_name = category.getLeague().getName();
        league_value_Label.setText(league_name);

        description_TextArea.setText (category.getDescription ());
        header_TextArea.setText (category.getHeader ());

        LocalDate local_start_date = category.getDate();
        int month = local_start_date.getMonthValue();
        int day   = local_start_date.getDayOfMonth();
        int year  = local_start_date.getYear();
        start_date_TextField.setText (month + "/" + day + "/" + year);

        LocalDate local_end_date = category.getEnd_date();
        if (local_end_date != null)
            {
            month = local_end_date.getMonthValue();
            day   = local_end_date.getDayOfMonth();
            year  = local_end_date.getYear();
            end_date_TextField.setText (month + "/" + day + "/" + year);
            }

        int category_id = category.getId();
        id_Label.setText ("Category ID: " + category_id);

        initialize_category_types ();

        int category_type_index = -1;
        for (int i = 0; i < category_types.size(); i++)
            {
            Category_Type current_type = category_types.get(i);
            if (current_type.equals (selected_category.getCategory_type ()))
                {
                category_type_index = i;
                break;
                }
            }
        if (category_type_index >= 0)
            category_type_ComboBox.setSelectedIndex (category_type_index);

        System.out.println ("INDEX FOR CATEGORY TYPE: " + category_type_index);
        System.out.println ("CATEGORY TYPE: " + selected_category.getCategory_type ());

        setTitle ("Edit Category");
        exclude_CheckBox.setSelected (selected_category.isExclude ());
        override_CheckBox.setSelected (selected_category.isOverride ());

        //set_prefilled_header_label (date);
        //type
        //System.out.println ("catgeory type: " + category.getCategory_type().name());

        description_Label.setVisible     (true);
        description_TextArea.setVisible (true);
        }
    //-----------------------------------------------------------------------------------------------------------------------
    public void initialize_fields_add_category (League league, Category category_sample_for_header)
        {
        setTitle ("Add Category");
        selected_category = new Category ();
        exclude_CheckBox.setSelected (false);
        this.category_sample = category_sample_for_header;
       // set_prefilled_category_header ();
        initialize_category_types ();

        this.league = league;
        selected_category.setLeague(league);
        league_value_Label.setText(league.getName());
        id_Label.setText ("Category ID: ---");
        start_date_TextField.setText("");
        end_date_TextField.setText("");
        header_TextArea.setText("Select Start Date to prefill header");
        start_date_TextField.setText ("Click to Select");
        end_date_TextField.setText ("Click to Select");
        category_type_ComboBox.setSelectedIndex(0);
        override_CheckBox.setSelected (false);

        setTitle ("Add Category");
        description_Label.setVisible     (false);
        description_TextArea.setVisible (false);
        }
    //----------------------------------------------------------------------------------------------------------------------
    private void start_date ()
        {
        if (start_date_dialog == null)
            start_date_dialog = new DateDialog (this, true, OffsetDateTime.now ());
        else
            start_date_dialog.initialize_public (OffsetDateTime.now ());
        start_date_dialog.set_is_for_start_date_add_edit (true, add_or_edit);
        start_date_dialog.setVisible (true);
        }
    //---------------------------------------------------------------------------------------------
    private void end_date ()
        {
        if (end_date_dialog == null)
            end_date_dialog = new DateDialog (this, true, OffsetDateTime.now ());
        else
            end_date_dialog.initialize_public (OffsetDateTime.now ());
        end_date_dialog.set_is_for_start_date_add_edit (false, add_or_edit);
        end_date_dialog.setVisible (true);
        }
    //---------------------------------------------------------------------------------------------
    private void save ()
        {
        //end date not mandatory
        header_text = header_TextArea.getText();

        if (add_or_edit == AddOrEdit.ADD)
            {
            selected_category = new Category ();
            selected_category.setDescription (header_text);
            }

        selected_category.setLeague   (league);
        selected_category.setDate     (start_date);
        selected_category.setEnd_date (end_date);
        selected_category.setHeader   (header_text);
        selected_category.setExclude  (exclude_CheckBox.isSelected ());
        selected_category.setOverride (override_CheckBox.isSelected ());

        int index = category_type_ComboBox.getSelectedIndex();
        selected_category.setCategory_type (category_types.get (index));

        save_changes ();

        if (add_or_edit == AddOrEdit.ADD)
            {
            add_category_database ();
            Main.add_category (selected_category);
            Main.categories_frame.add_new_category (selected_category);
            }
        else
            {
            save_category_to_database ();
            Main.send_category (selected_category);
            Main.categories_frame.edit_category (selected_category);
            }

        setVisible (false);
       // dispose ();
        }
    //---------------------------------------------------------------------------------------------
    public void add_category_database ()
        {
        int override = 0;
        if (override_CheckBox.isSelected ())
            override = 1;

        StringBuilder sql = new StringBuilder ();
        sql.append ("DECLARE @schedule_id    SMALLINT;\n")
           .append ("DECLARE @sc_sequence    SMALLINT;\n")
           .append ("DECLARE @category_id    INT;\n")
           .append ("SELECT @schedule_id = id FROM Schedule AS sch WHERE sch.timestamp = (SELECT MAX(timestamp) FROM Schedule AS sch1 WHERE sch1.date=(SELECT value FROM GlobalValues WHERE name1='schedule' AND name2='current'))\n\n")
           .append ("SELECT @sc_sequence = MAX(sequence) FROM Schedule_Category AS sc WHERE sc.schedule_id = @schedule_id;\n\n")

           .append ("SET @category_id = NULL;\n")
           .append ("SELECT @category_id = id FROM Category WHERE date='")
           .append (DateTimeUtils.get_date_string (selected_category.getDate ()))
           .append ("' AND league_id=")
           .append (selected_category.getLeague ().getId ())
           .append (" AND description='")
           .append (selected_category.getDescription ().replaceAll ("'", "''"))
           .append ("';\n")
           .append ("IF @category_id IS NULL\n")
           .append ("BEGIN\n")

           .append ("INSERT INTO Category (date, end_date, league_id, description, category_type_id, header, override, timestamp)\nVALUES ('")
           .append (DateTimeUtils.get_date_string (selected_category.getDate ()))
           .append ("', NULL, ")
           .append (selected_category.getLeague ().getId ())
           .append (", '")
           .append (selected_category.getDescription ().replaceAll ("'", "''"))
           .append ("', ")
           .append (selected_category.getCategory_type ().ordinal ())
           .append (", '")
           .append (selected_category.getDescription ().replaceAll ("'", "''"))
           .append ("',")
           .append (override)
           .append (", SYSDATETIMEOFFSET());\n")
           .append ("SET @category_id = SCOPE_IDENTITY();\n")
           .append ("SET @sc_sequence = @sc_sequence + 1;\n")
           .append ("INSERT INTO Schedule_Category (schedule_id, category_id, sequence) VALUES (@schedule_id, @category_id, @sc_sequence);\n")
           .append ("END\n")
           .append ("SELECT @category_id AS category_id;\n")
           ;

        Debug.print ("SQL>>>\n" + sql + "\n<<<");
        int category_id;
        try
            {
            ResultSet rs = Main.db.executeQuery (sql);
            if (rs != null)
                {
                rs.next ();
                category_id = rs.getInt ("category_id");
                selected_category.setId (category_id);
                }
            }
        catch (Exception e)
            {
            Debug.print (":  " + e);
            e.printStackTrace ();
            }
        }
    //---------------------------------------------------------------------------------------------
    private void save_category_to_database ()
        {
        int override = 0;
        if (override_CheckBox.isSelected ())
            override = 1;

        StringBuilder sql = new StringBuilder ();
        sql.append ("-- schedule changed - add_category\n")
           .append ("IF EXISTS (SELECT id FROM Category WHERE id = ")
           .append (selected_category.getId ())
           .append (")\n")
           .append ("  BEGIN\n")
           .append ("  UPDATE Category SET league_id=")
           .append (selected_category.getLeague ().getId ())
           .append (", date='")
           .append (selected_category.getDate ())
           .append ("', end_date=")
           .append (selected_category.getEnd_date () == null ? "NULL" : "'" + selected_category.getEnd_date () + "'")
           .append (", description='")
           .append (selected_category.getDescription ().replaceAll ("'", "''"))
           .append ("', header='")
           .append (selected_category.getHeader ().replaceAll ("'", "''"))
           .append ("', exclude=")
           .append (selected_category.isExclude () ? "1" : "0")
           .append (", override=")
           .append (override)
           .append (" WHERE id=")
           .append (selected_category.getId ())
           .append (";\n")
           .append ("  END\n")
           ;
        Debug.print ("SQL>>>\n" + sql.toString () + "\n<<<");
        Main.db.executeUpdate (sql);
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
    public void set_date (LocalDate date, boolean start_date)
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
        for (int i = 0; i < days.length; i++)
            {
            if (potential_date.contains(days[i]))
                {
                add_todays_date = true;
                break;
                }
            }

        if (add_todays_date)
            {
            ZoneId zone = ZoneId.of("America/Los_Angeles");
            LocalDate todays_date = LocalDate.now(zone);

            String day_of_week = todays_date.getDayOfWeek() + "";
            String month = todays_date.getMonth() + "";
            int day = todays_date.getDayOfMonth();
            String date = day_of_week + ", " + month + " " + day;
            header_title += " - " + date;
            }

        header_TextArea.setText (header_title);
        }
    //---------------------------------------------------------------------------------------------
    public void set_prefilled_category_sample (Category category)
        {
        this.category_sample = category;
        initialize_category_types ();
        }
//---------------------------------------------------------------------------------------------
//    public void set_prefilled_header_label (LocalDate date)
//        {
//        String [] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
//        String full_category = category_sample.getHeader();
//        System.out.println ("FULL CATEGORY: " + full_category);
//        String [] category_parts = full_category.split(" - ");
//
//        for (int i = 0; i < category_parts.length; i++)
//            System.out.println ("category parts i: " + i + " (" +category_parts[i] + ")");
//
//        String header_text = "";
//        for (int i = 0; i < category_parts.length - 1; i++)
//            {
//            System.out.println ("PART: " + category_parts[i]);
//            boolean is_date = false;
//            for (int j = 0; j < days.length; j++)
//                {
//                if (category_parts[i].toLowerCase().contains(days[j]))
//                    {
//                    is_date = true;
//                    String day_of_week_uppercase = date.getDayOfWeek() + "";
//                    String day_of_week = day_of_week_uppercase.substring(0, 1).toUpperCase() + day_of_week_uppercase.substring(1, day_of_week_uppercase.length()).toLowerCase();
//                    String month_uppercase = date.getMonth() + "";
//                    String month = month_uppercase.substring(0, 1).toUpperCase() + month_uppercase.substring(1, month_uppercase.length()).toLowerCase();
//                    int day = date.getDayOfMonth();
//                    //System.out.println ("DA IN ADD CATEGORY: " + day);
//                    header_text += day_of_week + ", " + month + " " + day + Utils.suffix (day);
//                    if (i != (category_parts.length - 1))
//                        header_text += " - ";
//                    }
//                }
//            if (!is_date)
//                {
//                if (i == (category_parts.length - 1))
//                    header_text += category_parts[i].split(" -")[0];
//                else
//                    {
//                    header_text += category_parts[i];
//                    header_text += " - ";
//                    }
//                }
//            }
//
//        header_TextField.setText(header_text);
//        }
    //---------------------------------------------------------------------------------------------
    public void prefill_header_after_selecting_date (LocalDate local_date)
        {
        String day = (local_date.getDayOfWeek () + "").toUpperCase ();
        day = day.substring (0, 1).toUpperCase () + day.substring (1).toLowerCase ();

        int year = local_date.getYear ();
        String month = (local_date.getMonth () + "");
        month = month.substring (0, 1).toUpperCase () + month.substring (1).toLowerCase ();

        int day_of_month = local_date.getDayOfMonth ();
        String date = day + ", " + month + " " + day_of_month + get_day_ending (day_of_month);
        String league_description = this.league.getFullname ().toUpperCase ();

        String prefilled_header = league_description + " - " + date + " - ";
        header_TextArea.setText (prefilled_header);
        }
    //---------------------------------------------------------------------------------------------
    public String get_day_ending (int day)
        {
        switch (day)
            {
            case 1:
            case 21:
            case 31:
                return "st";
            case 2:
            case 22:
                return "nd";
            case 3:
            case 23:
                return "rd";
            default:
                return "th";
            }
        }
    //---------------------------------------------------------------------------------------------
    public void initialize_category_types ()
        {
        //category_type_strings.add ("Unknown");
        category_type_strings.add ("Team"); //id = 1
        category_type_strings.add ("Player");
        category_type_strings.add ("Added Games");
        category_type_strings.add ("Extra Gmaes");
        category_type_strings.add ("Write In Games");
        category_type_strings.add ("Grand Salami");
        category_type_strings.add ("Series Price");
        category_type_strings.add ("Prop Team");
        category_type_strings.add ("Prop Player");
        category_type_strings.add ("Futures");
        category_type_strings.add ("In-Game Lines");

        //category_types.add (Category_Type.UNKNOWN);
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

        DefaultComboBoxModel category_model = new DefaultComboBoxModel ();
        for (int i = 0; i < category_type_strings.size(); i++)
            {
            category_model.addElement(category_type_strings.get(i));
            }
        category_type_ComboBox.setModel (category_model);
        }
    //-----------------------------------------------------------------------------------------------------------------------
    public void category_type_selected ()
        {
        int index = category_type_ComboBox.getSelectedIndex();
        selected_category.setCategory_type (category_types.get (index));
        }
    //-----------------------------------------------------------------------------------------------------------------------
    public void save_changes ()
        {
//        selected_category.setDate(start_date);
//        selected_category.setEnd_date(end_date);
//        String header = header_TextField.getText();
//        selected_category.setHeader(header);
//
//        int index = category_type_ComboBox.getSelectedIndex();
//        selected_category.setCategory_type (category_types.get (index));
        }
    //-----------------------------------------------------------------------------
    public void clear_start_date ()
        {
        start_date_TextField.setText(" ");
        this.start_date = null;
        }
    //-----------------------------------------------------------------------------
    public void clear_end_date ()
        {
        end_date_TextField.setText(" ");
        this.end_date = null;
        }
    //-----------------------------------------------------------------------------
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

        save_Button = new javax.swing.JButton();
        category_Panel = new javax.swing.JPanel();
        league_value_Label = new javax.swing.JLabel();
        id_Label = new javax.swing.JLabel();
        header_Label = new javax.swing.JLabel();
        header_ScrollPane = new javax.swing.JScrollPane();
        header_TextArea = new javax.swing.JTextArea();
        start_date_panel = new javax.swing.JPanel();
        start_date_TextField = new javax.swing.JTextField();
        clear_start_date_TextField = new javax.swing.JButton();
        start_date_Label = new javax.swing.JLabel();
        end_date_panel = new javax.swing.JPanel();
        end_date_TextField = new javax.swing.JTextField();
        clear_end_date_TextField = new javax.swing.JButton();
        end_date_Label = new javax.swing.JLabel();
        category_type_ComboBox = new javax.swing.JComboBox<>();
        exclude_CheckBox = new javax.swing.JCheckBox();
        description_Label = new javax.swing.JLabel();
        description_ScrollPane = new javax.swing.JScrollPane();
        description_TextArea = new javax.swing.JTextArea();
        override_CheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Category");

        save_Button.setText("Save");
        save_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                save_ButtonActionPerformed(evt);
            }
        });

        category_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        category_Panel.setMinimumSize(new java.awt.Dimension(665, 291));

        league_value_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        league_value_Label.setText("League");

        id_Label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        id_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        id_Label.setText("Category ID: ---");

        header_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        header_Label.setText("Header:");
        header_Label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        header_TextArea.setColumns(20);
        header_TextArea.setLineWrap(true);
        header_TextArea.setRows(2);
        header_TextArea.setText("Select Start Date to prefill Header");
        header_ScrollPane.setViewportView(header_TextArea);

        start_date_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        start_date_panel.setMinimumSize(new java.awt.Dimension(322, 37));
        start_date_panel.setPreferredSize(new java.awt.Dimension(333, 35));
        start_date_panel.setLayout(new java.awt.GridBagLayout());

        start_date_TextField.setEditable(false);
        start_date_TextField.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        start_date_TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        start_date_TextField.setText("Click to Select Start Date");
        start_date_TextField.setMinimumSize(new java.awt.Dimension(70, 22));
        start_date_TextField.setPreferredSize(new java.awt.Dimension(64, 25));
        start_date_TextField.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                start_date_TextFieldMouseClicked(evt);
            }
        });
        start_date_TextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                start_date_TextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.ipady = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 6, 1, 0);
        start_date_panel.add(start_date_TextField, gridBagConstraints);

        clear_start_date_TextField.setText("clear");
        clear_start_date_TextField.setMaximumSize(new java.awt.Dimension(321, 3213));
        clear_start_date_TextField.setMinimumSize(new java.awt.Dimension(42, 22));
        clear_start_date_TextField.setPreferredSize(new java.awt.Dimension(42, 25));
        clear_start_date_TextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                clear_start_date_TextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.ipady = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 6, 1, 4);
        start_date_panel.add(clear_start_date_TextField, gridBagConstraints);

        start_date_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        start_date_Label.setText("Start Date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 7;
        gridBagConstraints.ipady = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 0);
        start_date_panel.add(start_date_Label, gridBagConstraints);

        end_date_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        end_date_panel.setMinimumSize(new java.awt.Dimension(322, 37));
        end_date_panel.setPreferredSize(new java.awt.Dimension(333, 35));
        end_date_panel.setLayout(new java.awt.GridBagLayout());

        end_date_TextField.setEditable(false);
        end_date_TextField.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        end_date_TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        end_date_TextField.setText("Click to Select End Date");
        end_date_TextField.setMinimumSize(new java.awt.Dimension(70, 22));
        end_date_TextField.setPreferredSize(new java.awt.Dimension(64, 25));
        end_date_TextField.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                end_date_TextFieldMouseClicked(evt);
            }
        });
        end_date_TextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                end_date_TextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.ipady = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 6, 1, 0);
        end_date_panel.add(end_date_TextField, gridBagConstraints);

        clear_end_date_TextField.setText("clear");
        clear_end_date_TextField.setMaximumSize(new java.awt.Dimension(321, 3213));
        clear_end_date_TextField.setMinimumSize(new java.awt.Dimension(42, 22));
        clear_end_date_TextField.setPreferredSize(new java.awt.Dimension(42, 25));
        clear_end_date_TextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                clear_end_date_TextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.ipady = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 6, 1, 4);
        end_date_panel.add(clear_end_date_TextField, gridBagConstraints);

        end_date_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        end_date_Label.setText("End Date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.ipady = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 0);
        end_date_panel.add(end_date_Label, gridBagConstraints);

        category_type_ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Team", "Player", "Added games", "Extra games", "Write-in games", "Grand salami", "Series prices", "Prop team", "Prop player", "Futures" }));
        category_type_ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                category_type_ComboBoxActionPerformed(evt);
            }
        });

        exclude_CheckBox.setText("Exclude");
        exclude_CheckBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                exclude_CheckBoxActionPerformed(evt);
            }
        });

        description_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        description_Label.setText("Description:");
        description_Label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        description_TextArea.setColumns(20);
        description_TextArea.setLineWrap(true);
        description_TextArea.setRows(2);
        description_ScrollPane.setViewportView(description_TextArea);

        override_CheckBox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        override_CheckBox.setText("Override");

        javax.swing.GroupLayout category_PanelLayout = new javax.swing.GroupLayout(category_Panel);
        category_Panel.setLayout(category_PanelLayout);
        category_PanelLayout.setHorizontalGroup(
            category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(category_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(category_PanelLayout.createSequentialGroup()
                        .addComponent(league_value_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(id_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, category_PanelLayout.createSequentialGroup()
                        .addComponent(header_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(header_ScrollPane))
                    .addGroup(category_PanelLayout.createSequentialGroup()
                        .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(start_date_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(end_date_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addComponent(category_type_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(exclude_CheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(override_CheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(category_PanelLayout.createSequentialGroup()
                        .addComponent(description_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(description_ScrollPane)))
                .addContainerGap())
        );
        category_PanelLayout.setVerticalGroup(
            category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(category_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(league_value_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(id_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(header_Label, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(header_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(start_date_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(category_type_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(exclude_CheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(override_CheckBox))
                    .addComponent(end_date_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(description_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(description_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        category_PanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {description_Label, header_Label});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(save_Button))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(category_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(category_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(save_Button)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void start_date_TextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_start_date_TextFieldMouseClicked
    {//GEN-HEADEREND:event_start_date_TextFieldMouseClicked
    start_date ();
    }//GEN-LAST:event_start_date_TextFieldMouseClicked

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_save_ButtonActionPerformed
    {//GEN-HEADEREND:event_save_ButtonActionPerformed
        save ();
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void category_type_ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_category_type_ComboBoxActionPerformed
        category_type_selected ();
    }//GEN-LAST:event_category_type_ComboBoxActionPerformed

    private void start_date_TextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_start_date_TextFieldActionPerformed
    {//GEN-HEADEREND:event_start_date_TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_start_date_TextFieldActionPerformed

    private void clear_start_date_TextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clear_start_date_TextFieldActionPerformed
    {//GEN-HEADEREND:event_clear_start_date_TextFieldActionPerformed
        // TODO add your handling code here:
        clear_start_date ();
    }//GEN-LAST:event_clear_start_date_TextFieldActionPerformed

    private void exclude_CheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exclude_CheckBoxActionPerformed
    {//GEN-HEADEREND:event_exclude_CheckBoxActionPerformed
        // TODO add your handling code here:
        selected_category.setExclude (exclude_CheckBox.isSelected ());
    }//GEN-LAST:event_exclude_CheckBoxActionPerformed

    private void end_date_TextFieldMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_end_date_TextFieldMouseClicked
    {//GEN-HEADEREND:event_end_date_TextFieldMouseClicked
        // TODO add your handling code here:
        end_date ();
    }//GEN-LAST:event_end_date_TextFieldMouseClicked

    private void end_date_TextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_end_date_TextFieldActionPerformed
    {//GEN-HEADEREND:event_end_date_TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_end_date_TextFieldActionPerformed

    private void clear_end_date_TextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clear_end_date_TextFieldActionPerformed
    {//GEN-HEADEREND:event_clear_end_date_TextFieldActionPerformed
        // TODO add your handling code here:
        clear_end_date ();
    }//GEN-LAST:event_clear_end_date_TextFieldActionPerformed
    //---------------------------------------------------------------------------------------------
    public static void main (JFrame parent)
        {
        //java.awt.EventQueue.invokeLater (() ->
            {
            Main.add_category_dialog = new AddCategoryDialog (parent, true);
            Main.add_category_dialog.addWindowListener (new java.awt.event.WindowAdapter ()
                {
                @Override
                public void windowClosing (java.awt.event.WindowEvent e)
                    {
                    }
                    });
            }//);
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel category_Panel;
    private javax.swing.JComboBox<String> category_type_ComboBox;
    private javax.swing.JButton clear_end_date_TextField;
    private javax.swing.JButton clear_start_date_TextField;
    private javax.swing.JLabel description_Label;
    private javax.swing.JScrollPane description_ScrollPane;
    private javax.swing.JTextArea description_TextArea;
    private javax.swing.JLabel end_date_Label;
    private javax.swing.JTextField end_date_TextField;
    private javax.swing.JPanel end_date_panel;
    private javax.swing.JCheckBox exclude_CheckBox;
    private javax.swing.JLabel header_Label;
    private javax.swing.JScrollPane header_ScrollPane;
    private javax.swing.JTextArea header_TextArea;
    private javax.swing.JLabel id_Label;
    private javax.swing.JLabel league_value_Label;
    private javax.swing.JCheckBox override_CheckBox;
    private javax.swing.JButton save_Button;
    private javax.swing.JLabel start_date_Label;
    private javax.swing.JTextField start_date_TextField;
    private javax.swing.JPanel start_date_panel;
    // End of variables declaration//GEN-END:variables
    }
