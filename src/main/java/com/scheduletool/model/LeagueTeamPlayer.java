package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * League_Team_Player Entity
 * Replaces gsutils.data.League_Team_Player
 */
@Entity

@Table(name = "league_team_player")
public class LeagueTeamPlayer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "league_team_id", nullable = false)
    private LeagueTeam leagueTeam;
    
    @ManyToOne
    @JoinColumn(name = "league_player_id", nullable = false)
    private LeaguePlayer leaguePlayer;
    
    @ManyToOne
    @JoinColumn(name = "league_position_id")
    private LeaguePosition leaguePosition;
    
    @Column(name = "jersey_number")
    private Integer jerseyNumber;
    
    @Column(name = "active")
    private Boolean active;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    // Constructors
    public LeagueTeamPlayer() {
    }
    
    public LeagueTeamPlayer(LeagueTeam leagueTeam, LeaguePlayer leaguePlayer) {
        this.leagueTeam = leagueTeam;
        this.leaguePlayer = leaguePlayer;
        this.active = true;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public LeagueTeam getLeagueTeam() {
        return leagueTeam;
    }
    
    public void setLeagueTeam(LeagueTeam leagueTeam) {
        this.leagueTeam = leagueTeam;
    }
    
    public LeaguePlayer getLeaguePlayer() {
        return leaguePlayer;
    }
    
    public void setLeaguePlayer(LeaguePlayer leaguePlayer) {
        this.leaguePlayer = leaguePlayer;
    }
    
    public LeaguePosition getLeaguePosition() {
        return leaguePosition;
    }
    
    public void setLeaguePosition(LeaguePosition leaguePosition) {
        this.leaguePosition = leaguePosition;
    }
    
    public Integer getJerseyNumber() {
        return jerseyNumber;
    }
    
    public void setJerseyNumber(Integer jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "LeagueTeamPlayer{" +
                "id=" + id +
                ", jerseyNumber=" + jerseyNumber +
                '}';
    }
}

