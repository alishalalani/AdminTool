package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Event_Item_League_Team Entity
 */
@Entity

@Table(name = "event_item_league_team")
public class EventItemLeagueTeam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "event_item_id", nullable = false)
    private EventItem eventItem;
    
    @ManyToOne
    @JoinColumn(name = "league_team_id", nullable = false)
    private LeagueTeam leagueTeam;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    // Constructors
    public EventItemLeagueTeam() {
    }
    
    public EventItemLeagueTeam(EventItem eventItem, LeagueTeam leagueTeam) {
        this.eventItem = eventItem;
        this.leagueTeam = leagueTeam;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public EventItem getEventItem() {
        return eventItem;
    }
    
    public void setEventItem(EventItem eventItem) {
        this.eventItem = eventItem;
    }
    
    public LeagueTeam getLeagueTeam() {
        return leagueTeam;
    }
    
    public void setLeagueTeam(LeagueTeam leagueTeam) {
        this.leagueTeam = leagueTeam;
    }
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "EventItemLeagueTeam{" +
                "id=" + id +
                ", leagueTeam=" + (leagueTeam != null ? leagueTeam.getId() : "null") +
                '}';
    }
}

