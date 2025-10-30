package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Sport Entity
 * Replaces gsutils.data.Sport
 */
@Entity

@Table(name = "sport")
public class Sport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "abbr", length = 10)
    private String abbreviation;
    
    @Column(name = "active")
    private Boolean active;

    @Column(name = "created")
    private OffsetDateTime created;
    
    // Constructors
    public Sport() {
    }
    
    public Sport(String name) {
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
    
    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }
    
    @Override
    public String toString() {
        return "Sport{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", active=" + active +
                '}';
    }
}

