package com.scheduletool.repository;

import com.scheduletool.model.Event;
import com.scheduletool.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByDate(LocalDate date);
    List<Event> findByLeague(League league);
    List<Event> findByDateAndLeague(LocalDate date, League league);
    Event findByDateAndNumber(LocalDate date, Integer number);
    List<Event> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Event> findByExcludeFalse();
}

