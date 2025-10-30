package com.scheduletool.repository;

import com.scheduletool.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceRepository extends JpaRepository<Source, Integer> {
    List<Source> findByActiveTrue();
    Source findByName(String name);
}

