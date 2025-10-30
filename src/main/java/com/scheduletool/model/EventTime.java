package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Event_Time Entity
 * Replaces gsutils.data.Event_Time
 */
@Entity

    

@Table(name = "event_time")
public class EventTime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @Column(name = "time")
    private OffsetDateTime time;
    
    @Column(name = "TBA")
    private Boolean tba;
    
    @Column(name = "override")
    private Boolean override;
    
    @ManyToOne
    @JoinColumn(name = "source_id")
    private Source source;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    // Constructors
    public EventTime() {
    }
    
    public EventTime(Event event) {
        this.event = event;
        this.tba = false;
        this.override = false;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public OffsetDateTime getTime() {
        return time;
    }
    
    public void setTime(OffsetDateTime time) {
        this.time = time;
    }
    
    public Boolean getTba() {
        return tba;
    }
    
    public void setTba(Boolean tba) {
        this.tba = tba;
    }
    
    public Boolean getOverride() {
        return override;
    }
    
    public void setOverride(Boolean override) {
        this.override = override;
    }
    
    public Source getSource() {
        return source;
    }
    
    public void setSource(Source source) {
        this.source = source;
    }
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "EventTime{" +
                "id=" + id +
                ", time=" + time +
                ", tba=" + tba +
                '}';
    }
}

