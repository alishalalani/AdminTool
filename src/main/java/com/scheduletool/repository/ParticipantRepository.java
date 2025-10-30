package com.scheduletool.repository;

import com.scheduletool.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    
    List<Participant> findByEventId(Integer eventId);
    
    List<Participant> findByEventIdOrderBySortOrder(Integer eventId);
}

