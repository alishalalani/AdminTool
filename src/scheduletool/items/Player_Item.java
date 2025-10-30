package scheduletool.items;

import gsutils.data.Player;

public class Player_Item extends Player
    {
    private int league_team_player_id;
    private Player player;
    //private Event_Item_League_Team_Player event_item_league_team_player;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public Player_Item ()
        {
        }
    //---------------------------------------------------------------------------------------------
    public Player_Item (Player player)
        {
        this.player = player;
        }
    //---------------------------------------------------------------------------------------------
    public int getLeague_team_player_id ()
        {
        return league_team_player_id;
        }
    //---------------------------------------------------------------------------------------------
    public void setLeague_team_player_id (int league_team_player_id)
        {
        this.league_team_player_id = league_team_player_id;
        }
    //---------------------------------------------------------------------------------------------
    public Player get_Player ()
        {
        return this.player;
        }
    }
