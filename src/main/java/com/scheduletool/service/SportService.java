package com.scheduletool.service;

import com.scheduletool.model.Sport;
import com.scheduletool.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Sport operations
 * Contains business logic
 */
@Service
@Transactional
public class SportService {
    
    @Autowired
    private SportRepository sportRepository;
    
    /**
     * Get all sports
     */
    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }
    
    /**
     * Get all active sports
     */
    public List<Sport> getActiveSports() {
        return sportRepository.findByActiveTrue();
    }
    
    /**
     * Get sport by ID
     */
    public Optional<Sport> getSportById(Integer id) {
        return sportRepository.findById(id);
    }
    
    /**
     * Get sport by name
     */
    public Sport getSportByName(String name) {
        return sportRepository.findByName(name);
    }
    
    /**
     * Create new sport
     */
    public Sport createSport(Sport sport) {
        return sportRepository.save(sport);
    }
    
    /**
     * Update existing sport
     */
    public Sport updateSport(Integer id, Sport sportDetails) {
        Sport sport = sportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sport not found with id: " + id));
        
        sport.setName(sportDetails.getName());
        sport.setAbbreviation(sportDetails.getAbbreviation());
        sport.setActive(sportDetails.getActive());
        
        return sportRepository.save(sport);
    }
    
    /**
     * Delete sport
     */
    public void deleteSport(Integer id) {
        sportRepository.deleteById(id);
    }
}

