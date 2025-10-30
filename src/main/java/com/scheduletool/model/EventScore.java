package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Event_Score Entity
 * Replaces gsutils.data.Event_Score
 */
@Entity

    

@Table(name = "event_score")
public class EventScore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "source_id")
    private Source source;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;

    @Column(name = "score1")
    private Integer score1;

    @Column(name = "score2")
    private Integer score2;

    @Column(name = "timer")
    private String timer;

    @Column(name = "period")
    private String period;

    @OneToMany(mappedBy = "eventScore", cascade = CascadeType.ALL)
    private List<EventScoreItem> eventScoreItems;
    
    // Constructors
    public EventScore() {
    }
    
    public EventScore(Event event) {
        this.event = event;
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
    
    public List<EventScoreItem> getEventScoreItems() {
        return eventScoreItems;
    }
    
    public void setEventScoreItems(List<EventScoreItem> eventScoreItems) {
        this.eventScoreItems = eventScoreItems;
    }

    public Integer getScore1() {
        return score1;
    }

    public void setScore1(Integer score1) {
        this.score1 = score1;
    }

    public Integer getScore2() {
        return score2;
    }

    public void setScore2(Integer score2) {
        this.score2 = score2;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "EventScore{" +
                "id=" + id +
                ", event=" + (event != null ? event.getId() : "null") +
                ", score1=" + score1 +
                ", score2=" + score2 +
                '}';
    }
}

