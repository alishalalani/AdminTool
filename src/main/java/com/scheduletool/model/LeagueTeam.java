package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * League_Team Entity (Teams in specific leagues)
 * Replaces gsutils.data.League_Team
 */
@Entity
@Table(name = "league_team")
public class LeagueTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "league_id", nullable = false)
    private Integer leagueId;

    @Column(name = "team_id", nullable = false)
    private Integer teamId;
    
    // Constructors
    public LeagueTeam() {
    }

    public LeagueTeam(Integer leagueId, Integer teamId) {
        this.leagueId = leagueId;
        this.teamId = teamId;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    @Override
    public String toString() {
        return "LeagueTeam{" +
                "id=" + id +
                ", leagueId=" + leagueId +
                ", teamId=" + teamId +
                '}';
    }
}

