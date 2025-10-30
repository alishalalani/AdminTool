package com.scheduletool.repository;

import com.scheduletool.model.League;
import com.scheduletool.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League, Integer> {
    List<League> findByActiveTrue();
    List<League> findBySport(Sport sport);
    List<League> findBySportId(Integer sportId);
    List<League> findBySportIdAndActive(Integer sportId, Boolean active);
    List<League> findBySportAndActive(Sport sport, Boolean active);
    League findByName(String name);
}

