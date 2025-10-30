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

    public Optional<Team> getTeamById(Integer id) {
        return teamRepository.findById(id);
    }

    public Team getTeamByName(String name) {
        return teamRepository.findByName(name);
    }

    public Team getTeamByAbbr(String abbr) {
        return teamRepository.findByAbbr(abbr);
    }

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    public Team updateTeam(Integer id, Team teamDetails) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));

        team.setName(teamDetails.getName());
        team.setFirstName(teamDetails.getFirstName());
        team.setNickname(teamDetails.getNickname());
        team.setAbbr(teamDetails.getAbbr());
        team.setAbbrParser(teamDetails.getAbbrParser());
        team.setFullName(teamDetails.getFullName());

        return teamRepository.save(team);
    }

    public void deleteTeam(Integer id) {
        teamRepository.deleteById(id);
    }
}

