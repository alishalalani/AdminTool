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
    
    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "abbreviation", length = 10)
    private String abbreviation;
    
    @Column(name = "rotation_number")
    private Integer rotationNumber;
    
    @Column(name = "active")
    private Boolean active;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    @OneToMany(mappedBy = "leagueTeam", cascade = CascadeType.ALL)
    private List<LeagueTeamPlayer> players;
    
    // Constructors
    public LeagueTeam() {
    }
    
    public LeagueTeam(League league, String name) {
        this.league = league;
        this.name = name;
        this.active = true;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public League getLeague() {
        return league;
    }
    
    public void setLeague(League league) {
        this.league = league;
    }
    
    public Team getTeam() {
        return team;
    }
    
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAbbreviation() {
        return abbreviation;
    }
    
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
    
    public Integer getRotationNumber() {
        return rotationNumber;
    }
    
    public void setRotationNumber(Integer rotationNumber) {
        this.rotationNumber = rotationNumber;
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
    
    public List<LeagueTeamPlayer> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<LeagueTeamPlayer> players) {
        this.players = players;
    }
    
    @Override
    public String toString() {
        return "LeagueTeam{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", league=" + (league != null ? league.getName() : "null") +
                ", rotationNumber=" + rotationNumber +
                '}';
    }
}

