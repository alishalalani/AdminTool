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
    @Column(name = "event_id")
    private Integer eventId;

    @Column(name = "time")
    private OffsetDateTime time;

    @Column(name = "TBA")
    private Integer tba;

    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    // Constructors
    public EventTime() {
    }

    public EventTime(Integer eventId) {
        this.eventId = eventId;
        this.tba = 0;
    }

    // Getters and Setters
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public void setTime(OffsetDateTime time) {
        this.time = time;
    }

    public Integer getTba() {
        return tba;
    }

    public void setTba(Integer tba) {
        this.tba = tba;
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
                "eventId=" + eventId +
                ", time=" + time +
                ", tba=" + tba +
                '}';
    }
}

