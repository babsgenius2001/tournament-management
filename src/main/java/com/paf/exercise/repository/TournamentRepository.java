package com.paf.exercise.repository;

import com.paf.exercise.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament,Long> {
    Optional<Tournament> findByTournamentId(String tournamentId);

    void deleteByTournamentId(String tournamentId);
}
