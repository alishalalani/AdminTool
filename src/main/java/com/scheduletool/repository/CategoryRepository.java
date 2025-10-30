package com.scheduletool.repository;

import com.scheduletool.model.Category;
import com.scheduletool.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByLeague(League league);
    List<Category> findByLeagueId(Integer leagueId);
    List<Category> findByLeagueIdAndDateGreaterThanEqual(Integer leagueId, LocalDate date);
    List<Category> findByDate(LocalDate date);
    List<Category> findByLeagueAndDate(League league, LocalDate date);
    List<Category> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Category> findByExcludeFalse();
}

