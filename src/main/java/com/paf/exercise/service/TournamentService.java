package com.paf.exercise.service;

import com.paf.exercise.dto.PlayerRequest;
import com.paf.exercise.exceptions.PlayerNotFoundException;
import com.paf.exercise.exceptions.TournamentNotFoundException;
import com.paf.exercise.dto.TournamentRequest;
import com.paf.exercise.entity.Player;
import com.paf.exercise.entity.Tournament;

import java.util.List;

public interface TournamentService {

    Tournament addTournament(TournamentRequest tournamentRequest);

    Tournament updateTournament(String tournamentId, Tournament tournament) throws TournamentNotFoundException;

    Tournament getTournament(String tournamentId) throws TournamentNotFoundException;

    List<Tournament> getTournaments();

    void deleteTournament(String tournamentId) throws TournamentNotFoundException;

    Tournament addPlayerIntoTournament(String tournamentId, PlayerRequest playerRequest) throws TournamentNotFoundException;

    void removePlayerFromTournament(String tournamentId, String playerId) throws PlayerNotFoundException, TournamentNotFoundException;

    List<Player> getPlayersInTournament(String tournamentId) throws TournamentNotFoundException;

}
