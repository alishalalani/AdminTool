package com.scheduletool.repository;

import com.scheduletool.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByActiveTrue();
    List<Location> findByCity(String city);
    List<Location> findByState(String state);
    List<Location> findByCountry(String country);
    Location findByCityAndState(String city, String state);
}

