/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool;

import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.HDF.Linescore;
import gsutils.Utils;
import gsutils.data.Event;
import gsutils.data.Event_Item;
import gsutils.data.Event_Item_League_Team;
import gsutils.data.Event_Score;
import gsutils.data.League_Team;
import gsutils.data.Source;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author bunny
 */
public class LinescoreDialog extends javax.swing.JDialog
    {
    static private boolean updatingTable = false; // Flag to prevent recursive updates
    //---------------------------------------------------------------------------------------------
    private ArrayList <Source> sources_all;
    private Event     ol_event;
    private Linescore linescore;
    private int       columns_size;

    private int       selected_source_id;
    private boolean   close_dialog;
    private boolean   updateLinescoreTable                   = false;
    private boolean   already_asked_to_create_new_line_score = false;
    private boolean   score_correction                       = false;
    private int       corrected_away_score;
    private int       corrected_home_score;
    //----------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------
    public LinescoreDialog (java.awt.Dialog parent, boolean modal)
        {
        super (parent, modal);
        initComponents ();
        }
    //----------------------------------------------------------------------------------------
    public void initialize (Event ol_event)
        {
        initialize (ol_event, false, -1, -1);
        }
    //----------------------------------------------------------------------------------------
    public void initialize (Event ol_event, boolean score_correction, int corrected_away_score, int corrected_home_score)
        {
        close_dialog = false;
        this.ol_event = ol_event;
        this.score_correction = score_correction;
        this.corrected_away_score = corrected_away_score;
        this.corrected_home_score = corrected_home_score;
        score_correction_CheckBox.setSelected (score_correction);
        updateLinescoreTable = true;
        //add_table_selection_listener (); // for updating total which we do NOT want for now

        initialize_source_combobox (Source.ESPN);
        periods_TextField.setText ("" + columns_size);
        if (close_dialog)
            setVisible (false);
        else
            setVisible (true);
        }
    //----------------------------------------------------------------------------------------
    public void periods_textfield_edited ()
        {
        columns_size = Integer.parseInt (periods_TextField.getText ());
        initialize_linescore_table ();
        }
    //----------------------------------------------------------------------------------------
    public void initialize_linescore_table ()
        {
        // Column Names
        String[] columnNames = new String [columns_size + 3];
        columnNames [0] = "Team";
        for (int i = 1; i < columns_size + 1; i++)
            {
            columnNames [i] = i + "";
            }
        columnNames [columns_size + 1] = "T";
        columnNames [columns_size + 2] = "Status";

        DefaultTableModel model = (DefaultTableModel) line_score_Table.getModel ();
        //model.setColumnCount (6);
        model.setColumnIdentifiers (columnNames);
        line_score_Table.getColumnModel().getColumn(0).setPreferredWidth(200);

        model.setRowCount (0);

        Object [] away_line_row = get_row_data_from_line_score (linescore.getAwayLineScore (), true);
        Object [] home_line_row = get_row_data_from_line_score (linescore.getHomeLineScore (), false);

        model.addRow (away_line_row);
        model.addRow (home_line_row);
        line_score_Table.setModel (model);

        line_score_Table.setRowHeight (30);

        // Calculate the height needed for just the header and one row
        int height = line_score_Table.getRowHeight() + line_score_Table.getTableHeader().getHeight();

        // Set the preferred size of the scroll pane
        jScrollPane1.setPreferredSize(new java.awt.Dimension (line_score_Table.getPreferredSize().width, height));

        line_score_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Create a custom cell renderer to center text
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Set the renderer to all columns
        for (int columnIndex = 0; columnIndex < line_score_Table.getColumnCount(); columnIndex++)
            {
            line_score_Table.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
            }

        if (linescore.getAwayLineScore ().isEmpty () && linescore.getHomeLineScore ().isEmpty ())
            {
            if (!already_asked_to_create_new_line_score)
                {
                already_asked_to_create_new_line_score = true;
                int response = JOptionPane.showConfirmDialog(this,
                                "No linescore data available. Do you want to create a new linescore?",
                                "Confirm",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.NO_OPTION)
                    close_dialog = true;
                }
            }
        }
    //----------------------------------------------------------------------------------------
    public void initialize_source_combobox (int source_id)
        {
        TreeMap <Integer, Source> sources = Source.getSources ();
        sources_all = new ArrayList <> ();
        DefaultComboBoxModel sources_model = new DefaultComboBoxModel ();
        int i = 0;
        int index = -1;
        for (Source source : sources.values ())
            {
            sources_model.addElement (source.getName ());
            sources_all.add (source);

            if (source.getId () == source_id)
                index = i;
            i++;
            }
        source_ComboBox.setModel (sources_model);
        if (index >= 0)
            {
            source_ComboBox.setSelectedIndex (index);
            selected_source_id = source_id;
            }
        }
    //----------------------------------------------------------------------------------------
    public void source_selected ()
        {
        int index = source_ComboBox.getSelectedIndex ();

        if (index >= 0)
            {
            selected_source_id = sources_all.get (index).getId ();
            if (updateLinescoreTable)
                {
                linescore = get_linescore ();
                int away_line_score_size = linescore.getAwayLineScore ().size ();
                int home_line_score_size = linescore.getHomeLineScore ().size ();
                // Find the maximum value
                columns_size = Math.max (ol_event.getLeague ().getPeriods (), Math.max(away_line_score_size, home_line_score_size));
                periods_TextField.setText (columns_size + "");
                initialize_linescore_table ();
                }
            }
        }
    //----------------------------------------------------------------------------------------
    public Linescore get_linescore ()
        {
        Linescore local_linescore = new Linescore ();

//        String sql = "EXEC usp_Line_Score @EventDate = '" + ol_event.getDate () + "', @EventNumber = " + ol_event.getNumber ();
        String sql = "EXEC usp_Line_Score @EventDate = '" + ol_event.getDate () + "', @EventNumber = " + ol_event.getNumber () + ", @Source = " + selected_source_id;
        Debug.print (sql);
        int source_id = -1;
        try
            {
            ResultSet rs = Main.db.executeQuery (sql);
            if (rs != null)
                {
                while (rs.next ())
                    {
                    int period = rs.getInt ("event_item_score_period");
                    Integer current_away_score = rs.getInt ("away_score");
                    if (!rs.wasNull ())
                        local_linescore.addToAwayLineScore (period, current_away_score);

                    Integer current_home_score = rs.getInt ("home_score");
                    if (!rs.wasNull ())
                        local_linescore.addToHomeLineScore (period, current_home_score);

                    if (source_id == -1)
                        source_id = rs.getInt ("away_source_id");
                    }
                }
            }
        catch (Exception e)
            {
            Debug.print (":  " + e);
            e.printStackTrace ();
            }

        if (source_id > 0 && source_id != selected_source_id)
            {
            updateLinescoreTable = false;
            selected_source_id = source_id;
            initialize_source_combobox (source_id);
            updateLinescoreTable = true;
            }
        return local_linescore;
        }
    //----------------------------------------------------------------------------------------
    public Object [] get_row_data_from_line_score (TreeMap <Integer, Integer> linescore, boolean away)
        {
        Object [] line_row = new Object [columns_size + 3];
        int i = 1;
        Event_Item event_item = ol_event.getEvent_items ().get ((away ? 0 : 1));
        line_row [0] = event_item.getEvent_item_league_team ().getLeague_team ().getName ();
        for (Integer score : linescore.values ())
            {
            line_row [i] = "" + score;
            i++;
            }
        Event_Score event_score = ol_event.getEvent_score ();
        if (event_score != null)
            {
            line_row [columns_size + 1] = event_score.getEvent_score_items ().get ((away ? 0 : 1)).getScore ();
            line_row [columns_size + 2] = (away ? event_score.getStatus1 () : event_score.getStatus2 ());
            }

        // line_row
        return line_row;
        }
    //----------------------------------------------------------------------------------------
    public void save_button_selected ()
        {
        Linescore new_linescore = new Linescore ();
        //First value is team
        for (int i = 1; i < columns_size + 1; i++)
            {
            Object value = line_score_Table.getValueAt (0, i);
            if (value == null)
                break;

            int score = Integer.parseInt (value.toString ());
            new_linescore.addToAwayLineScore (i - 1, score);
            }
        for (int i = 1; i < columns_size + 1; i++)
            {
            Object value = line_score_Table.getValueAt (1, i);
            if (value == null)
                break;

            int score = Integer.parseInt (value.toString ());
            new_linescore.addToHomeLineScore (i - 1, score);
            }

        boolean update_score = check_totals ();

        if (update_score)
            {
            StringBuilder sql = insert_line_score_into_database (new_linescore.getAwayLineScore (), true);
            sql        .append (insert_line_score_into_database (new_linescore.getHomeLineScore (), false));
            Main.db.executeUpdate (sql);

            if (score_correction)
                {
                StringBuilder score_correction_message = new StringBuilder ("SCORE_CORRECTION>>>\n")
                                                        .append (Linescore.line_score_correction (new_linescore, ol_event.toHDF (), corrected_away_score, corrected_home_score))
                                                        .append ("\n<<<")
                                                        ;
                Main.schedule_client.send (score_correction_message);
                }
            setVisible (false);
            }
        }
//    //----------------------------------------------------------------------------------------
//    private StringBuilder line_score (Linescore new_linescore)
//        {
//        String param_event_date   = ol_event.getDate ().toString ().replaceAll ("-", "");
//        int    param_event_number = ol_event.getNumber ();
//        String away_team          = get_team_name (0);
//        String home_team          = get_team_name (1);
//        int    min_periods        = ol_event.getLeague ().getPeriods ();
//        int    max_periods        = Math.max (new_linescore.getAwayLineScore ().values ().size (), new_linescore.getHomeLineScore ().values ().size ());
//        int    away_last_score    = Utils.parse_int (ol_event.getEvent_score ().getEvent_score_items ().get (0).getScore ());
//        int    home_last_score    = Utils.parse_int (ol_event.getEvent_score ().getEvent_score_items ().get (1).getScore ());
//        String status0            = ol_event.getEvent_score ().getStatus0 ();
//        String status1            = ol_event.getEvent_score ().getStatus1 ();
//        String status2            = ol_event.getEvent_score ().getStatus2 ();
//
//        TreeMap <Integer, PeriodScore> line_scores = new TreeMap <> ();
//        for (Integer period : new_linescore.getAwayLineScore ().keySet ())
//            line_scores.put (period, new PeriodScore (period, new_linescore.getAwayLineScore ().get (period), 0));
//        for (Integer period : new_linescore.getHomeLineScore ().keySet ())
//            {
//            PeriodScore period_score = line_scores.get (period);
//            period_score.home_score = new_linescore.getHomeLineScore ().get (period);
//            }
//
//        StringBuilder response = new StringBuilder (line_score_web_header ())
//                         .append ("\n{")
//                         .append (DateTimeUtils.get_current_pacific_time ())
//                         .append (",")
//                         .append (param_event_date)
//                         .append (",")
//                         .append (param_event_number)
//                         .append (",")
//                         .append (away_team)
//                         .append (",")
//                         .append (home_team)
//                         .append (",")
//                         .append (min_periods)
//                         .append (",")
//                         .append (max_periods)
//                         .append (",")
//                         .append (corrected_away_score)
//                         .append (",")
//                         .append (corrected_home_score)
//                         .append (",")
//                         .append (status0 == null ? "" : status0)
//                         .append (",")
//                         .append (status1)
//                         .append (",")
//                         .append (status2)
//                         ;
//        for (PeriodScore period_score : line_scores.values ())
//            {
//            response.append ("\n{")
//                    .append (period_score.period)
//                    .append (",")
//                    .append (period_score.away_score)
//                    .append (",")
//                    .append (period_score.home_score)
//                    .append ("}")
//                    ;
//            }
//        response.append ("}");
//        return (response);
//        }
//    //---------------------------------------------------------------------------------------------
//    public String get_team_name (int sequence)
//        {
//        Event_Item event_item = ol_event.getEvent_items ().get (sequence);
//        if (event_item != null)
//            {
//            Event_Item_League_Team event_item_league_team = event_item.getEvent_item_league_team ();
//            if (event_item_league_team != null)
//                {
//                League_Team league_team = event_item_league_team.getLeague_team ();
//                if (league_team != null)
//                    {
//                    if (league_team.getName () != null && league_team.getName ().length () > 0)
//                        return (league_team.getName ());
//                    else
//                        return (league_team.getTemp_name ());
//                    }
//                }
//            }
//        return ("");
//        }
//    //---------------------------------------------------------------------------------------------
//    static public StringBuilder line_score_web_header ()
//        {
//        StringBuilder header = new StringBuilder ();
//        header.append ("["
//                        + "line_score_correction"
//                            + "{"
//                            + "updated"
//                            + ",date"
//                            + ",number"
//                            + ",away_team"
//                            + ",home_team"
//                            + ",min_periods"
//                            + ",total_periods"
//                            + ",away_last_score"
//                            + ",home_last_score"
//                            + ",status0"
//                            + ",status1"
//                            + ",status2"
//                            + ",periods"
//                                + "{"
//                                + "period"
//                                + ",away_score"
//                                + ",home_score"
//                                + "}"
//                            + "}"
//                        + "]"
//                        );
//        return (header);
//        }
    //----------------------------------------------------------------------------------------
    public StringBuilder insert_line_score_into_database (TreeMap <Integer, Integer> new_linescore, boolean away)
        {
        selected_source_id = sources_all.get (source_ComboBox.getSelectedIndex ()).getId ();
        TreeMap <Integer, Integer> old_linescore = (away ? linescore.getAwayLineScore () : linescore.getHomeLineScore ());
        int event_item_id = (away ? ol_event.getEvent_items ().get (0).getId () : ol_event.getEvent_items ().get (1).getId ());

        int period = 1;
        StringBuilder sql = new StringBuilder ();
        for (Integer score : new_linescore.values ())
            {
            Integer old_score = old_linescore.get (period);
            if (old_score == null || score.intValue () != old_score.intValue ())
                {
                sql.append (  "IF EXISTS (SELECT event_item_id FROM Event_Item_Line_Score WHERE event_item_id = " + event_item_id + " AND period = " + period + " AND source_id = " + selected_source_id + ")")
                   .append ("\nBEGIN")
                   .append ("\n    UPDATE Event_Item_Line_Score")
                   .append ("\n    SET score = " + score)
                   .append ("\n    WHERE event_item_id = " + event_item_id + " AND period = " + period + " AND source_id = " + selected_source_id)
                   .append ("\nEND")
                   .append ("\nELSE")
                   .append ("\nBEGIN")
                   .append ("\n    INSERT INTO Event_Item_Line_Score (event_item_id, period, source_id, score)")
                   .append ("\n    VALUES (" + event_item_id + ", " + period + ", " + selected_source_id + ", " + score + ")")
                   .append ("\nEND;")
                   ;
                }
            period++;
            }

        Debug.print (sql);
        return (sql);
        }
    //----------------------------------------------------------------------------------------
    public boolean check_totals ()
        {
        int total_away_score = Integer.parseInt (line_score_Table.getValueAt (0, columns_size + 1).toString ());
        int old_away_score   = Integer.parseInt (ol_event.getEvent_score ().getEvent_score_items ().get (0).getScore ());

        if (total_away_score != old_away_score)
            {
            String away_team = ol_event.getEvent_items ().get (0).getEvent_item_league_team ().getLeague_team ().getName ();
            int response = JOptionPane.showConfirmDialog (this, "Total score for " + away_team + " does not match its previous value. Do you want to proceed?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION)
                return true;
            else if (response == JOptionPane.NO_OPTION)
                return false;
            }

        int total_home_score = Integer.parseInt (line_score_Table.getValueAt (1, columns_size + 1).toString());
        int old_home_score   = Integer.parseInt (ol_event.getEvent_score ().getEvent_score_items ().get (1).getScore ());
        if (total_home_score != old_home_score)
            {
            String home_team = ol_event.getEvent_items ().get (1).getEvent_item_league_team ().getLeague_team ().getName ();
            int response = JOptionPane.showConfirmDialog (this, "Total score for " + home_team + " does not match its previous value. Do you want to proceed?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION)
                return true;
            else if (response == JOptionPane.NO_OPTION)
                return false;
            }
        return true;
        }
    //----------------------------------------------------------------------------------------
    public void add_table_selection_listener ()
        {
        // Add row selection listener
        // Add table model listener for cell value changes
        line_score_Table.getModel ().addTableModelListener((TableModelEvent e) ->
            {
            if (updatingTable)
                return; // Ignore updates triggered by the same action

            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column != TableModelEvent.ALL_COLUMNS)
                {
                if (row != -1)
                    {
                    int total_score = 0;
                    for (int i = 1; i < columns_size + 1; i++)
                        {
                        String value = (String) line_score_Table.getValueAt (row, i);
                        if (value != null)
                            {
                            int score = Utils.parse_int (value);
                            total_score += score;
                            }
                        }
                    updatingTable = true;
                    line_score_Table.setValueAt (total_score, row, columns_size + 1);
                    updatingTable = false;
                    }
                }
            });
        }
    //----------------------------------------------------------------------------------------
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
        line_score_Table = new javax.swing.JTable();
        save_Button = new javax.swing.JButton();
        source_ComboBox = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        periods_TextField = new javax.swing.JTextField();
        score_correction_CheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        line_score_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane1.setViewportView(line_score_Table);

        save_Button.setText("Save");
        save_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                save_ButtonActionPerformed(evt);
            }
        });

        source_ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        source_ComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                source_ComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("Source:");

        jLabel2.setText("# of Periods:");

        periods_TextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                periods_TextFieldActionPerformed(evt);
            }
        });

        score_correction_CheckBox.setText("Score CORRECTION");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(source_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(periods_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(score_correction_CheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(save_Button)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(save_Button)
                        .addComponent(source_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(periods_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(score_correction_CheckBox)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void save_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_save_ButtonActionPerformed
    {//GEN-HEADEREND:event_save_ButtonActionPerformed
    save_button_selected ();
    }//GEN-LAST:event_save_ButtonActionPerformed

    private void source_ComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_source_ComboBoxActionPerformed
    {//GEN-HEADEREND:event_source_ComboBoxActionPerformed
    source_selected ();
    }//GEN-LAST:event_source_ComboBoxActionPerformed

    private void periods_TextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_periods_TextFieldActionPerformed
    {//GEN-HEADEREND:event_periods_TextFieldActionPerformed
    periods_textfield_edited ();
    }//GEN-LAST:event_periods_TextFieldActionPerformed

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
            java.util.logging.Logger.getLogger (LinescoreDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (InstantiationException ex)
            {
            java.util.logging.Logger.getLogger (LinescoreDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (IllegalAccessException ex)
            {
            java.util.logging.Logger.getLogger (LinescoreDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
            {
            java.util.logging.Logger.getLogger (LinescoreDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
            }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater (() ->
            {
            LinescoreDialog dialog = new LinescoreDialog (new javax.swing.JDialog (), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable line_score_Table;
    private javax.swing.JTextField periods_TextField;
    private javax.swing.JButton save_Button;
    private javax.swing.JCheckBox score_correction_CheckBox;
    private javax.swing.JComboBox<String> source_ComboBox;
    // End of variables declaration//GEN-END:variables
    }
