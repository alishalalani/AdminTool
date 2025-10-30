package com.scheduletool.controller;

import com.scheduletool.dto.GameDTO;
import com.scheduletool.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}

