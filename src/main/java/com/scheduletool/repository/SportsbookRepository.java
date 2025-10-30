package com.scheduletool.repository;

import com.scheduletool.model.Sportsbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportsbookRepository extends JpaRepository<Sportsbook, Integer> {
    List<Sportsbook> findByActiveTrue();
    Sportsbook findByName(String name);
    Sportsbook findByAbbreviation(String abbreviation);
}

