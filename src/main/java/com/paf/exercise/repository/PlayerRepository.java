package com.paf.exercise.repository;

import com.paf.exercise.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player,Long> {

    Optional<Player> findByPlayerId(String playerId);
}