/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduletool.table;

import java.time.LocalDate;

/**
 *
 * @author samla
 */
public class RecordKey implements Comparable
    {
    int       sport_id;
    int       league_id;
    int       category_id;
    LocalDate category_date;
    LocalDate event_date;
    int       event_number;
    int       event_item_sequence;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public RecordKey (int sport_id, int league_id, int category_id, LocalDate category_date, LocalDate event_date, int event_number, int event_item_sequence)
        {
        this.sport_id            = sport_id;
        this.league_id           = league_id;
        this.category_id         = category_id;
        this.category_date       = category_date;
        this.event_date          = event_date;
        this.event_number        = event_number;
        this.event_item_sequence = event_item_sequence;
        }
    //---------------------------------------------------------------------------------------------
    @Override
    public int compareTo (Object o)
        {
        RecordKey other = (RecordKey) o;
        if (category_id > other.category_id)
            return (1);
        if (category_id < other.category_id)
            return (-1);
        if (category_date.compareTo (other.category_date) > 0)
            return (1);
        if (category_date.compareTo (other.category_date) < 0)
            return (-1);
        if (event_date.compareTo (other.event_date) > 0)
            return (1);
        if (event_date.compareTo (other.event_date) < 0)
            return (-1);
        if (event_number > other.event_number)
            return (1);
        if (event_number < other.event_number)
            return (-1);
        if (event_item_sequence > other.event_item_sequence)
            return (1);
        if (event_item_sequence < other.event_item_sequence)
            return (-1);
        return (0);
        }
    }
