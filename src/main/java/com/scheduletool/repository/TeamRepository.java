package com.scheduletool.repository;

import com.scheduletool.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findByActiveTrue();
    Team findByName(String name);
    Team findByAbbreviation(String abbreviation);
    List<Team> findByCity(String city);
}

