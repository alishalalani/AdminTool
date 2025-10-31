package com.scheduletool.service;

import com.scheduletool.model.Event;
import com.scheduletool.model.League;
import com.scheduletool.repository.EventRepository;
import com.scheduletool.repository.EventTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventTimeRepository eventTimeRepository;
    
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    public Optional<Event> getEventById(Integer id) {
        return eventRepository.findById(id);
    }
    
    public List<Event> getEventsByDate(LocalDate date) {
        return eventRepository.findByDate(date);
    }
    
    public List<Event> getEventsByLeague(League league) {
        return eventRepository.findByLeague(league);
    }
    
    public List<Event> getEventsByDateAndLeague(LocalDate date, League league) {
        return eventRepository.findByDateAndLeague(date, league);
    }
    
    public Event getEventByDateAndNumber(LocalDate date, Integer number) {
        return eventRepository.findByDateAndNumber(date, number);
    }
    
    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByDateBetween(startDate, endDate);
    }
    
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }
    
    public Event updateEvent(Integer id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        
        event.setDate(eventDetails.getDate());
        event.setNumber(eventDetails.getNumber());
        event.setLeague(eventDetails.getLeague());
        event.setDoubleHeader(eventDetails.getDoubleHeader());
        
        return eventRepository.save(event);
    }
    
    public void deleteEvent(Integer id) {
        // Instead of deleting, set active to false
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        event.setActive(false);
        eventRepository.save(event);
    }
}

