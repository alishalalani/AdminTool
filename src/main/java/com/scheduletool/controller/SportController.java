package com.scheduletool.controller;

import com.scheduletool.model.Sport;
import com.scheduletool.service.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Sport operations
 * Handles HTTP requests for sports management
 */
@RestController
@RequestMapping("/api/sports")
@CrossOrigin(origins = "*")
public class SportController {
    
    @Autowired
    private SportService sportService;
    
    /**
     * GET /api/sports
     * Get all sports
     */
    @GetMapping
    public ResponseEntity<List<Sport>> getAllSports() {
        List<Sport> sports = sportService.getAllSports();
        return ResponseEntity.ok(sports);
    }
    
    /**
     * GET /api/sports/active
     * Get all active sports
     */
    @GetMapping("/active")
    public ResponseEntity<List<Sport>> getActiveSports() {
        List<Sport> sports = sportService.getActiveSports();
        return ResponseEntity.ok(sports);
    }
    
    /**
     * GET /api/sports/{id}
     * Get sport by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sport> getSportById(@PathVariable Integer id) {
        return sportService.getSportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * POST /api/sports
     * Create new sport
     */
    @PostMapping
    public ResponseEntity<Sport> createSport(@RequestBody Sport sport) {
        Sport createdSport = sportService.createSport(sport);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSport);
    }
    
    /**
     * PUT /api/sports/{id}
     * Update existing sport
     */
    @PutMapping("/{id}")
    public ResponseEntity<Sport> updateSport(@PathVariable Integer id, @RequestBody Sport sport) {
        try {
            Sport updatedSport = sportService.updateSport(id, sport);
            return ResponseEntity.ok(updatedSport);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * DELETE /api/sports/{id}
     * Delete sport
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSport(@PathVariable Integer id) {
        sportService.deleteSport(id);
        return ResponseEntity.noContent().build();
    }
}

