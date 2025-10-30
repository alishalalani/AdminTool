/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduletool.schedule_client;

import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.HDF.League;
import gsutils.HDF.League_Equivalent;
import gsutils.HDF.League_Player;
import gsutils.HDF.League_Prop;
import gsutils.HDF.League_Team;
import gsutils.HDF.League_Team_Player;
import gsutils.HDF.Player;
import gsutils.HDF.Schedule;
import gsutils.HDF.Score;
import gsutils.HDF.Sport;
import gsutils.HDF.Sportsbook;
import gsutils.HDF.parser.HDFparser;
import gsutils.SleepFrame;
import gsutils.Utils;
import gsutils.data.Event;
import gsutils.data.Event_Key;
import gsutils.data.Event_Score;
import gsutils.socket.Communicator;
import gsutils.socket.SocketQueue;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import scheduletool.Main;
import static scheduletool.schedule_client.CommunicationState.category_id;
import static scheduletool.schedule_client.CommunicationState.event_id;
import static scheduletool.schedule_client.CommunicationState.league_team_players;
import static scheduletool.schedule_client.CommunicationState.league_teams;
import static scheduletool.schedule_client.CommunicationState.leagues;

/**
 *
 * @author samla
 */
public class ScheduleClient extends Thread
    {
    //---------------------------------------------------------------------------------------------
    //static public        String      host = "localhost";
    static public        String      host = "samlalani.freeddns.org";
    static public String             host_prod = "odds.oddslogic.com";
    //static public String             host_dev  = "192.168.1.74";
    //static public String             host_dev  = "localhost";
    static public String             host_dev  = "samlalani.freeddns.org";
    //static private final int         port = 50001;
    static private final int         port = 50006;
    //---------------------------------------------------------------------------------------------
    private final String             name;
    private       Socket             socket;
    private       Communicator       communicator;
    private       Thread             communicator_thread;
    private       StringBuilder      buffer        = new StringBuilder ();
    private       CommunicationState state         = CommunicationState.initialize;
    private       Schedule           schedule;
    private       boolean            leagues_obtained = false;
    private       boolean            sports_obtained = false;
    private       boolean            sportsbooks_obtained = false;
    private       boolean            schedule_obtained = false;
    private       boolean            scores_obtained = false;
    private       boolean            lines_obtained = false;
    private       boolean            league_equivalents_obtained = false;
    private       boolean            league_teams_obtained = false;
    private       boolean            players_obtained = false;
    private       boolean            league_players_obtained = false;
    private       boolean            league_team_players_obtained = false;
    private       boolean            done          = false;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public ScheduleClient ()
        {
        name = "ScheduleToolClient";
        }
    //---------------------------------------------------------------------------------------------
    @Override
    public void run ()
        {
        String function = "ScheduleClient.run (" + name + ")";
        System.out.println (function + ":  top");
        int print_exception = 0;
        boolean print_bottom;
        do
            {
            print_bottom = true;
            try
                {
                Debug.printt ("CONNECTING TO SOCKET (" + host + ") ***********************");
                socket = new Socket (host, port);
                System.out.println (function + ": " + DateTimeUtils.get_current_time_string () + " connected to socket (" + socket + ")");

                communicator = new Communicator (socket, Main.development);
                communicator.setName (name);
                communicator_thread = new Thread (communicator);
                communicator_thread.start ();

                System.out.println (function + ": " + DateTimeUtils.get_current_time_string () + " connected to communicator (" + socket + ")");
                //9998 is the type
                communicator.getOutput_queue ().add (name + "\n9999\n");
                //Main.get_initial_data ();

                while (!done && !Main.isDone () && !communicator.isDone ())
                    {
                    while (communicator.getInput_queue ().available ())
                        process_input (communicator.getInput_queue ().get ());
                    Thread.sleep (500);
                    }

                if (!communicator.isDone ())
                    {
                    communicator.getOutput_queue ().add ("QUIT\n");
                    communicator.setDone (true, function);
                    }
                Thread.sleep (2000);
                communicator.close ();
                }
            catch (IOException | InterruptedException e)
                {
                String e_string = e.toString ();
                if (e_string.contains ("Connection refused"))
                    {
                    if ((++print_exception) % 10 == 1)
                        Utils.process_exception (function, e);
                    else
                        print_bottom = false;
                    }
                try
                    {
                    sleep (1000);
                    }
                catch (InterruptedException ex)
                    {
                    Utils.process_exception (function + " (InterruptedException)", e);
                    }
                }

            if (print_bottom)
                {
                System.out.println (function + ":  bottom of while, Main.done " + Main.isDone ());
                Utils.memory_usage (function);
                }

            } while (!done && !Main.isDone ());

        System.out.println (function + ":  bottom");
        Utils.memory_usage (function);
        }
    //---------------------------------------------------------------------------------------------
    protected void process_input (Object input)
        {
        String function = "ScheduleClient.process_input";
//        String input = getInput_queue ().get ();
        //System.out.println (function + ":  state (" + state + ") received (" + input + ")");
        switch (state)
            {
            case initialize:
                state = CommunicationState.waiting;
                String response = (String) input;
                System.out.println (function + ": " + DateTimeUtils.get_current_time_string () + " response (" + response + ")");
                break;

            case waiting:
                switch ((String) input)
                    {
                    case "QUIT":
                        communicator.setDone (true, function);
                        break;
                    case "RESTART":
                        SleepFrame.show_message (function, "*** Restarting ***");
                        System.exit (1);
                        break;
                    case "SPORTS>>>":
                        state = CommunicationState.sports;
                        SleepFrame.show_message (function, "Getting HDF sports");
                        break;
                    case "LEAGUES>>>":
                        state = CommunicationState.leagues;
                        SleepFrame.show_message (function, "Getting HDF leagues");
                        break;
                    case "SPORTSBOOKS>>>":
                        state = CommunicationState.sportsbooks;
                        SleepFrame.show_message (function, "Getting HDF sportsbooks");
                        break;
                    case "SCHEDULE>>>":
                        state = CommunicationState.schedule;
                        SleepFrame.show_message (function, "Getting HDF schedule");
                        break;
                    case "SCORES>>>":
                        state = CommunicationState.scores;
                        SleepFrame.show_message (function, "Getting HDF scores");
                        break;
                    case "LEAGUE_EQUIVALENTS>>>":
                        state = CommunicationState.league_equivalents;
                        SleepFrame.show_message (function, "Getting HDF league equivalents");
                        break;
                    case "LEAGUE_TEAMS>>>":
                        state = CommunicationState.league_teams;
                        SleepFrame.show_message (function, "Getting HDF league teams");
                        break;
                    case "LEAGUE_PROPS>>>":
                        state = CommunicationState.league_props;
                        SleepFrame.show_message (function, "Getting HDF league props");
                        break;
                    case "LEAGUE_PLAYERS>>>":
                        state = CommunicationState.league_players;
                        SleepFrame.show_message (function, "Getting HDF league players");
                        break;
                    case "PLAYERS>>>":
                        state = CommunicationState.players;
                        SleepFrame.show_message (function, "Getting HDF players");
                        break;
                    case "LEAGUE_TEAM_PLAYERS>>>":
                        state = CommunicationState.league_team_players;
                        SleepFrame.show_message (function, "Getting HDF league team players");
                        break;

                    case "CATEGORY_ID>>>":
                        state = CommunicationState.category_id;
                        //SleepFrame.show_message (function, "Getting HDF categories for team");
                        break;
                    case "EVENT_ID>>>":
                        state = CommunicationState.event_id;
                        //SleepFrame.show_message (function, "Getting HDF categories for team");
                        break;
                    case "EVENT_ITEM_LEAGUE_TEAM_PLAYER_ID>>>":
                        state = CommunicationState.event_item_league_team_player_id;
                        //SleepFrame.show_message (function, "Getting HDF categories for team");
                        break;
                    default:
                        //System.out.println (function + ":  other value (" + input + ")");
                    }
                break;

            case sportsbooks:
                if (input.equals ("<<<"))
                    process_sportsbooks ();
                else
                    add_to_buffer (input);
                break;

            case sports:
                if (input.equals ("<<<"))
                    process_sports ();
                else
                    add_to_buffer (input);
                break;

            case leagues:
                if (input.equals ("<<<"))
                    process_leagues ();
                else
                    add_to_buffer (input);
                break;
            case league_equivalents:
                if (input.equals ("<<<"))
                    process_league_equivalents ();
                else
                    add_to_buffer (input);
                break;
            case league_teams:
                if (input.equals ("<<<"))
                    process_league_teams ();
                else
                    add_to_buffer (input);
                break;
            case league_props:
                if (input.equals ("<<<"))
                    process_league_props ();
                else
                    add_to_buffer (input);
                break;
            case players:
                if (input.equals ("<<<"))
                    process_players ();
                else
                    add_to_buffer (input);
                break;
            case league_players:
                if (input.equals ("<<<"))
                    process_league_players ();
                else
                    add_to_buffer (input);
                break;
            case league_team_players:
                if (input.equals ("<<<"))
                    process_league_team_players ();
                else
                    add_to_buffer (input);
                break;
            case schedule:
                if (input.equals ("<<<"))
                    process_schedule ();
                else
                    add_to_buffer (input);
                break;

            case scores:
                if (input.equals ("<<<"))
                    process_scores ();
                else
                    add_to_buffer (input);
                break;

            case category_id:
                if (input.equals ("<<<"))
                    process_category_id ();
                else
                    add_to_buffer (input);
                break;
            case event_id:
                if (input.equals ("<<<"))
                    process_event_id ();
                else
                    add_to_buffer (input);
                break;
            case event_item_league_team_player_id:
                if (input.equals ("<<<"))
                    {
                    process_event_item_league_team_player_id ();
                    }
                else
                    add_to_buffer (input);
                break;
            default:
                break;
            }
        }
    //---------------------------------------------------------------------------------------------
    public void send (StringBuilder buffer)
        {
        send (buffer.toString ());
        }
    //---------------------------------------------------------------------------------------------
    public void send (String buffer)
        {
        Debug.printt ("Sending:" + buffer);
        SocketQueue output_queue = communicator.getOutput_queue ();
        output_queue.add (buffer + "\n");
        }
    //---------------------------------------------------------------------------------------------
    public boolean isDone ()
        {
        return done;
        }
    //---------------------------------------------------------------------------------------------
    public void setDone (boolean done)
        {
        this.done = done;
        }
    //---------------------------------------------------------------------------------------------
    static public void setHost (String host)
        {
        ScheduleClient.host = host;
        }
    //---------------------------------------------------------------------------------------------
    public boolean isLeagues_obtained ()
        {
        return leagues_obtained;
        }
    //---------------------------------------------------------------------------------------------
    private void add_to_buffer (Object input)
        {
        buffer.append ((String) input)
              .append ("\n")
              ;
        }
    //---------------------------------------------------------------------------------------------
    private void process_category_id ()
        {
        String function = "ScheduleClient.process_category_id";
       // SleepFrame.show_message (function, "Processing HDF category id");
        state = CommunicationState.waiting;
       // HDFparser parser = parse_data ("player_id");

        //player id, league player, league team player id
        String buffer_string = buffer + "";
        String id_parts [] = buffer_string.split("\n");

       // System.out.println ("category buffer: (" + buffer_string + ")");

        //buffer.setLength (0);
        //Utils.write_to_file("data/hdf_players.txt, buffer);
        buffer = new StringBuilder ();
        //Player.convert_from_hdf (parser);
        //players_obtained = true;
       // SleepFrame.show_message (function, "Done processing HDF categories");

        Main.categories_frame.display_new_category(Integer.parseInt(id_parts[0]));
        }
    //---------------------------------------------------------------------------------------------
    private void process_event_id ()
        {
        System.out.println ("IN PROCESS EVENT ID");
        String function = "ScheduleClient.process_event_id";
       // SleepFrame.show_message (function, "Processing HDF category id");
        state = CommunicationState.waiting;
       // HDFparser parser = parse_data ("player_id");

        //player id, league player, league team player id
        String buffer_string = buffer + "";
        String id_parts [] = buffer_string.split(",");


        System.out.println ("event buffer: (" + buffer_string + ")");
        for (int i =0; i < id_parts.length; i++)
            {
            System.out.println ("(" + id_parts[i] + ")");
            }

        //buffer.setLength (0);
        //Utils.write_to_file("data/hdf_players.txt, buffer);
        buffer = new StringBuilder ();
        //Player.convert_from_hdf (parser);
        //players_obtained = true;
       // SleepFrame.show_message (function, "Done processing HDF categories");
        int event_id = Integer.parseInt(id_parts[0]);
        int away_event_item_id = Integer.parseInt(id_parts[1]);
        String home_id_part = id_parts[2].replaceAll("\n", "");
        System.out.println ("home id part: (" + home_id_part + ")");
        int home_event_item_id = Integer.parseInt(home_id_part);
        Main.categories_frame.display_new_event(event_id, away_event_item_id, home_event_item_id);
        }

//---------------------------------------------------------------------------------------------
    private void process_event_item_league_team_player_id ()
        {
        //System.out.println ("IN PROCESS EVENT ID");
        String function = "ScheduleClient.process_event_item_league_team_player_id";
       // SleepFrame.show_message (function, "Processing HDF category id");
        state = CommunicationState.waiting;
       // HDFparser parser = parse_data ("player_id");

        //player id, league player, league team player id
        String buffer_string = buffer + "";
        System.out.println ("event_item_league_team_player_id buffer: (" + buffer_string + ")");
        String id_parts [] = buffer_string.split("\n");

//        for (int i =0; i < id_parts.length; i++)
//            {
//            System.out.println ("(" + id_parts[i] + ")");
//            }

        //buffer.setLength (0);
        //Utils.write_to_file("data/hdf_players.txt, buffer);
        buffer = new StringBuilder ();
        //Player.convert_from_hdf (parser);
        //players_obtained = true;
       // SleepFrame.show_message (function, "Done processing HDF categories");
        int event_item_league_team_player_id = Integer.parseInt(id_parts[0]);
        //int away_event_item_id = Integer.parseInt(id_parts[1]);
        //String home_id_part = id_parts[2].replaceAll("\n", "");
        //System.out.println ("pitcher id part: (" + home_id_part + ")");
        //int home_event_item_id = Integer.parseInt(home_id_part);
//        Main.categories_frame.set_event_item_league_team_player_id (event_item_league_team_player_id);
        }
//---------------------------------------------------------------------------------------------
    private void process_sportsbooks ()
        {
        String function = "ScheduleClient.process_sportsbooks";

        //SleepFrame.show_message (function, "Processing HDF sportsbooks");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("sportsbooks");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        Sportsbook.convert_from_hdf (parser);
        SleepFrame.show_message (function, "Done processing HDF sportsbooks");
        sportsbooks_obtained = true;
        //Main.loading_frame.set_label ("sportsbooks");
        }
//---------------------------------------------------------------------------------------------
    private void process_sports ()
        {
        String function = "ScheduleClient.process_sports";

        SleepFrame.show_message (function, "Processing HDF sports");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("sports");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        Sport.convert_from_hdf (parser);
        SleepFrame.show_message (function, "Done processing HDF sports");
        sports_obtained = true;
        //Main.loading_frame.set_label ("sports");
        }
//---------------------------------------------------------------------------------------------
    private void process_leagues ()
        {
        String function = "ScheduleClient.process_leagues";

        //SleepFrame.show_message (function, "Processing HDF leagues");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("leagues");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        League.convert_from_hdf (parser);
        leagues_obtained = true;
       // Main.get_initial_data ();
        //Main.loading_frame.set_label ("leagues");
//        for (League_Equivalent league_equivalent : League_Equivalent.getLeague_equivalents_by_name ().values ())
//            league_equivalent.setHDF_league (League.get_league (league_equivalent.getLeague ().getId ()));

        //SleepFrame.show_message (function, "Done processing HDF leagues - number of leagues " + League.size ());
        if (Main.categories_frame != null)
            {
            Debug.print ("initializing leagues");
            //Main.categories_frame.initialize_leagues (League.getLeagues ());
            //Main.schedule_client.send("GET_INJURY_MESSAGES");
            }
        else
            {
            System.out.println ("***************************MAIN FRAME IS NULL*************************8");
            }
        //connected = true;
        }
    //---------------------------------------------------------------------------------------------
    private void process_schedule ()
        {
        String function = "ScheduleClient.process_schedule";

        SleepFrame.show_message (function, "Processing HDF schedule");
        state = CommunicationState.waiting;
        HDFparser schedule_hdf = parse_data ("schedule");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        schedule = Schedule.convert_from_hdf (schedule_hdf);
       // Main.setDone (true);
        //if (Main.categories_frame != null)
         //   Main.categories_frame.schedule_received_initialize_frame ();
        SleepFrame.show_message (function, "Done processing HDF schedule");

        schedule_obtained = true;
       // Main.loading_frame.set_label ("schedule");
        }
    //---------------------------------------------------------------------------------------------
    private void process_scores ()
        {
        String function = "ScheduleClient.process_scores";
        Debug.printt (buffer);
        if (Main.schedule == null)
            return;
        SleepFrame.show_message (function, "Processing HDF scores");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("scores");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        //updates schedule and everything
        ArrayList <gsutils.HDF.Score> array = Score.convert_from_hdf (Main.schedule, parser);
        //ArrayList <gsutils.HDF.Score> array = gsutils.HDF.Score.get_hdf_array (parser);
        for (gsutils.HDF.Score score : array)
            {
            LocalDate event_date = score.getEvent_date ();
            int event_number     = score.getEvent_number ();

//if (event_number == 101)
//    Debug.print ("debug");
            //event is updated
            Event event = Main.schedule.getEvents_by_date_and_number ().get (new Event_Key (event_date, event_number));

            if (Main.categories_frame != null && event != null)
                {
                Event_Score event_score = event.getEvent_score ();
                Main.categories_frame.update_score_on_screen (event, event_score);
                }
            }
        SleepFrame.show_message (function, "Done processing HDF scores");
        scores_obtained = true;

        //Main.loading_frame.set_label ("scores");
        }
    //---------------------------------------------------------------------------------------------
    private void process_league_equivalents ()
        {
        String function = "ScheduleClient.process_league_teams";
        Debug.printt ("top");
        //SleepFrame.show_message (function, "Processing HDF league_teams");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("league_equivalents");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        League_Equivalent.convert_from_hdf (parser);
        //league_teams_obtained = true;
        //SleepFrame.show_message (function, "Done processing HDF league teams");
        if (Main.categories_frame != null)
            {
            //Debug.print ("initializing league teams");
            //Main.main_frame.initialize
            //Main.main_frame.team_show_card (0);
            //Main.main_frame.league_selected();

            //FILL TEAMS
            //Main.main_frame.load_teams();
            }
        league_equivalents_obtained = true;
        //Main.loading_frame.set_label ("league_equivalents");
        Debug.printt ("bottom");
        }
    //---------------------------------------------------------------------------------------------
    private void process_league_teams ()
        {
        String function = "ScheduleClient.process_league_teams";
        Debug.printt ("top");
        //SleepFrame.show_message (function, "Processing HDF league_teams");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("league_teams");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        League_Team.convert_from_hdf (parser);
        //league_teams_obtained = true;
        //SleepFrame.show_message (function, "Done processing HDF league teams");
        if (Main.categories_frame != null)
            {
            //Debug.print ("initializing league teams");
            //Main.main_frame.initialize
            //Main.main_frame.team_show_card (0);
            //Main.main_frame.league_selected();

            //FILL TEAMS
            //Main.main_frame.load_teams();
            }
        league_teams_obtained = true;
        //Main.loading_frame.set_label ("league_teams");
        Debug.printt ("bottom");
        }
    //---------------------------------------------------------------------------------------------
    private void process_league_props ()
        {
        String function = "ScheduleClient.process_league_props";
        Debug.printt ("top");
        //SleepFrame.show_message (function, "Processing HDF league_teams");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("league_props");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        League_Prop.convert_from_hdf (parser);
        //league_teams_obtained = true;
        //SleepFrame.show_message (function, "Done processing HDF league teams");
        if (Main.categories_frame != null)
            {
            //Debug.print ("initializing league teams");
            //Main.main_frame.initialize
            //Main.main_frame.team_show_card (0);
            //Main.main_frame.league_selected();

            //FILL TEAMS
            //Main.main_frame.load_teams();
            }
        Debug.printt ("bottom");
        }

    //---------------------------------------------------------------------------------------------
    private void process_league_team_players ()
        {
        String function = "ScheduleClient.process_league_team_players";
        Debug.printt ("top");
        //SleepFrame.show_message (function, "Processing HDF league team players");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("league_team_players");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        League_Team_Player.convert_from_hdf (parser);
        //league_team_players_obtained = true;
        //SleepFrame.show_message (function, "Done processing HDF league team players");

        if (Main.categories_frame != null)
            {
//            Main.main_frame.player_show_card (0);
//            Main.main_frame.team_selected();
//            if (Main.main_frame.get_selected_team() != null)
//                Main.main_frame.initialize_players (false);
            }
        league_team_players_obtained = true;
        //Main.loading_frame.set_label ("league_team_players");
        Debug.printt ("bottom");
        }
    //---------------------------------------------------------------------------------------------
    private void process_league_players ()
        {
        String function = "ScheduleClient.process_league_players";
        Debug.printt ("top");
       // SleepFrame.show_message (function, "Processing HDF league players");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("league_players");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        League_Player.convert_from_hdf (parser);
        //league_players_obtained = true;
        //SleepFrame.show_message (function, "Done processing HDF league players");
        league_players_obtained = true;
        //Main.loading_frame.set_label ("league_players");
        Debug.printt ("bottom");
        }
    //---------------------------------------------------------------------------------------------
    private void process_players ()
        {
        String function = "ScheduleClient.process_players";
        Debug.printt ("top");
        //SleepFrame.show_message (function, "Processing HDF players");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("players");
        //buffer.setLength (0);
        Utils.write_to_file("data/hdf_players.txt", buffer);
        buffer = new StringBuilder ();
        Player.convert_from_hdf (parser);
        //players_obtained = true;
        //SleepFrame.show_message (function, "Done processing HDF players");
        players_obtained = true;
        //Main.loading_frame.set_label ("players");
        Debug.printt ("bottom");
        }
    //---------------------------------------------------------------------------------------------
    private HDFparser parse_data (String name)
        {
        String function = "ScheduleClient.parse_data";
        HDFparser parser = new HDFparser (buffer, true);
        parser.parse ();
        return (parser);
        }
//---------------------------------------------------------------------------------------------
    public boolean isSports_obtained ()
        {
        return sports_obtained;
        }
//---------------------------------------------------------------------------------------------
    public boolean isSportsbooks_obtained ()
        {
        return sportsbooks_obtained;
        }
//---------------------------------------------------------------------------------------------
    public boolean isSchedule_obtained ()
        {
        return schedule_obtained;
        }
//---------------------------------------------------------------------------------------------
    public boolean isScores_obtained ()
        {
        return scores_obtained;
        }
//---------------------------------------------------------------------------------------------
    public boolean isLeague_equivalents_obtained ()
        {
        return league_equivalents_obtained;
        }
//---------------------------------------------------------------------------------------------
    public boolean isLeague_teams_obtained ()
        {
        return league_teams_obtained;
        }
//---------------------------------------------------------------------------------------------
    public boolean isPlayers_obtained ()
        {
        return players_obtained;
        }
//---------------------------------------------------------------------------------------------
    public boolean isLeague_players_obtained ()
        {
        return league_players_obtained;
        }
//---------------------------------------------------------------------------------------------
    public boolean isLeague_team_players_obtained ()
        {
        return league_team_players_obtained;
        }
//---------------------------------------------------------------------------------------------
    }
