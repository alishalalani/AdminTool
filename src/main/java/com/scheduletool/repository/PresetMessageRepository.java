package com.scheduletool.repository;

import com.scheduletool.model.PresetMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresetMessageRepository extends JpaRepository<PresetMessage, Integer> {
    List<PresetMessage> findByActiveTrue();
}

