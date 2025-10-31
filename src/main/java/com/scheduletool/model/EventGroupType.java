package com.scheduletool.model;

import javax.persistence.*;

/**
 * EventGroupType Entity
 * Maps to event_group_type table
 */
@Entity
@Table(name = "event_group_type")
public class EventGroupType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    // Constructors
    public EventGroupType() {
    }
    
    public EventGroupType(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "EventGroupType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

