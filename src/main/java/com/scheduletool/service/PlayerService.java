package com.scheduletool.service;

import com.scheduletool.model.Player;
import com.scheduletool.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlayerService {
    
    @Autowired
    private PlayerRepository playerRepository;
    
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
    
    public List<Player> getActivePlayers() {
        return playerRepository.findByActiveTrue();
    }
    
    public Optional<Player> getPlayerById(Integer id) {
        return playerRepository.findById(id);
    }
    
    public Player getPlayerByFullName(String fullName) {
        return playerRepository.findByFullName(fullName);
    }
    
    public List<Player> getPlayersByLastName(String lastName) {
        return playerRepository.findByLastName(lastName);
    }
    
    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }
    
    public Player updatePlayer(Integer id, Player playerDetails) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
        
        player.setFirstName(playerDetails.getFirstName());
        player.setLastName(playerDetails.getLastName());
        player.setFullName(playerDetails.getFullName());
        player.setActive(playerDetails.getActive());
        
        return playerRepository.save(player);
    }
    
    public void deletePlayer(Integer id) {
        playerRepository.deleteById(id);
    }
}

