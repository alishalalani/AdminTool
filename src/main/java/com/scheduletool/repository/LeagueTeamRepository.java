package com.scheduletool.repository;

import com.scheduletool.model.League;
import com.scheduletool.model.LeagueTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueTeamRepository extends JpaRepository<LeagueTeam, Integer> {
    List<LeagueTeam> findByLeague(League league);
    List<LeagueTeam> findByLeagueAndActive(League league, Boolean active);
    LeagueTeam findByLeagueAndName(League league, String name);
    List<LeagueTeam> findByActiveTrue();
}

