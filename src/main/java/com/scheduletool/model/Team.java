package com.scheduletool.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Team Entity (Generic teams)
 * Replaces gsutils.data.Team
 */
@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "abbr", length = 10)
    private String abbr;

    @Column(name = "abbr_parser", length = 10)
    private String abbrParser;

    @Column(name = "full_name", length = 200)
    private String fullName;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "created")
    private OffsetDateTime created;
    
    // Constructors
    public Team() {
    }

    public Team(String name) {
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getAbbrParser() {
        return abbrParser;
    }

    public void setAbbrParser(String abbrParser) {
        this.abbrParser = abbrParser;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", firstName='" + firstName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", abbr='" + abbr + '\'' +
                '}';
    }
}

