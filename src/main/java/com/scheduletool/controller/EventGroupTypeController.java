package com.scheduletool.controller;

import com.scheduletool.model.EventGroupType;
import com.scheduletool.service.EventGroupTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-group-types")
@CrossOrigin(origins = "*")
public class EventGroupTypeController {
    
    @Autowired
    private EventGroupTypeService eventGroupTypeService;
    
    @GetMapping
    public List<EventGroupType> getAllEventGroupTypes() {
        return eventGroupTypeService.getAllEventGroupTypes();
    }
    
    @GetMapping("/{id}")
    public EventGroupType getEventGroupTypeById(@PathVariable Integer id) {
        return eventGroupTypeService.getEventGroupTypeById(id);
    }
}

