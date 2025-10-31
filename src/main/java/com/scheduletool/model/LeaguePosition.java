package com.scheduletool.model;

import javax.persistence.*;

/**
 * League_Position Entity (Positions in a league, e.g., QB, RB, etc.)
 * Replaces gsutils.data.League_Position
 */
@Entity
@Table(name = "League_Position")
public class LeaguePosition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "abbreviation", length = 10)
    private String abbreviation;
    
    @Column(name = "sequence")
    private Integer sequence;
    
    @Column(name = "active")
    private Boolean active;
    
    // Constructors
    public LeaguePosition() {
    }
    
    public LeaguePosition(League league, String name) {
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
    
    public Integer getSequence() {
        return sequence;
    }
    
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return "LeaguePosition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                '}';
    }
}

