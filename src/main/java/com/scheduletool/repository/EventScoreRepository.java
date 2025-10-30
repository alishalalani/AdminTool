package com.scheduletool.repository;

import com.scheduletool.model.Event;
import com.scheduletool.model.EventScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventScoreRepository extends JpaRepository<EventScore, Integer> {
    List<EventScore> findByEvent(Event event);
    EventScore findTopByEventOrderByTimestampDesc(Event event);
}

