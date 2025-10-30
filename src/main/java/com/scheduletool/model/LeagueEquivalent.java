package com.scheduletool.model;

import javax.persistence.*;

/**
 * League_Equivalent Entity (Mapping between different league representations)
 * Replaces gsutils.data.League_Equivalent
 */
@Entity

    

@Table(name = "league_equivalent")
public class LeagueEquivalent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    
    @Column(name = "external_id", length = 50)
    private String externalId;
    
    @Column(name = "source", length = 50)
    private String source;
    
    // Constructors
    public LeagueEquivalent() {
    }
    
    public LeagueEquivalent(League league, String externalId, String source) {
        this.league = league;
        this.externalId = externalId;
        this.source = source;
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
    
    public String getExternalId() {
        return externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    @Override
    public String toString() {
        return "LeagueEquivalent{" +
                "id=" + id +
                ", league=" + (league != null ? league.getName() : "null") +
                ", externalId='" + externalId + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}

