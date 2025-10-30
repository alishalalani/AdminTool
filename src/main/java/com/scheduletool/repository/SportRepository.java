package com.scheduletool.repository;

import com.scheduletool.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Sport entity
 * Provides database access methods
 */
@Repository
public interface SportRepository extends JpaRepository<Sport, Integer> {
    
    /**
     * Find all active sports
     */
    List<Sport> findByActiveTrue();
    
    /**
     * Find sport by name
     */
    Sport findByName(String name);
    
    /**
     * Find sport by abbreviation
     */
    Sport findByAbbreviation(String abbreviation);
}

