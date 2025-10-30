package com.scheduletool.repository;

import com.scheduletool.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    Team findByName(String name);
    Team findByAbbr(String abbr);
    Team findByNickname(String nickname);
}

