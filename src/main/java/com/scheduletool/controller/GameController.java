package com.scheduletool.controller;

import com.scheduletool.dto.GameDTO;
import com.scheduletool.model.EventTime;
import com.scheduletool.model.ParticipantLeagueTeam;
import com.scheduletool.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {
    
    @Autowired
    private GameService gameService;
    
    /**
     * Get all games for a specific group
     * @param groupId The group ID
     * @return List of games with team and time information
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<GameDTO>> getGamesByGroupId(@PathVariable Integer groupId) {
        List<GameDTO> games = gameService.getGamesByGroupId(groupId);
        return ResponseEntity.ok(games);
    }

    /**
     * Get count of games for a specific group
     * @param groupId The group ID
     * @return Count of games in the group
     */
    @GetMapping("/group/{groupId}/count")
    public ResponseEntity<Long> getGameCountByGroupId(@PathVariable Integer groupId) {
        Long count = gameService.getGameCountByGroupId(groupId);
        return ResponseEntity.ok(count);
    }

    /**
     * Update event time
     * @param eventId The event ID
     * @param request Map containing time and tba fields
     * @return Updated EventTime
     */
    @PutMapping("/event/{eventId}/time")
    public ResponseEntity<EventTime> updateEventTime(
            @PathVariable Integer eventId,
            @RequestBody Map<String, Object> request) {
        try {
            OffsetDateTime time = OffsetDateTime.parse((String) request.get("time"));
            Integer tba = (Integer) request.get("tba");
            EventTime updatedTime = gameService.updateEventTime(eventId, time, tba);
            return ResponseEntity.ok(updatedTime);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update participant team
     * @param participantId The participant ID
     * @param request Map containing leagueTeamId
     * @return Updated ParticipantLeagueTeam
     */
    @PutMapping("/participant/{participantId}/team")
    public ResponseEntity<ParticipantLeagueTeam> updateParticipantTeam(
            @PathVariable Integer participantId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer leagueTeamId = request.get("leagueTeamId");
            ParticipantLeagueTeam updated = gameService.updateParticipantTeam(participantId, leagueTeamId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get teams for a specific league
     * @param leagueId The league ID
     * @return List of teams with their league team IDs
     */
    @GetMapping("/league/{leagueId}/teams")
    public ResponseEntity<List<Map<String, Object>>> getTeamsByLeagueId(@PathVariable Integer leagueId) {
        List<Map<String, Object>> teams = gameService.getTeamsByLeagueId(leagueId);
        return ResponseEntity.ok(teams);
    }
}

