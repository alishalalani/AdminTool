package com.scheduletool.service;

import com.scheduletool.dto.GameDTO;
import com.scheduletool.model.*;
import com.scheduletool.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            game.setLeagueId(event.getLeague() != null ? event.getLeague().getId() : null);

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
                            // Use sort_order to determine home/away: 0 = away, 1 = home
                            boolean isHome = (participant.getSortOrder() != null && participant.getSortOrder() == 1);

                            if (isHome) {
                                game.setHomeTeam(team.getName());
                                game.setHomeTeamId(team.getId());
                                game.setHomeParticipantId(participant.getId());
                            } else {
                                game.setAwayTeam(team.getName());
                                game.setAwayTeamId(team.getId());
                                game.setAwayParticipantId(participant.getId());
                            }
                        }
                    }
                }
            }
            
            games.add(game);
        }
        
        return games;
    }

    /**
     * Get count of games for a specific group
     * @param groupId The group ID
     * @return Count of games in the group
     */
    public Long getGameCountByGroupId(Integer groupId) {
        return groupEventRepository.countByGroupId(groupId);
    }

    /**
     * Update event time
     * @param eventId The event ID
     * @param time The new time
     * @param tba Whether the time is TBA
     * @return Updated EventTime
     */
    public EventTime updateEventTime(Integer eventId, java.time.OffsetDateTime time, Integer tba) {
        List<EventTime> eventTimes = eventTimeRepository.findByEventId(eventId);
        EventTime eventTime;

        if (eventTimes.isEmpty()) {
            // Create new event time
            eventTime = new EventTime();
            eventTime.setEventId(eventId);
        } else {
            // Update existing event time
            eventTime = eventTimes.get(0);
        }

        eventTime.setTime(time);
        eventTime.setTba(tba);
        eventTime.setTimestamp(java.time.OffsetDateTime.now());

        return eventTimeRepository.save(eventTime);
    }

    /**
     * Update participant team
     * @param participantId The participant ID
     * @param leagueTeamId The new league team ID
     * @return Updated ParticipantLeagueTeam
     */
    public ParticipantLeagueTeam updateParticipantTeam(Integer participantId, Integer leagueTeamId) {
        ParticipantLeagueTeam plt = participantLeagueTeamRepository.findFirstByParticipantId(participantId);

        if (plt == null) {
            // Create new participant league team
            plt = new ParticipantLeagueTeam();
            plt.setParticipantId(participantId);
        }

        plt.setLeagueTeamId(leagueTeamId);
        plt.setTimestamp(java.time.OffsetDateTime.now());

        return participantLeagueTeamRepository.save(plt);
    }

    /**
     * Get teams for a specific league
     * @param leagueId The league ID
     * @return List of teams with their league team IDs
     */
    public List<Map<String, Object>> getTeamsByLeagueId(Integer leagueId) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get all league teams for this league
        List<LeagueTeam> leagueTeams = leagueTeamRepository.findByLeagueId(leagueId);

        for (LeagueTeam leagueTeam : leagueTeams) {
            Team team = teamRepository.findById(leagueTeam.getTeamId()).orElse(null);
            if (team != null) {
                Map<String, Object> teamData = new HashMap<>();
                teamData.put("leagueTeamId", leagueTeam.getId());
                teamData.put("teamId", team.getId());
                teamData.put("teamName", team.getName());
                result.add(teamData);
            }
        }

        return result;
    }
}

