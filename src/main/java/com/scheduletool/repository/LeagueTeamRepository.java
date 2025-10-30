package com.scheduletool.repository;

import com.scheduletool.model.LeagueTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueTeamRepository extends JpaRepository<LeagueTeam, Integer> {
    List<LeagueTeam> findByLeagueId(Integer leagueId);
    List<LeagueTeam> findByTeamId(Integer teamId);
}

