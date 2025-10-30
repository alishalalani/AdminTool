package com.scheduletool.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Participant Entity
 * Represents a participant in an event
 */
@Entity
@Table(name = "participant")
public class Participant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "event_id", nullable = false)
    private Integer eventId;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "home")
    private Integer home;
    
    @Column(name = "active")
    private Integer active;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    // Constructors
    public Participant() {
    }
    
    public Participant(Integer eventId, Integer sortOrder) {
        this.eventId = eventId;
        this.sortOrder = sortOrder;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getEventId() {
        return eventId;
    }
    
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Integer getHome() {
        return home;
    }
    
    public void setHome(Integer home) {
        this.home = home;
    }
    
    public Integer getActive() {
        return active;
    }
    
    public void setActive(Integer active) {
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
        return "Participant{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", sortOrder=" + sortOrder +
                ", home=" + home +
                '}';
    }
}

