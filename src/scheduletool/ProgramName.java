/*
 * Ctrl-click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Ctrl-click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduletool;

import java.util.TreeMap;

/**
 *
 * @author samla
 */
public class ProgramName
    {
    public static TreeMap <String, ProgramName> map = new TreeMap <> (); // key is internal_name
    //---------------------------------------------------------------------------------------------
    public String viewing_name;
    public String internal_name;
    public String start_time;
    //---------------------------------------------------------------------------------------------
    public ProgramName (String viewing_name, String internal_name, String start_time)
        {
        this.viewing_name  = viewing_name;
        this.internal_name = internal_name;
        this.start_time    = start_time;
        }
    }
