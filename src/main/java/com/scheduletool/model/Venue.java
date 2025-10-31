package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Venue Entity
 * Replaces gsutils.data.Venue
 */
@Entity
@Table(name = "venue")
public class Venue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    
    @Column(name = "capacity")
    private Integer capacity;
    
    @Column(name = "surface", length = 50)
    private String surface;
    
    @Column(name = "active")
    private Boolean active;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    // Constructors
    public Venue() {
    }
    
    public Venue(String name) {
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
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public String getSurface() {
        return surface;
    }
    
    public void setSurface(String surface) {
        this.surface = surface;
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
        return "Venue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location=" + (location != null ? location.getCity() : "null") +
                ", capacity=" + capacity +
                '}';
    }
}

