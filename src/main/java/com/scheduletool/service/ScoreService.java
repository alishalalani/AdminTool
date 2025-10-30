package com.scheduletool.service;

import com.scheduletool.model.Event;
import com.scheduletool.model.EventScore;
import com.scheduletool.repository.EventScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScoreService {
    
    @Autowired
    private EventScoreRepository eventScoreRepository;
    
    public List<EventScore> getAllScores() {
        return eventScoreRepository.findAll();
    }
    
    public Optional<EventScore> getScoreById(Integer id) {
        return eventScoreRepository.findById(id);
    }
    
    public List<EventScore> getScoresByEvent(Event event) {
        return eventScoreRepository.findByEvent(event);
    }
    
    public EventScore getLatestScoreByEvent(Event event) {
        return eventScoreRepository.findTopByEventOrderByTimestampDesc(event);
    }
    
    public EventScore createScore(EventScore eventScore) {
        return eventScoreRepository.save(eventScore);
    }
    
    public EventScore updateScore(Integer id, EventScore scoreDetails) {
        EventScore eventScore = eventScoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Score not found with id: " + id));
        
        eventScore.setEvent(scoreDetails.getEvent());
        eventScore.setSource(scoreDetails.getSource());
        
        return eventScoreRepository.save(eventScore);
    }
    
    public void deleteScore(Integer id) {
        eventScoreRepository.deleteById(id);
    }
}

