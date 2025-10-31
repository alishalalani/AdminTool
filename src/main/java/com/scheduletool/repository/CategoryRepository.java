package com.scheduletool.repository;

import com.scheduletool.model.Category;
import com.scheduletool.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByLeagueOrderByDateAsc(League league);
    List<Category> findByLeagueIdOrderByDateAsc(Integer leagueId);
    List<Category> findByLeagueIdAndDateGreaterThanEqualOrderByDateAsc(Integer leagueId, LocalDate date);
    List<Category> findByDateOrderByDateAsc(LocalDate date);
    List<Category> findByLeagueAndDateOrderByDateAsc(League league, LocalDate date);
    List<Category> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);
    List<Category> findByExcludeFalseOrderByDateAsc();
}

