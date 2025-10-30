package com.scheduletool.controller;

import com.scheduletool.model.EventScore;
import com.scheduletool.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {
    
    @Autowired
    private ScoreService scoreService;
    
    @GetMapping
    public ResponseEntity<List<EventScore>> getAllScores() {
        List<EventScore> scores = scoreService.getAllScores();
        return ResponseEntity.ok(scores);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventScore> getScoreById(@PathVariable Integer id) {
        return scoreService.getScoreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<EventScore> createScore(@RequestBody EventScore eventScore) {
        EventScore createdScore = scoreService.createScore(eventScore);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdScore);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EventScore> updateScore(@PathVariable Integer id, @RequestBody EventScore eventScore) {
        try {
            EventScore updatedScore = scoreService.updateScore(id, eventScore);
            return ResponseEntity.ok(updatedScore);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScore(@PathVariable Integer id) {
        scoreService.deleteScore(id);
        return ResponseEntity.noContent().build();
    }
}

