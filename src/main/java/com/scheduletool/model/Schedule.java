package com.scheduletool.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Schedule Entity
 * Replaces gsutils.data.Schedule
 */
@Entity
 
    

@Table(name = "schedule")
public class Schedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "date", nullable = false, unique = true)
    private LocalDate date;
    
    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
    
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ScheduleCategory> scheduleCategories;
    
    // Constructors
    public Schedule() {
    }
    
    public Schedule(LocalDate date) {
        this.date = date;
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
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<ScheduleCategory> getScheduleCategories() {
        return scheduleCategories;
    }
    
    public void setScheduleCategories(List<ScheduleCategory> scheduleCategories) {
        this.scheduleCategories = scheduleCategories;
    }
    
    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }
}

