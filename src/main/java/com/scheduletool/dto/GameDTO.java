package com.scheduletool.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * DTO for Game information
 * Combines Event, Participants, and Teams
 */
public class GameDTO {
    
    private Integer eventId;
    private LocalDate date;
    private Integer number;
    private OffsetDateTime time;
    private Integer tba;
    private String awayTeam;
    private Integer awayTeamId;
    private String homeTeam;
    private Integer homeTeamId;
    
    public GameDTO() {
    }
    
    public GameDTO(Integer eventId, LocalDate date, Integer number) {
        this.eventId = eventId;
        this.date = date;
        this.number = number;
    }
    
    // Getters and Setters
    public Integer getEventId() {
        return eventId;
    }
    
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
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
    
    public String getAwayTeam() {
        return awayTeam;
    }
    
    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }
    
    public Integer getAwayTeamId() {
        return awayTeamId;
    }
    
    public void setAwayTeamId(Integer awayTeamId) {
        this.awayTeamId = awayTeamId;
    }
    
    public String getHomeTeam() {
        return homeTeam;
    }
    
    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }
    
    public Integer getHomeTeamId() {
        return homeTeamId;
    }
    
    public void setHomeTeamId(Integer homeTeamId) {
        this.homeTeamId = homeTeamId;
    }
    
    @Override
    public String toString() {
        return "GameDTO{" +
                "eventId=" + eventId +
                ", date=" + date +
                ", number=" + number +
                ", time=" + time +
                ", awayTeam='" + awayTeam + '\'' +
                ", homeTeam='" + homeTeam + '\'' +
                '}';
    }
}

