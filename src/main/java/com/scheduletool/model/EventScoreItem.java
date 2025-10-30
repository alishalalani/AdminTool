package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Event_Score_Item Entity
 * Replaces gsutils.data.Event_Score_Item
 */
@Entity

@Table(name = "event_score_item")
public class EventScoreItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "event_score_id", nullable = false)
    private EventScore eventScore;
    
    @ManyToOne
    @JoinColumn(name = "event_item_id", nullable = false)
    private EventItem eventItem;
    
    @Column(name = "score")
    private Integer score;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    // Constructors
    public EventScoreItem() {
    }
    
    public EventScoreItem(EventScore eventScore, EventItem eventItem) {
        this.eventScore = eventScore;
        this.eventItem = eventItem;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public EventScore getEventScore() {
        return eventScore;
    }
    
    public void setEventScore(EventScore eventScore) {
        this.eventScore = eventScore;
    }
    
    public EventItem getEventItem() {
        return eventItem;
    }
    
    public void setEventItem(EventItem eventItem) {
        this.eventItem = eventItem;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "EventScoreItem{" +
                "id=" + id +
                ", score=" + score +
                '}';
    }
}

