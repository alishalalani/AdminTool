package com.scheduletool.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * GroupEvent Junction Entity
 * Maps groups to events
 */
@Entity
@Table(name = "group_event")
@IdClass(GroupEvent.GroupEventId.class)
public class GroupEvent {

    @Id
    @Column(name = "group_id")
    private Integer groupId;

    @Id
    @Column(name = "event_id")
    private Integer eventId;
    
    // Constructors
    public GroupEvent() {
    }
    
    public GroupEvent(Integer groupId, Integer eventId) {
        this.groupId = groupId;
        this.eventId = eventId;
    }
    
    // Getters and Setters
    public Integer getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
    
    public Integer getEventId() {
        return eventId;
    }
    
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    
    @Override
    public String toString() {
        return "GroupEvent{" +
                "groupId=" + groupId +
                ", eventId=" + eventId +
                '}';
    }

    /**
     * Composite key class for GroupEvent
     */
    public static class GroupEventId implements Serializable {
        private Integer groupId;
        private Integer eventId;

        public GroupEventId() {
        }

        public GroupEventId(Integer groupId, Integer eventId) {
            this.groupId = groupId;
            this.eventId = eventId;
        }

        // Getters and Setters
        public Integer getGroupId() {
            return groupId;
        }

        public void setGroupId(Integer groupId) {
            this.groupId = groupId;
        }

        public Integer getEventId() {
            return eventId;
        }

        public void setEventId(Integer eventId) {
            this.eventId = eventId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupEventId that = (GroupEventId) o;
            return groupId.equals(that.groupId) && eventId.equals(that.eventId);
        }

        @Override
        public int hashCode() {
            return 31 * groupId.hashCode() + eventId.hashCode();
        }
    }
}

