/*
 * Ctrl-click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Ctrl-click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduletool.restart_programs;

import gsutils.data.League;

//---------------------------------------------------------------------------------------------
public class RestartLeague extends League implements Comparable
    {
    //---------------------------------------------------------------------------------------------
    public RestartLeague (League league)
        {
        super ();
        setId               (league.getId               ());
        setName             (league.getName             ());
        setFullname         (league.getFullname         ());
        setAbbr             (league.getAbbr             ());
        setSport_id         (league.getSport_id         ());
        setParent_league_id (league.getParent_league_id ());
        setMain_league_id   (league.getMain_league_id   ());
        setRegion_id        (league.getRegion_id        ());
        setLeague_type_id   (league.getLeague_type_id   ());
        setOLdisplay_id     (league.getOLdisplay_id     ());
        setScore_type_id    (league.getScore_type_id    ());
        setHalftime_minutes (league.getHalftime_minutes ());
        setPeriods          (league.getPeriods          ());
        setPeriod_length    (league.getPeriod_length    ());
        setOT_length        (league.getOT_length        ());
        }
    //---------------------------------------------------------------------------------------------
    public String league_value ()
        {
        return (super.toString ());
        }
    //---------------------------------------------------------------------------------------------
    @Override
    public String toString ()
        {
        return (getName ());
        }
    }
