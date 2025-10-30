package com.scheduletool.service;

import com.scheduletool.model.Team;
import com.scheduletool.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamService {
    
    @Autowired
    private TeamRepository teamRepository;
    
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    
    public List<Team> getActiveTeams() {
        return teamRepository.findByActiveTrue();
    }
    
    public Optional<Team> getTeamById(Integer id) {
        return teamRepository.findById(id);
    }
    
    public Team getTeamByName(String name) {
        return teamRepository.findByName(name);
    }
    
    public List<Team> getTeamsByCity(String city) {
        return teamRepository.findByCity(city);
    }
    
    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }
    
    public Team updateTeam(Integer id, Team teamDetails) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));
        
        team.setName(teamDetails.getName());
        team.setAbbreviation(teamDetails.getAbbreviation());
        team.setCity(teamDetails.getCity());
        team.setActive(teamDetails.getActive());
        
        return teamRepository.save(team);
    }
    
    public void deleteTeam(Integer id) {
        teamRepository.deleteById(id);
    }
}

