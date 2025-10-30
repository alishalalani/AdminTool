package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * ParticipantLeagueTeam Entity
 * Links participants to league teams
 */
@Entity
@Table(name = "participant_league_team")
public class ParticipantLeagueTeam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "participant_id", nullable = false)
    private Integer participantId;
    
    @Column(name = "league_team_id", nullable = false)
    private Integer leagueTeamId;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    // Constructors
    public ParticipantLeagueTeam() {
    }
    
    public ParticipantLeagueTeam(Integer participantId, Integer leagueTeamId) {
        this.participantId = participantId;
        this.leagueTeamId = leagueTeamId;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getParticipantId() {
        return participantId;
    }
    
    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
    }
    
    public Integer getLeagueTeamId() {
        return leagueTeamId;
    }
    
    public void setLeagueTeamId(Integer leagueTeamId) {
        this.leagueTeamId = leagueTeamId;
    }
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ParticipantLeagueTeam{" +
                "id=" + id +
                ", participantId=" + participantId +
                ", leagueTeamId=" + leagueTeamId +
                '}';
    }
}

