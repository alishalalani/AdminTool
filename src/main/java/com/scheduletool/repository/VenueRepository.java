package com.scheduletool.repository;

import com.scheduletool.model.Location;
import com.scheduletool.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Integer> {
    List<Venue> findByActiveTrue();
    Venue findByName(String name);
    List<Venue> findByLocation(Location location);
}

