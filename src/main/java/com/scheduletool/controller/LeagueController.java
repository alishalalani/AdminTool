package com.scheduletool.controller;

import com.scheduletool.model.League;
import com.scheduletool.service.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leagues")
@CrossOrigin(origins = "*")
public class LeagueController {
    
    @Autowired
    private LeagueService leagueService;
    
    @GetMapping
    public ResponseEntity<List<League>> getAllLeagues() {
        List<League> leagues = leagueService.getAllLeagues();
        return ResponseEntity.ok(leagues);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<League>> getActiveLeagues() {
        List<League> leagues = leagueService.getActiveLeagues();
        return ResponseEntity.ok(leagues);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<League> getLeagueById(@PathVariable Integer id) {
        return leagueService.getLeagueById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<League> createLeague(@RequestBody League league) {
        League createdLeague = leagueService.createLeague(league);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLeague);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<League> updateLeague(@PathVariable Integer id, @RequestBody League league) {
        try {
            League updatedLeague = leagueService.updateLeague(id, league);
            return ResponseEntity.ok(updatedLeague);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeague(@PathVariable Integer id) {
        leagueService.deleteLeague(id);
        return ResponseEntity.noContent().build();
    }
}

