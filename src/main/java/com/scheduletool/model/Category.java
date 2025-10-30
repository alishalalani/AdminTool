package com.scheduletool.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Category Entity
 * Replaces gsutils.data.Category
 */
@Entity



@Table(name = "groups")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "header", columnDefinition = "TEXT")
    private String header;
    
    @Column(name = "exclude")
    private Boolean exclude;

    @Column(name = "override")
    private Boolean override;

    // Constructors
    public Category() {
    }
    
    public Category(League league, LocalDate date) {
        this.league = league;
        this.date = date;
        this.exclude = false;
        this.override = false;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public League getLeague() {
        return league;
    }
    
    public void setLeague(League league) {
        this.league = league;
    }

    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getHeader() {
        return header;
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public Boolean getExclude() {
        return exclude;
    }
    
    public void setExclude(Boolean exclude) {
        this.exclude = exclude;
    }
    
    public Boolean getOverride() {
        return override;
    }
    
    public void setOverride(Boolean override) {
        this.override = override;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", league=" + (league != null ? league.getName() : "null") +
                ", date=" + date +
                ", header='" + header + '\'' +
                '}';
    }
}

