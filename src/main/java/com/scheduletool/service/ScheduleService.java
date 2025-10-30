package com.scheduletool.service;

import com.scheduletool.model.Schedule;
import com.scheduletool.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleService {
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }
    
    public Optional<Schedule> getScheduleById(Integer id) {
        return scheduleRepository.findById(id);
    }
    
    public Schedule getScheduleByDate(LocalDate date) {
        return scheduleRepository.findByDate(date);
    }
    
    public List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByDateBetween(startDate, endDate);
    }
    
    public List<Schedule> getUpcomingSchedules(LocalDate date) {
        return scheduleRepository.findByDateAfter(date);
    }
    
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }
    
    public Schedule updateSchedule(Integer id, Schedule scheduleDetails) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));
        
        schedule.setDate(scheduleDetails.getDate());
        
        return scheduleRepository.save(schedule);
    }
    
    public void deleteSchedule(Integer id) {
        scheduleRepository.deleteById(id);
    }
}

