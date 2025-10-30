package com.scheduletool.repository;

import com.scheduletool.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    List<Player> findByActiveTrue();
    List<Player> findByLastName(String lastName);
    Player findByFullName(String fullName);
    List<Player> findByFirstNameAndLastName(String firstName, String lastName);
}

