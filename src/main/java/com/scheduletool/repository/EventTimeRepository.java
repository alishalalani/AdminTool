package com.scheduletool.repository;

import com.scheduletool.model.EventTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventTimeRepository extends JpaRepository<EventTime, Integer> {

    @Query("SELECT et FROM EventTime et WHERE et.eventId = :eventId")
    List<EventTime> findByEventId(@Param("eventId") Integer eventId);

    @Modifying
    @Query("DELETE FROM EventTime et WHERE et.eventId = :eventId")
    void deleteByEventId(@Param("eventId") Integer eventId);
}

