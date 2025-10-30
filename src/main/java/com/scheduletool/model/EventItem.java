package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Event_Item Entity (Participants in an event)
 * Replaces gsutils.data.Event_Item
 */
@Entity

@Table(name = "event_item")
public class EventItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @Column(name = "sequence")
    private Integer sequence;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    @OneToMany(mappedBy = "eventItem", cascade = CascadeType.ALL)
    private List<EventItemLeagueTeam> eventItemLeagueTeams;
    
    @OneToMany(mappedBy = "eventItem", cascade = CascadeType.ALL)
    private List<EventScoreItem> eventScoreItems;
    
    // Constructors
    public EventItem() {
    }
    
    public EventItem(Event event, Integer sequence) {
        this.event = event;
        this.sequence = sequence;
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
    
    public Integer getSequence() {
        return sequence;
    }
    
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<EventItemLeagueTeam> getEventItemLeagueTeams() {
        return eventItemLeagueTeams;
    }
    
    public void setEventItemLeagueTeams(List<EventItemLeagueTeam> eventItemLeagueTeams) {
        this.eventItemLeagueTeams = eventItemLeagueTeams;
    }
    
    public List<EventScoreItem> getEventScoreItems() {
        return eventScoreItems;
    }
    
    public void setEventScoreItems(List<EventScoreItem> eventScoreItems) {
        this.eventScoreItems = eventScoreItems;
    }
    
    @Override
    public String toString() {
        return "EventItem{" +
                "id=" + id +
                ", sequence=" + sequence +
                '}';
    }
}

