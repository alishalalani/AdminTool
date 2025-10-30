package com.scheduletool.repository;

import com.scheduletool.model.GroupEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupEventRepository extends JpaRepository<GroupEvent, Integer> {
    
    @Query("SELECT ge.eventId FROM GroupEvent ge WHERE ge.groupId = :groupId")
    List<Integer> findEventIdsByGroupId(@Param("groupId") Integer groupId);
}

