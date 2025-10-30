package com.scheduletool.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Event Entity
 * Replaces gsutils.data.Event
 */
@Entity
@Table(name = "event", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"date", "number"})
})
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "number", nullable = false)
    private Integer number;
    
    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    
    @Column(name = "exclude")
    private Boolean exclude;
    
    @Column(name = "double_header")
    private Integer doubleHeader;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    @Column(name = "updated")
    private OffsetDateTime updated;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventItem> eventItems;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventTime> eventTimes;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventVenue> eventVenues;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventScore> eventScores;
    
    // Constructors
    public Event() {
    }
    
    public Event(LocalDate date, Integer number, League league) {
        this.date = date;
        this.number = number;
        this.league = league;
        this.exclude = false;
        this.doubleHeader = 0;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Integer getNumber() {
        return number;
    }
    
    public void setNumber(Integer number) {
        this.number = number;
    }
    
    public League getLeague() {
        return league;
    }
    
    public void setLeague(League league) {
        this.league = league;
    }
    
    public Boolean getExclude() {
        return exclude;
    }
    
    public void setExclude(Boolean exclude) {
        this.exclude = exclude;
    }
    
    public Integer getDoubleHeader() {
        return doubleHeader;
    }
    
    public void setDoubleHeader(Integer doubleHeader) {
        this.doubleHeader = doubleHeader;
    }
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public OffsetDateTime getUpdated() {
        return updated;
    }
    
    public void setUpdated(OffsetDateTime updated) {
        this.updated = updated;
    }
    
    public List<EventItem> getEventItems() {
        return eventItems;
    }
    
    public void setEventItems(List<EventItem> eventItems) {
        this.eventItems = eventItems;
    }
    
    public List<EventTime> getEventTimes() {
        return eventTimes;
    }
    
    public void setEventTimes(List<EventTime> eventTimes) {
        this.eventTimes = eventTimes;
    }
    
    public List<EventVenue> getEventVenues() {
        return eventVenues;
    }
    
    public void setEventVenues(List<EventVenue> eventVenues) {
        this.eventVenues = eventVenues;
    }
    
    public List<EventScore> getEventScores() {
        return eventScores;
    }
    
    public void setEventScores(List<EventScore> eventScores) {
        this.eventScores = eventScores;
    }
    
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", date=" + date +
                ", number=" + number +
                ", league=" + (league != null ? league.getName() : "null") +
                '}';
    }
}

