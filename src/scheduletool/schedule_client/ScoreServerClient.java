/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scheduletool.schedule_client;

import scheduletool.Main;
import gsutils.DateTimeUtils;
import gsutils.Debug;
import gsutils.HDF.League;
import gsutils.HDF.Schedule;
import gsutils.HDF.Score;
import gsutils.HDF.Sport;
import gsutils.HDF.Sportsbook;
import gsutils.HDF.parser.HDFparser;
import gsutils.SleepFrame;
import gsutils.Utils;
import gsutils.data.League_Equivalent;
import gsutils.socket.Communicator;
import gsutils.socket.SocketQueue;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.Socket;

/**
 *
 * @author samla
 */
public class ScoreServerClient extends Thread
    {
    static public  final int SCORE_SERVER_PORT = 50010;
    static private String host = "localhost";
    //---------------------------------------------------------------------------------------------
    private final String             name;
//    private final String             host = "samlalani.freeddns.org";
    private final int                port;
    private       Socket             socket;
    private       Communicator       communicator;
    private       Thread             communicator_thread;
    private       Schedule           schedule;
    private       StringBuilder      buffer         = new StringBuilder ();
    private       CommunicationState state          = CommunicationState.initialize;
    private       boolean            lines_obtained = false;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public ScoreServerClient ()
        {
        name = "ScheduleTool";
        port = SCORE_SERVER_PORT;
        }
    //---------------------------------------------------------------------------------------------
    static public void setHost (String host)
        {
        ScoreServerClient.host = host;
        }
    //---------------------------------------------------------------------------------------------
    @Override
    public void run ()
        {
        String function = "ScoreServerClient.run (" + name + ")";
        System.out.println (function + ":  top");
        int print_exception = 0;
        boolean print_bottom;
        do
            {
            print_bottom = true;
            try
                {
                socket = new Socket (host, port);
                System.out.println (function + ": " + DateTimeUtils.get_current_time_string () + " connected to socket (" + socket + ")");

                communicator = new Communicator (socket, Main.development);
                communicator.setName (name);
                communicator_thread = new Thread (communicator);
                communicator_thread.start ();

                System.out.println (function + ": " + DateTimeUtils.get_current_time_string () + " connected to communicator (" + socket + ")");
                communicator.getOutput_queue ().add (name + "\n9998\n");

                while (!Main.isDone () && !communicator.isDone ())
                    {
                    while (communicator.getInput_queue ().available ())
                        process_input (communicator.getInput_queue ().get ());
                    sleep (500);
                    }

                if (!communicator.isDone ())
                    {
                    communicator.getOutput_queue ().add ("QUIT\n");
                    communicator.setDone (true, function);
                    }
                sleep (2000);
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

            } while (!Main.isDone ());

        System.out.println (function + ":  bottom at " + DateTimeUtils.get_current_time_string ());
        Utils.memory_usage (function);
        }
    //---------------------------------------------------------------------------------------------
    protected void process_input (Object input)
        {
        String function = "ScheduleClient.process_input";
//        String input = getInput_queue ().get ();
        //System.out.println (function + ":  received (" + input + ")");
        switch (state)
            {
            case initialize:
                String response = (String) input;
                System.out.println (function + ": " + DateTimeUtils.get_current_time_string () + " response (" + response + ")");
                state = CommunicationState.waiting;
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
                    case "LINES>>>":
                        state = CommunicationState.lines;
                        SleepFrame.show_message (function, "Getting HDF lines");
                        break;
                    default:
                        System.out.println (function + ":  other value (" + input + ")");
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

            case lines:
                if (input.equals ("<<<"))
                    {
                    process_lines ();
                    }
                else
                    add_to_buffer (input);
                break;
            }
        }
    //---------------------------------------------------------------------------------------------
    private void add_to_buffer (Object input)
        {
        buffer.append ((String) input)
              .append ("\n")
              ;
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
    private void process_schedule ()
        {
        String function = "ScheduleClient.process_schedule";

        SleepFrame.show_message (function, "Processing HDF schedule");
        state = CommunicationState.waiting;
        HDFparser schedule_hdf = parse_data ("schedule");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        schedule = Schedule.convert_from_hdf (schedule_hdf);
        //Main.setDone (true);
        SleepFrame.show_message (function, "Done processing HDF schedule");
        }
    //---------------------------------------------------------------------------------------------
    private void process_scores ()
        {
        String function = "ScheduleClient.process_scores";

        SleepFrame.show_message (function, "Processing HDF scores");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("scores");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        Score.convert_from_hdf (schedule, parser);
        SleepFrame.show_message (function, "Done processing HDF scores");
        }
    //---------------------------------------------------------------------------------------------
    private void process_lines ()
        {
        String function = "ScheduleClient.process_lines";
        SleepFrame.show_message (function, "Processing HDF lines");
        state = CommunicationState.waiting;
        lines_obtained = true;
        SleepFrame.show_message (function, "Done processing HDF lines");
        }
    //---------------------------------------------------------------------------------------------
    private void process_sportsbooks ()
        {
        String function = "ScheduleClient.process_sportsbooks";

        SleepFrame.show_message (function, "Processing HDF sportsbooks");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("sportsbooks");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        Sportsbook.convert_from_hdf (parser);
        SleepFrame.show_message (function, "Done processing HDF sportsbooks");
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
        }
    //---------------------------------------------------------------------------------------------
    private void process_leagues ()
        {
        String function = "ScheduleClient.process_leagues";

        SleepFrame.show_message (function, "Processing HDF leagues");
        state = CommunicationState.waiting;
        HDFparser parser = parse_data ("leagues");
        //buffer.setLength (0);
        buffer = new StringBuilder ();
        League.convert_from_hdf (parser);
        for (League_Equivalent league_equivalent : League_Equivalent.getLeague_equivalents_by_name ().values ())
            league_equivalent.setHDF_league (League.get_league (league_equivalent.getLeague ().getId ()));

        SleepFrame.show_message (function, "Done processing HDF leagues - number of leagues " + League.size ());
        }
    //---------------------------------------------------------------------------------------------
    public Schedule getSchedule ()
        {
        return schedule;
        }
    //---------------------------------------------------------------------------------------------
    public boolean isLines_obtained ()
        {
        return lines_obtained;
        }
    //---------------------------------------------------------------------------------------------
    public synchronized void send (StringBuilder buffer)
        {
        SocketQueue output_queue = communicator.getOutput_queue ();
        String buffer_string = buffer.toString ();
        output_queue.add (buffer_string);
        output_queue.add ("\n");
        if (Main.development)
            Debug.print (">>>\n" + buffer_string + "\n<<<");
        }
    //---------------------------------------------------------------------------------------------
    }

