package com.scheduletool.repository;

import com.scheduletool.model.EventGroupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventGroupTypeRepository extends JpaRepository<EventGroupType, Integer> {
}

