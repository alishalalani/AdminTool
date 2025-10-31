package com.scheduletool.service;

import com.scheduletool.model.EventGroupType;
import com.scheduletool.repository.EventGroupTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventGroupTypeService {
    
    @Autowired
    private EventGroupTypeRepository eventGroupTypeRepository;
    
    public List<EventGroupType> getAllEventGroupTypes() {
        return eventGroupTypeRepository.findAll();
    }
    
    public EventGroupType getEventGroupTypeById(Integer id) {
        return eventGroupTypeRepository.findById(id).orElse(null);
    }
}

