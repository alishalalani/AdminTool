package com.scheduletool.repository;

import com.scheduletool.model.EventVenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventVenueRepository extends JpaRepository<EventVenue, Integer> {
    List<EventVenue> findByEventId(Integer eventId);
    List<EventVenue> findByVenueId(Integer venueId);
}

