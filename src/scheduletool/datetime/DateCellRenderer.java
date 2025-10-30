/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduletool.datetime;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class DateCellRenderer extends JLabel implements TableCellRenderer
    {
    Color old_foreground = null;
    Color old_background = null;
    //---------------------------------------------------------------------------------------------
    public DateCellRenderer ()
        {
        setOpaque (true);
        }
    //---------------------------------------------------------------------------------------------
    @Override
    public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
        String function = "DateCellRenderer.getTableCellRendererComponent";
        if (old_foreground == null)
            old_foreground = getForeground ();
        if (old_background == null)
            old_background = getBackground ();

        //System.out.println (function + ":  value (" + text + ")(" + getForeground () + ")(" + getBackground () + ")");
        String text = (String) value;
        setText (text);
        if (isSelected)
            {
            setForeground (Color.WHITE);
            setBackground (Color.BLUE);
            }
        else
            {
            setForeground (old_foreground);
            setBackground (old_background);
            }
        setSize (30,30);
        setHorizontalAlignment (SwingConstants.CENTER);
        setVerticalAlignment   (SwingConstants.CENTER);
        setFont (new Font ("Segoe UI", Font.BOLD, 18));
        return this;
        }
    //---------------------------------------------------------------------------------------------
    static public int height ()
        {
        return (30);
        }
    }
