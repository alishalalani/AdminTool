package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * League Entity
 * Replaces gsutils.data.League
 */
@Entity
 
    

@Table(name = "league")
public class League {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "abbreviation", length = 20)
    private String abbreviation;
    
    @Column(name = "active")
    private Boolean active;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    private List<LeagueTeam> teams;
    
    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    private List<Category> categories;
    
    // Constructors
    public League() {
    }
    
    public League(Sport sport, String name) {
        this.sport = sport;
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
    
    public Sport getSport() {
        return sport;
    }
    
    public void setSport(Sport sport) {
        this.sport = sport;
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
    
    public List<LeagueTeam> getTeams() {
        return teams;
    }
    
    public void setTeams(List<LeagueTeam> teams) {
        this.teams = teams;
    }
    
    public List<Category> getCategories() {
        return categories;
    }
    
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
    @Override
    public String toString() {
        return "League{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", sport=" + (sport != null ? sport.getName() : "null") +
                ", active=" + active +
                '}';
    }
}

