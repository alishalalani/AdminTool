package com.scheduletool.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Category Entity
 * Replaces gsutils.data.Category
 */
@Entity
 
    

@Table(name = "category")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    
    @ManyToOne
    @JoinColumn(name = "category_type_id")
    private CategoryType categoryType;
    
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
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
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
    
    public CategoryType getCategoryType() {
        return categoryType;
    }
    
    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
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
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
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

