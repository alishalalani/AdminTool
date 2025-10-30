package com.scheduletool.repository;

import com.scheduletool.model.ParticipantLeagueTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantLeagueTeamRepository extends JpaRepository<ParticipantLeagueTeam, Integer> {
    
    List<ParticipantLeagueTeam> findByParticipantId(Integer participantId);
    
    ParticipantLeagueTeam findFirstByParticipantId(Integer participantId);
}

