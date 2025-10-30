package com.scheduletool.service;

import com.scheduletool.dto.GameDTO;
import com.scheduletool.model.*;
import com.scheduletool.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    
    @Autowired
    private GroupEventRepository groupEventRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ParticipantRepository participantRepository;
    
    @Autowired
    private ParticipantLeagueTeamRepository participantLeagueTeamRepository;
    
    @Autowired
    private LeagueTeamRepository leagueTeamRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EventTimeRepository eventTimeRepository;
    
    /**
     * Get all games for a specific group
     * @param groupId The group ID
     * @return List of GameDTO objects with team and time information
     */
    public List<GameDTO> getGamesByGroupId(Integer groupId) {
        List<GameDTO> games = new ArrayList<>();
        
        // Step 1: Get all event IDs for this group
        List<Integer> eventIds = groupEventRepository.findEventIdsByGroupId(groupId);
        
        // Step 2: For each event, get the event details, participants, and teams
        for (Integer eventId : eventIds) {
            Event event = eventRepository.findById(eventId).orElse(null);
            if (event == null) {
                continue;
            }
            
            GameDTO game = new GameDTO();
            game.setEventId(event.getId());
            game.setDate(event.getDate());
            game.setNumber(event.getNumber());
            
            // Step 3: Get event time
            List<EventTime> eventTimes = eventTimeRepository.findByEventId(eventId);
            if (!eventTimes.isEmpty()) {
                EventTime eventTime = eventTimes.get(0); // Get the first time
                game.setTime(eventTime.getTime());
                game.setTba(eventTime.getTba());
            }
            
            // Step 4: Get participants for this event
            List<Participant> participants = participantRepository.findByEventIdOrderBySortOrder(eventId);
            
            // Step 5: For each participant, get the team information
            for (Participant participant : participants) {
                ParticipantLeagueTeam plt = participantLeagueTeamRepository.findFirstByParticipantId(participant.getId());
                if (plt != null) {
                    LeagueTeam leagueTeam = leagueTeamRepository.findById(plt.getLeagueTeamId()).orElse(null);
                    if (leagueTeam != null) {
                        // Get the actual team name from the team table
                        Team team = teamRepository.findById(leagueTeam.getTeamId()).orElse(null);
                        if (team != null) {
                            // Determine if this is home or away team based on participant.home
                            if (participant.getHome() != null && participant.getHome() == 1) {
                                game.setHomeTeam(team.getName());
                                game.setHomeTeamId(team.getId());
                            } else {
                                game.setAwayTeam(team.getName());
                                game.setAwayTeamId(team.getId());
                            }
                        }
                    }
                }
            }
            
            games.add(game);
        }
        
        return games;
    }
}

