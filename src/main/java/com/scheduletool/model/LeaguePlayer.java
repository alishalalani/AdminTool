package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * League_Player Entity
 * Replaces gsutils.data.League_Player
 */
@Entity
@Table(name = "League_Player")
public class LeaguePlayer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    
    @Column(name = "jersey_number")
    private Integer jerseyNumber;
    
    @Column(name = "active")
    private Boolean active;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    // Constructors
    public LeaguePlayer() {
    }
    
    public LeaguePlayer(League league, Player player) {
        this.league = league;
        this.player = player;
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
    
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
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
        return "LeaguePlayer{" +
                "id=" + id +
                ", player=" + (player != null ? player.getFullName() : "null") +
                ", league=" + (league != null ? league.getName() : "null") +
                ", jerseyNumber=" + jerseyNumber +
                '}';
    }
}

