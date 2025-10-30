package com.scheduletool.model;

import javax.persistence.*;

/**
 * Lineserver_Sportsbook Entity (Odds sportsbooks)
 * Replaces gsutils.data.Odds_Sportsbook
 */
@Entity

    

@Table(name = "lineserver_sportsbook")
public class LineserverSportsbook {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "sportsbook_id", nullable = false)
    private Sportsbook sportsbook;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "active")
    private Boolean active;
    
    // Constructors
    public LineserverSportsbook() {
    }
    
    public LineserverSportsbook(Sportsbook sportsbook, String name) {
        this.sportsbook = sportsbook;
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
    
    public Sportsbook getSportsbook() {
        return sportsbook;
    }
    
    public void setSportsbook(Sportsbook sportsbook) {
        this.sportsbook = sportsbook;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return "LineserverSportsbook{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sportsbook=" + (sportsbook != null ? sportsbook.getName() : "null") +
                '}';
    }
}

