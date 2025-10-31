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
    private Integer awayParticipantId;
    private String homeTeam;
    private Integer homeTeamId;
    private Integer homeParticipantId;
    private Integer leagueId;
    private Boolean active;  // Whether the game is active or deactivated

    // Score information
    private Integer score1;  // Away score
    private Integer score2;  // Home score
    private String timer;    // Game clock (from status0 column)
    private String period;   // Game period (from status1 column)

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

    public Integer getAwayParticipantId() {
        return awayParticipantId;
    }

    public void setAwayParticipantId(Integer awayParticipantId) {
        this.awayParticipantId = awayParticipantId;
    }

    public Integer getHomeParticipantId() {
        return homeParticipantId;
    }

    public void setHomeParticipantId(Integer homeParticipantId) {
        this.homeParticipantId = homeParticipantId;
    }

    public Integer getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
        return "GameDTO{" +
                "eventId=" + eventId +
                ", date=" + date +
                ", number=" + number +
                ", time=" + time +
                ", awayTeam='" + awayTeam + '\'' +
                ", homeTeam='" + homeTeam + '\'' +
                ", score1=" + score1 +
                ", score2=" + score2 +
                '}';
    }
}

