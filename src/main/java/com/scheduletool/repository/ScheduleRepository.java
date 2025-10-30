package com.scheduletool.repository;

import com.scheduletool.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    Schedule findByDate(LocalDate date);
    List<Schedule> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Schedule> findByDateAfter(LocalDate date);
    List<Schedule> findByDateBefore(LocalDate date);
}

