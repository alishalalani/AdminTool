/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool;

import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.data.Category;
import gsutils.data.Event;
import gsutils.data.League;
import gsutils.data.Preset_Message;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

//---------------------------------------------------------------------------------------------------------------------------------------
import static scheduletool.Main.db;
//---------------------------------------------------------------------------------------------------------------------------------------


//---------------------------------------------------------------------------------------------------------------------------------------
public class MessageDialog extends javax.swing.JDialog
    {
//---------------------------------------------------------------------------------------------------------------------------------------
    private int      league_id;
    private Event    selected_event;
    private Category selected_category;
    private boolean  for_event;
    private Preset_Message_Dialog preset_message_dialog;
//---------------------------------------------------------------------------------------------------------------------------------------
    private TreeMap <Integer, Preset_Message> preset_messages_treemap;
    private ArrayList <Preset_Message> preset_messages_arraylist;
//---------------------------------------------------------------------------------------------------------------------------------------
    public MessageDialog (java.awt.Frame parent, boolean modal)
        {
        super (parent, modal);
        initComponents ();

        // Enable line wrap
        message_TextArea.setLineWrap(true);
        // Enable word wrap
        message_TextArea.setWrapStyleWord(true);

        initialize_preset_messages (-1);
        //initialize_edit_icon ();
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void initialize_fields_for_event (int league_id, Event selected_event)
        {
        this.selected_event = selected_event;
        this.league_id      = league_id;
        this.for_event      = true;

        game_number_Label.setText ("Gm#: " + selected_event.getNumber ());

        clear_fields ();
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void initialize_fields_for_category (int league_id, Category selected_category)
        {
        this.selected_category = selected_category;
        this.league_id      = league_id;
        this.for_event      = false;

        game_number_Label.setText ("ID: " + selected_category.getId ());

        clear_fields ();
        }

//---------------------------------------------------------------------------------------------------------------------------------------
    public void clear_fields ()
        {
        setTitle ("Send Message");

        if (preset_messages_JList.getSelectedIndex () >= 0)
            preset_messages_JList.clearSelection ();

        message_TextArea.setText ("");
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void initialize_edit_icon ()
        {
        Icon edit_icon = new ImageIcon(getClass().getResource("edit_icon.png"));
        edit_Button.setIcon(edit_icon);
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    private void send_message ()
        {
        if (for_event)
            send_message_for_event ();
        else
            send_message_for_category ();

        clear_fields ();
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void send_message_for_category ()
        {
        String message_type = (String) message_type_ComboBox.getSelectedItem ();
        String type         = (html_CheckBox.isSelected () ? "html" : "txt");

        StringBuilder message = new StringBuilder ("MESSAGE>>>\n")
                              .append ("[{messagesV2{updated,message{header,league_id,game_number,type,data}}}]{")
                              .append (DateTimeUtils.get_current_pacific_time ())
                              .append ("{")
                              .append ("Notice")
//                              .append (game_number == 0 ? "Notice" : (header))
                              .append (",")
//                              .append ("Notice,0,0,")
                                //league_id
                              .append (league_id)
                              .append (",")
                              .append (0)
                              .append (",")
                              .append (type)
                              .append (",\"")
                              .append (message_TextArea.getText ().trim ().replaceAll ("\"", "\"\""))
                              .append ("\"}\n<<<\n")

//                              .append ("[{messages{updated,message{header,game_number,type,data}}}]{")
//                              .append (DateTimeUtils.get_current_pacific_time ())
//                              .append ("{")
//                              .append (header)
//                              .append (",")
//                              .append (game_number)
//                              .append (",")
//                              .append (type)
//                              .append (",\"")
//                              .append (message_TextArea.getText ().replaceAll ("\"", "\"\""))
//                              .append ("\"}\n<<<\n")
                              ;
        Main.schedule_client.send (message);
        Debug.print ("Sent (" + message.toString () + ")");
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void send_message_for_event ()
        {
        String header = (String) message_type_ComboBox.getSelectedItem ();
        String type = (html_CheckBox.isSelected () ? "html" : "txt");

        StringBuilder message = new StringBuilder ("MESSAGE>>>\n")
                              .append ("[{messagesV2{updated,message{header,league_id,game_number,type,data}}}]{")
                              .append (DateTimeUtils.get_current_pacific_time ())
                              .append ("{")
                              .append ("Notice")
//                              .append (game_number == 0 ? "Notice" : (header))
                              .append (",")
//                              .append ("Notice,0,0,")
                                //league_id
                              .append (league_id)
                              .append (",")
                              .append (selected_event.getNumber ())
                              .append (",")
                              .append (type)
                              .append (",\"")
                              .append (message_TextArea.getText ().trim ().replaceAll ("\"", "\"\""))
                              .append ("\"}\n<<<\n")

//                              .append ("[{messages{updated,message{header,game_number,type,data}}}]{")
//                              .append (DateTimeUtils.get_current_pacific_time ())
//                              .append ("{")
//                              .append (header)
//                              .append (",")
//                              .append (game_number)
//                              .append (",")
//                              .append (type)
//                              .append (",\"")
//                              .append (message_TextArea.getText ().replaceAll ("\"", "\"\""))
//                              .append ("\"}\n<<<\n")
                              ;
        Main.schedule_client.send (message);
        Debug.print ("Sent (" + message.toString () + ")");
        }

//---------------------------------------------------------------------------------------------------------------------------------------
    public void exit ()
        {
        setVisible (false);
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void clear_preset_message_selection ()
        {
        preset_messages_JList.clearSelection ();
        message_TextArea.setText ("");
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void add_preset_message ()
        {
        if (this.preset_message_dialog == null)
            preset_message_dialog = new Preset_Message_Dialog (this, true);
        preset_message_dialog.initialize_fields_add ();

        if (preset_message_dialog.isSaved ())
            {
            String new_preset_message_string = preset_message_dialog.get_preset_message ();
            //add to list of preset messages
            int new_preset_message_id = add_preset_message_database (new_preset_message_string);

            Preset_Message new_preset_message = new Preset_Message ();
            new_preset_message.setId (new_preset_message_id);
            new_preset_message.setValue (new_preset_message_string);
            new_preset_message.setActive (true);

            preset_messages_treemap.put (new_preset_message_id, new_preset_message);
            initialize_preset_messages (new_preset_message_id);
            }
        }

//---------------------------------------------------------------------------------------------------------------------------------------
    public int add_preset_message_database (String preset_message_string)
        {
        int preset_message_id = 0;

        StringBuilder sql = new StringBuilder ();
        sql.append ("INSERT INTO Preset_Message (value, active) VALUES ('")
           .append (preset_message_string.trim ().replaceAll ("'", "''"))
           .append ("',")
           .append ("1")
           .append (");")
           .append ("SELECT SCOPE_IDENTITY() AS preset_message_id;\n")
           ;
        try
            {
            ResultSet rs = db.executeQuery (sql);
            if (rs != null)
                {
                rs.next ();
                preset_message_id = rs.getInt ("preset_message_id");
                System.out.println ("got id from preset message: " + preset_message_id);
                }
            }
        catch (Exception e)
            {
            Debug.print (":  " + e);
            e.printStackTrace ();
            }

        return preset_message_id;
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void edit_preset_message ()
        {
        int index    = preset_messages_JList.getSelectedIndex ();
        String value = preset_messages_JList.getSelectedValue ();
        Preset_Message selected_preset_message = preset_messages_arraylist.get (index);

        if (index < 0)
            {
            JOptionPane.showMessageDialog(null, "A preset message must be selected before attempting to edit.", "No preset message selected", JOptionPane.PLAIN_MESSAGE);
            }
        else
            {
            if (this.preset_message_dialog == null)
                preset_message_dialog = new Preset_Message_Dialog (this, true);

            preset_message_dialog.initialize_fields_edit (value);

            if (preset_message_dialog.isSaved ())
                {
                String new_preset_message_string = preset_message_dialog.get_preset_message ();
                selected_preset_message.setValue (new_preset_message_string);

                //edit value in treemap
                Preset_Message treemap_preset_message = preset_messages_treemap.get (selected_preset_message.getId ());
                treemap_preset_message.setValue (new_preset_message_string);

                edit_preset_message_database (selected_preset_message);
                initialize_preset_messages (selected_preset_message.getId ());
                }
            }
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void edit_preset_message_database (Preset_Message selected_preset_message)
        {
        String message    = selected_preset_message.getValue ().trim ();
        int    message_id = selected_preset_message.getId ();

        String sql =   "UPDATE Preset_Message "
                     + "SET value='" + message.replaceAll ("'", "''") + "'"
                     + " WHERE id="  + message_id;

        Debug.print ("(" + sql + ")");
        try
            {
            db.executeUpdate (sql);
            }
        catch (Exception e)
            {
            Debug.print (":  " + e);
            e.printStackTrace ();
            }
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void delete_preset_message ()
        {
        int index    = preset_messages_JList.getSelectedIndex ();
        Preset_Message selected_preset_message = preset_messages_arraylist.get (index);

        //edit active value in treemap
        Preset_Message treemap_preset_message = preset_messages_treemap.get (selected_preset_message.getId ());
        treemap_preset_message.setActive (false);

        delete_preset_message_database (selected_preset_message);
        initialize_preset_messages (-1);
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void delete_preset_message_database (Preset_Message preset_message)
        {
        int message_id = preset_message.getId ();

        String sql =   "UPDATE Preset_Message "
                     + "SET active=0"
                     + " WHERE id="  + message_id;

        System.out.println (sql);
        try
            {
            db.executeUpdate (sql);
            }
        catch (Exception e)
            {
            Debug.print (":  " + e);
            e.printStackTrace ();
            }
        }
//---------------------------------------------------------------------------------------------------------------------------------------

    //takes in parameter of preset message id if a new one was added or existing one was edited
    //new_preset_message_id will be -1 if no new message to add
    public void initialize_preset_messages (int preset_message_id)
        {
        int selected_index = -1;

        preset_messages_treemap = Preset_Message.getPreset_messages ();
        TreeMap <String , Preset_Message> preset_messages_sorted = new TreeMap <> ();
        preset_messages_arraylist = new ArrayList <> ();

        DefaultListModel <String> preset_messages_model = new DefaultListModel<>();

        for (Preset_Message preset_message : preset_messages_treemap.values ())
            preset_messages_sorted.put (preset_message.getValue (), preset_message);

        int index = 0;
        for (Preset_Message preset_message : preset_messages_sorted.values ())
            {
            if (preset_message.isActive ())
                {
                preset_messages_arraylist.add(preset_message);
                preset_messages_model.addElement (preset_message.getValue ());

                if (preset_message.getId () == preset_message_id)
                    selected_index = index;

                index++;
                }
            }

        preset_messages_JList.setModel (preset_messages_model);

        if (selected_index >= 0)
            preset_messages_JList.setSelectedIndex (selected_index);
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void preset_message_selected ()
        {
        int index = preset_messages_JList.getSelectedIndex ();
        if (index >= 0)
            {
            Preset_Message selected_preset_message = preset_messages_arraylist.get (index);

            if (for_event)
                fill_message_box_for_event    (selected_preset_message);
            else
                fill_message_box_for_category (selected_preset_message);
            }
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void fill_message_box_for_event (Preset_Message selected_preset_message)
        {
        League league = League.get_league (league_id);
        String away_team = selected_event.getEvent_items ().get (0).getEvent_item_league_team ().getLeague_team ().getName ();
        String home_team = selected_event.getEvent_items ().get (1).getEvent_item_league_team ().getLeague_team ().getName ();

        String full_message = (league.getAbbr () + " - Game " + selected_event.getNumber () + " - "
                                                 + away_team + " vs " + home_team + " "
                                                 + selected_preset_message.getValue ()).trim ();
        message_TextArea.setText (full_message);
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public void fill_message_box_for_category (Preset_Message selected_preset_message)
        {
        League league = League.get_league (league_id);
        Debug.print ( "Selected league: " + league.getId ());

        String category_header = null;
        if (league.getId () == gsutils.data.League.AUTO_FORMULA_1 ||
            league.getId () == gsutils.data.League.AUTO_INDYCAR   ||
            league.getId () == gsutils.data.League.AUTO_NASCAR    ||
            league.getId () == gsutils.data.League.AUTO_OTHER     ||
            league.getId () == gsutils.data.League.AUTO_TRUCKS    ||
            league.getId () == gsutils.data.League.AUTO_XFINITY     )
                category_header = parse_header_auto_racing ();
        else
            category_header = parse_header ();

        String full_message = (category_header + " - " + selected_preset_message.getValue ()).trim ();
        message_TextArea.setText (full_message);
        }

//---------------------------------------------------------------------------------------------------------------------------------------
    public boolean contains_date (String word)
        {
        word = word.toUpperCase ();

        String [] days_of_the_week = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        String [] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

        for (int i = 0; i < days_of_the_week.length; i++)
            {
            String current_day = days_of_the_week [i];
            if (word.contains (current_day))
                return true;
            }

        for (int i = 0; i < months.length; i++)
            {
            String current_day = months [i];
            if (word.contains (current_day))
                return true;
            }

        return false;
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public String parse_header_auto_racing ()
        {
        String category_header = selected_category.getHeader ();
        if (category_header.contains (" - "))
            {
            String [] header_parts = category_header.split (" - ");
            String first_part = header_parts [0];
            if (first_part.toUpperCase ().contains ("MATCHUPS"))
                first_part = first_part.substring (0, first_part.indexOf ("MATCHUPS"));
            category_header = convert_to_camel_case (first_part);
            for (int i = 1; i < header_parts.length; i++)
                {
                String current_part = header_parts [i];
                if (contains_date (current_part))
                    break;

                if (current_part.toUpperCase ().contains ("MATCHUPS"))
                    current_part = current_part.substring (0, current_part.indexOf ("MATCHUPS"));

                category_header += " - " + convert_to_camel_case (current_part);
                }
            }
        else
            category_header = convert_to_camel_case (category_header);

        return category_header.trim ();
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public String parse_header ()
        {
        String category_header = selected_category.getHeader ();
        if (category_header.contains (" - "))
            {
            String [] header_parts = category_header.split (" - ");
            category_header = convert_to_camel_case (header_parts [0]);
            for (int i = 1; i < header_parts.length; i++)
                {
                if (contains_date (header_parts [i]))
                    break;
                category_header += " - " + convert_to_camel_case (header_parts [i]);
                }
            }
        else
            category_header = convert_to_camel_case (category_header);

        return category_header;
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public String parse_header_auto_racing_old ()
        {
        Debug.print ("Calling parse header for auto racing");
        //FORMULA 1 - MONACO GRAND PRIX MATCHUPS - Sunday, May 26th @Circuit De Monaco
        String category_header   = selected_category.getHeader ();
        String [] category_parts = category_header.split (" - ", 3);
        String first_element       = category_parts [0];
        String matchup_string    = category_parts [1];

        if (matchup_string.toUpperCase ().contains ("MATCHUPS"))
            matchup_string = matchup_string.substring (0, matchup_string.indexOf ("MATCHUPS"));
        else if (first_element.toUpperCase ().contains ("MATCHUPS"))
            first_element = first_element.substring (0, first_element.indexOf ("MATCHUPS"));

        first_element  = convert_to_camel_case (first_element.trim ());
        matchup_string = convert_to_camel_case (matchup_string.trim ());
        category_header = first_element + " - " + matchup_string;

        return category_header;
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public String convert_to_camel_case (String string_to_convert)
        {
        String camel_case_string = "";
        if (string_to_convert.contains (" "))
            {
            String [] words = string_to_convert.split (" ");

            for (int i = 0; i < words.length; i++)
                {
                String current_word = words [i];
                //(Semis)
                int first_character_index = find_first_character (current_word);
                if (first_character_index == 0 && current_word.length () >= 2)
                    current_word = current_word.substring (0, 1).toUpperCase () + current_word.substring (1).toLowerCase ();
                //-=hi
                //first_character_index = 2, +1 = 3
                //length = 4
                //(Semis)
                else if (current_word.length () > (first_character_index + 1))
                    {
                    current_word = current_word.substring (0, first_character_index)
                                 + current_word.substring (first_character_index, first_character_index + 1).toUpperCase ()
                                 + current_word.substring (first_character_index + 1                               ).toLowerCase ();
                    }
                //-=h
                else if (current_word.length () == (first_character_index + 1))
                    {
                    current_word = current_word.substring (0, first_character_index)
                                 + current_word.substring (first_character_index, first_character_index + 1).toUpperCase ();
                    }

                //Coca-cola
                if (current_word.contains ("-"))
                    {
                    String [] dash_parts = current_word.split ("-");
                    current_word = dash_parts [0];
                    for (int j = 1; j < dash_parts.length; j++)
                        {
                        String next_part_of_dash = dash_parts [j];
                        next_part_of_dash = next_part_of_dash.substring (0, 1).toUpperCase () + next_part_of_dash.substring (1).toLowerCase ();
                        current_word += "-" + next_part_of_dash;
                        }
                    }

                camel_case_string += current_word;
                camel_case_string += " ";
                }

            camel_case_string.trim ();
            }
        else
            camel_case_string = string_to_convert.substring (0, 1).toUpperCase () + string_to_convert.substring (1).toLowerCase ();
        return camel_case_string;
        }
//---------------------------------------------------------------------------------------------------------------------------------------
    public int find_first_character (String word)
        {
        for (int i = 0; i < word.length (); i++)
            {
            char current_char = word.charAt (i);
            if (Character.isLetter (current_char))
                return i;
            }

        return 0;
        }
//---------------------------------------------------------------------------------------------------------------------------------------
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

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jLabel1 = new javax.swing.JLabel();
        message_type_ComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        html_CheckBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        preset_messages_JList = new javax.swing.JList<>();
        preset_messages_panel = new javax.swing.JPanel();
        edit_Button = new javax.swing.JButton();
        delete_Button = new javax.swing.JButton();
        add_Button = new javax.swing.JButton();
        clear_selection_Button = new javax.swing.JButton();
        preset_messages_Label = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        message_TextArea = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        send_message_Button = new javax.swing.JButton();
        exit_Button = new javax.swing.JButton();
        clear_message_Button = new javax.swing.JButton();
        game_number_Label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 600));
        setPreferredSize(new java.awt.Dimension(600, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Message Tool");
        jLabel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 375;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        getContentPane().add(jLabel1, gridBagConstraints);

        message_type_ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Notice", "Urgent", "Finals", "Lineups", "Revised_lineups", "Umpires", "Officials", "Referees", "Halftime", "Pitching_change", "Steam_play", "Time_change", "Key_move", "Odds_watch", "Middle_alert", "System_play", "Cancelled", "Postponed", "started", "Other messages" }));
        message_type_ComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 2, 0);
        getContentPane().add(message_type_ComboBox, gridBagConstraints);

        jLabel2.setText("Message Type:");
        jLabel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jLabel2.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 39, 0, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        html_CheckBox.setText("HTML");
        html_CheckBox.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        html_CheckBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 45;
        gridBagConstraints.ipady = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 21, 0, 6);
        getContentPane().add(html_CheckBox, gridBagConstraints);

        preset_messages_JList.setModel(new javax.swing.AbstractListModel<String>()
        {
            String[] strings = { "Alisha", "Billy", "Bob", "Example message #1", "Example message #2", "I like pie" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        preset_messages_JList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                preset_messages_JListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(preset_messages_JList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 486;
        gridBagConstraints.ipady = 145;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        preset_messages_panel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        edit_Button.setText("Edit");
        edit_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                edit_ButtonActionPerformed(evt);
            }
        });

        delete_Button.setText("-");
        delete_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                delete_ButtonActionPerformed(evt);
            }
        });

        add_Button.setText("+");
        add_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                add_ButtonActionPerformed(evt);
            }
        });

        clear_selection_Button.setText("Clear selection");
        clear_selection_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                clear_selection_ButtonActionPerformed(evt);
            }
        });

        preset_messages_Label.setBackground(new java.awt.Color(0, 204, 204));
        preset_messages_Label.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        preset_messages_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        preset_messages_Label.setText("Preset Messages");

        javax.swing.GroupLayout preset_messages_panelLayout = new javax.swing.GroupLayout(preset_messages_panel);
        preset_messages_panel.setLayout(preset_messages_panelLayout);
        preset_messages_panelLayout.setHorizontalGroup(
            preset_messages_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, preset_messages_panelLayout.createSequentialGroup()
                .addComponent(clear_selection_Button)
                .addGap(54, 54, 54)
                .addComponent(preset_messages_Label, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                .addGap(58, 58, 58)
                .addComponent(add_Button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delete_Button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edit_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        preset_messages_panelLayout.setVerticalGroup(
            preset_messages_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preset_messages_panelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(preset_messages_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preset_messages_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(edit_Button)
                        .addComponent(delete_Button)
                        .addComponent(add_Button)
                        .addComponent(preset_messages_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(clear_selection_Button))
                .addGap(0, 0, 0))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 6, 0, 6);
        getContentPane().add(preset_messages_panel, gridBagConstraints);

        message_TextArea.setColumns(20);
        message_TextArea.setRows(5);
        jScrollPane2.setViewportView(message_TextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 486;
        gridBagConstraints.ipady = 104;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 6);
        getContentPane().add(jScrollPane2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Message Box");
        jLabel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 408;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 0, 6);
        getContentPane().add(jLabel3, gridBagConstraints);

        jSeparator1.setForeground(new java.awt.Color(0, 51, 51));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 501;
        gridBagConstraints.ipady = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        getContentPane().add(jSeparator1, gridBagConstraints);

        jSeparator2.setForeground(new java.awt.Color(0, 51, 51));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 501;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 0, 6);
        getContentPane().add(jSeparator2, gridBagConstraints);

        send_message_Button.setText("Send Message");
        send_message_Button.setMaximumSize(new java.awt.Dimension(130, 22));
        send_message_Button.setMinimumSize(new java.awt.Dimension(130, 22));
        send_message_Button.setPreferredSize(new java.awt.Dimension(130, 22));
        send_message_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                send_message_ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        getContentPane().add(send_message_Button, gridBagConstraints);

        exit_Button.setText("Exit");
        exit_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                exit_ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        getContentPane().add(exit_Button, gridBagConstraints);

        clear_message_Button.setText("Clear Message");
        clear_message_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                clear_message_ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        getContentPane().add(clear_message_Button, gridBagConstraints);

        game_number_Label.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        game_number_Label.setText("Gm#: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 58;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 0);
        getContentPane().add(game_number_Label, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clear_message_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clear_message_ButtonActionPerformed
    {//GEN-HEADEREND:event_clear_message_ButtonActionPerformed
        message_TextArea.setText ("");
    }//GEN-LAST:event_clear_message_ButtonActionPerformed

    private void send_message_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_send_message_ButtonActionPerformed
    {//GEN-HEADEREND:event_send_message_ButtonActionPerformed
        send_message ();
    }//GEN-LAST:event_send_message_ButtonActionPerformed

    private void exit_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exit_ButtonActionPerformed
    {//GEN-HEADEREND:event_exit_ButtonActionPerformed
        exit ();
    }//GEN-LAST:event_exit_ButtonActionPerformed

    private void clear_selection_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clear_selection_ButtonActionPerformed
    {//GEN-HEADEREND:event_clear_selection_ButtonActionPerformed
        clear_preset_message_selection ();
    }//GEN-LAST:event_clear_selection_ButtonActionPerformed

    private void add_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_add_ButtonActionPerformed
    {//GEN-HEADEREND:event_add_ButtonActionPerformed
        add_preset_message ();
    }//GEN-LAST:event_add_ButtonActionPerformed

    private void edit_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_edit_ButtonActionPerformed
    {//GEN-HEADEREND:event_edit_ButtonActionPerformed
        edit_preset_message ();
    }//GEN-LAST:event_edit_ButtonActionPerformed

    private void delete_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_delete_ButtonActionPerformed
    {//GEN-HEADEREND:event_delete_ButtonActionPerformed
        delete_preset_message ();
    }//GEN-LAST:event_delete_ButtonActionPerformed

    private void preset_messages_JListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_preset_messages_JListValueChanged
    {//GEN-HEADEREND:event_preset_messages_JListValueChanged
        // This condition ensures that the event is not fired multiple times for a single selection change
        if (!evt.getValueIsAdjusting())
            preset_message_selected ();

    }//GEN-LAST:event_preset_messages_JListValueChanged

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
            java.util.logging.Logger.getLogger (MessageDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (InstantiationException ex)
            {
            java.util.logging.Logger.getLogger (MessageDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (IllegalAccessException ex)
            {
            java.util.logging.Logger.getLogger (MessageDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
            {
            java.util.logging.Logger.getLogger (MessageDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        //java.awt.EventQueue.invokeLater (new Runnable ()
            MessageDialog dialog = new MessageDialog (new javax.swing.JFrame (), true);
            dialog.addWindowListener (new java.awt.event.WindowAdapter ()
                {
                @Override
                public void windowClosing (java.awt.event.WindowEvent e)
                    {
                    System.exit (0);
                    }
                });
            dialog.setVisible (true);
            //);
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add_Button;
    private javax.swing.JButton clear_message_Button;
    private javax.swing.JButton clear_selection_Button;
    private javax.swing.JButton delete_Button;
    private javax.swing.JButton edit_Button;
    private javax.swing.JButton exit_Button;
    private javax.swing.JLabel game_number_Label;
    private javax.swing.JCheckBox html_CheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea message_TextArea;
    private javax.swing.JComboBox<String> message_type_ComboBox;
    private javax.swing.JList<String> preset_messages_JList;
    private javax.swing.JLabel preset_messages_Label;
    private javax.swing.JPanel preset_messages_panel;
    private javax.swing.JButton send_message_Button;
    // End of variables declaration//GEN-END:variables
    }
