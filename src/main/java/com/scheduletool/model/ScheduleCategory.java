package com.scheduletool.model;

import javax.persistence.*;

/**
 * Schedule_Category Junction Entity
 */
@Entity

    

@Table(name = "schedule_category")
public class ScheduleCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(name = "sequence")
    private Integer sequence;
    
    // Constructors
    public ScheduleCategory() {
    }
    
    public ScheduleCategory(Schedule schedule, Category category) {
        this.schedule = schedule;
        this.category = category;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Schedule getSchedule() {
        return schedule;
    }
    
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Integer getSequence() {
        return sequence;
    }
    
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
    
    @Override
    public String toString() {
        return "ScheduleCategory{" +
                "id=" + id +
                ", sequence=" + sequence +
                '}';
    }
}

