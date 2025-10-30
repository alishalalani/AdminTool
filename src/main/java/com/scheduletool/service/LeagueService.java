package com.scheduletool.service;

import com.scheduletool.model.League;
import com.scheduletool.model.Sport;
import com.scheduletool.repository.LeagueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeagueService {
    
    @Autowired
    private LeagueRepository leagueRepository;
    
    public List<League> getAllLeagues() {
        return leagueRepository.findAll();
    }
    
    public List<League> getActiveLeagues() {
        return leagueRepository.findByActiveTrue();
    }
    
    public Optional<League> getLeagueById(Integer id) {
        return leagueRepository.findById(id);
    }
    
    public League getLeagueByName(String name) {
        return leagueRepository.findByName(name);
    }
    
    public List<League> getLeaguesBySport(Sport sport) {
        return leagueRepository.findBySport(sport);
    }

    public List<League> getLeaguesBySportId(Integer sportId) {
        return leagueRepository.findBySportId(sportId);
    }

    public List<League> getActiveLeaguesBySportId(Integer sportId) {
        return leagueRepository.findBySportIdAndActive(sportId, true);
    }

    public League createLeague(League league) {
        return leagueRepository.save(league);
    }

    public League updateLeague(Integer id, League leagueDetails) {
        League league = leagueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("League not found with id: " + id));

        league.setName(leagueDetails.getName());
        league.setSport(leagueDetails.getSport());
        league.setActive(leagueDetails.getActive());

        return leagueRepository.save(league);
    }
    
    public void deleteLeague(Integer id) {
        leagueRepository.deleteById(id);
    }
}

