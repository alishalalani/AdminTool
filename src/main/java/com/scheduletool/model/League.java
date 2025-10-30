package com.scheduletool.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created")
    private OffsetDateTime created;

    @JsonIgnore
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
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
                ", sport=" + (sport != null ? sport.getName() : "null") +
                ", active=" + active +
                '}';
    }
}

