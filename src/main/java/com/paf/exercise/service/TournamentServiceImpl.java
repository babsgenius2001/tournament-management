package com.paf.exercise.service;

import com.paf.exercise.exceptions.PlayerNotFoundException;
import com.paf.exercise.exceptions.TournamentNotFoundException;
import com.paf.exercise.dto.PlayerRequest;
import com.paf.exercise.dto.TournamentRequest;
import com.paf.exercise.entity.Player;
import com.paf.exercise.repository.PlayerRepository;
import com.paf.exercise.entity.Tournament;
import com.paf.exercise.repository.TournamentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TournamentServiceImpl implements TournamentService {
    private static final String TOURNAMENT_NOT_FOUND = "Tournament not found with id: ";
    private static final String PLAYER_NOT_FOUND = "Player not found with id: ";
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Override
    @Transactional
    public Tournament addTournament(TournamentRequest tournamentRequest) {
        log.info("Entered the |addTournament| method");
        String tournamentId = UUID.randomUUID().toString();

        if (validRequest(tournamentRequest)) {

            Tournament tournament = new Tournament();
            tournament.setTournamentId(tournamentId);
            tournament.setTournamentName(tournamentRequest.getTournamentName());
            tournament.setRewardAmount(tournamentRequest.getRewardAmount());
            tournament.setCurrency(tournamentRequest.getCurrency());
            tournament.setPlayers(new ArrayList<>());

            log.info("Tournament Created and Saved Successfully!");
            return tournamentRepository.save(tournament);
        } else {
            log.error("Invalid tournament request: " + tournamentRequest);
            throw new IllegalStateException("Invalid tournament request: " + tournamentRequest);
        }
    }

    @Override
    @Transactional
    public Tournament updateTournament(String tournamentId, Tournament tournament) throws TournamentNotFoundException {
        log.info("Entered the |updateTournament| method");
        Tournament checkTournamentWithId = tournamentRepository.findByTournamentId(tournamentId).orElse(null);
        if (checkTournamentWithId != null) {
            if (tournament.getTournamentName() != null) {
                checkTournamentWithId.setTournamentName(tournament.getTournamentName());
            }
            if (tournament.getRewardAmount() > 0) {
                checkTournamentWithId.setRewardAmount(tournament.getRewardAmount());
            }
            if (tournament.getCurrency() != null) {
                checkTournamentWithId.setCurrency(tournament.getCurrency());
            }

            log.info("Tournament with ID: {} details updated successfully!", tournamentId);
            return tournamentRepository.save(checkTournamentWithId);
        } else {
            log.error(TOURNAMENT_NOT_FOUND + tournamentId);
            throw new TournamentNotFoundException(TOURNAMENT_NOT_FOUND + tournamentId);
        }
    }

    @Override
    public Tournament getTournament(String tournamentId) throws TournamentNotFoundException {
        log.info("Entered the |getTournament| method");
        Tournament tournament = tournamentRepository.findByTournamentId(tournamentId).orElse(null);
        if (tournament != null) {
            log.info("Tournament with ID: {} details retrieved successfully!", tournamentId);
            return tournament;
        } else {
            log.error(TOURNAMENT_NOT_FOUND + tournamentId);
            throw new TournamentNotFoundException(TOURNAMENT_NOT_FOUND + tournamentId);
        }
    }

    @Override
    public List<Tournament> getTournaments() {
        log.info("Entered the |getTournaments| method");
        return tournamentRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteTournament(String tournamentId) throws TournamentNotFoundException {
        log.info("Entered the |deleteTournament| method");
        Tournament tournament = tournamentRepository.findByTournamentId(tournamentId).orElse(null);
        if (tournament != null) {
            tournamentRepository.deleteByTournamentId(tournamentId);
        } else {
            log.error(TOURNAMENT_NOT_FOUND + tournamentId);
            throw new TournamentNotFoundException(TOURNAMENT_NOT_FOUND + tournamentId);
        }
        log.info("Tournament with ID: {} deleted successfully!", tournamentId);
    }

    @Override
    @Transactional
    public Tournament addPlayerIntoTournament(String tournamentId, PlayerRequest playerRequest) throws TournamentNotFoundException {
        log.info("Entered the |addPlayerIntoTournament| method");
        String playerId = UUID.randomUUID().toString();
        Tournament tournament = tournamentRepository.findByTournamentId(tournamentId).orElse(null);
        if (tournament != null) {
            if (validatePlayerRequest(playerRequest)) {
                Player player = new Player();
                player.setPlayerId(playerId);
                player.setPlayerName(playerRequest.getPlayerName());
                playerRepository.save(player);

                tournament.getPlayers().add(player);
                log.info("Player added to Tournament with ID: {} successfully!", tournamentId);
                return tournamentRepository.save(tournament);
            } else {
                log.error("Invalid player request: " + playerRequest);
                throw new IllegalStateException("Invalid player request: " + playerRequest);
            }
        } else {
            log.error(TOURNAMENT_NOT_FOUND + tournamentId);
            throw new TournamentNotFoundException(TOURNAMENT_NOT_FOUND + tournamentId);
        }
    }

    @Override
    public void removePlayerFromTournament(String tournamentId, String playerId) throws PlayerNotFoundException, TournamentNotFoundException {
        log.info("Entered the |removePlayerFromTournament| method");
        Tournament tournament = tournamentRepository.findByTournamentId(tournamentId).orElse(null);
        if (tournament != null) {
            if (tournament.getPlayers().stream().anyMatch(id -> Objects.equals(id.getPlayerId(), playerId))) {

                Player player = playerRepository.findByPlayerId(playerId).orElse(null);
                if (player != null) {
                    tournament.getPlayers().remove(player);
                    tournamentRepository.save(tournament);
                } else {
                    log.error(PLAYER_NOT_FOUND + playerId);
                    throw new PlayerNotFoundException(PLAYER_NOT_FOUND + playerId);
                }
            } else {
                log.error(PLAYER_NOT_FOUND + playerId);
                throw new PlayerNotFoundException(PLAYER_NOT_FOUND + playerId);
            }
        } else {
            log.error(TOURNAMENT_NOT_FOUND + tournamentId);
            throw new TournamentNotFoundException(TOURNAMENT_NOT_FOUND + tournamentId);
        }
        log.info("Player with ID: {} deleted successfully!", playerId);
    }

    @Override
    public List<Player> getPlayersInTournament(String tournamentId) throws TournamentNotFoundException {
        log.info("Entered the |getPlayersInTournament| method");
        Tournament tournament = tournamentRepository.findByTournamentId(tournamentId).orElse(null);
        if (tournament != null) {
            return tournament.getPlayers().stream().map(player -> {
                Player players = new Player();
                players.setPlayerId(player.getPlayerId());
                players.setPlayerName(player.getPlayerName());
                return players;
            }).collect(Collectors.toList());
        } else {
            log.error(TOURNAMENT_NOT_FOUND + tournamentId);
            throw new TournamentNotFoundException(TOURNAMENT_NOT_FOUND + tournamentId);
        }
    }

    private boolean validRequest(TournamentRequest tournamentRequest) {
        return !tournamentRequest.getTournamentName().isEmpty() && tournamentRequest.getRewardAmount() >= 1 && !tournamentRequest.getCurrency().isEmpty();
    }

    private boolean validatePlayerRequest(PlayerRequest playerRequest) {
        boolean resp = true;
        if (playerRequest.getPlayerName() == null) return false;
        else if (playerRequest.getPlayerName().isEmpty()) return false;
        else if (playerRequest.getPlayerName().trim().isEmpty()) return false;
        return resp;
    }
}
