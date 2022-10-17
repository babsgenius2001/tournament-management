package com.paf.exercise.controller;

import com.paf.exercise.dto.PlayerRequest;
import com.paf.exercise.dto.TournamentRequest;
import com.paf.exercise.entity.Player;
import com.paf.exercise.exceptions.PlayerNotFoundException;
import com.paf.exercise.exceptions.TournamentNotFoundException;
import com.paf.exercise.entity.Tournament;
import com.paf.exercise.service.TournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ExerciseController {

    private TournamentService tournamentService;

    public ExerciseController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    //Create a new tournament
    @PostMapping(value = "/addTournament")
    public ResponseEntity<Tournament> createNewTournament(@Valid @RequestBody TournamentRequest tournamentRequest) {
        log.info("Creating a new tournament with request payload {}", tournamentRequest);
        return new ResponseEntity<>(tournamentService.addTournament(tournamentRequest), HttpStatus.CREATED);
    }

    //Fetching all tournaments
    @GetMapping(value = "/getTournaments")
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        log.info("Getting all available tournaments from the database");
        return ResponseEntity.ok(tournamentService.getTournaments());
    }

    //Fetching a particular tournament with its tournament id
    @GetMapping(value = "/getTournament")
    public ResponseEntity<Tournament> retrieveTournamentById(@RequestParam String tournamentId) throws TournamentNotFoundException {
        log.info("Getting details of tournament with id {}", tournamentId);
        return ResponseEntity.ok(tournamentService.getTournament(tournamentId));
    }

    //Update tournament details with the tournament id
    @PutMapping("/updateTournament")
    public ResponseEntity<Tournament> updateTournamentDetails(@RequestParam String tournamentId, @RequestBody Tournament tournament) throws TournamentNotFoundException {
        log.info("updating the details of tournament with id {}", tournamentId);
        return ResponseEntity.ok(tournamentService.updateTournament(tournamentId, tournament));
    }

    //Deleting a particular tournament using its tournament id
    @DeleteMapping(value = "/removeTournament")
    public void deleteTournament(@RequestParam String tournamentId) throws TournamentNotFoundException {
        log.info("Initiating the process of removing the tournament with id {}", tournamentId);
        tournamentService.deleteTournament(tournamentId);
    }

    //Create a new player and register with a tournament
    @PostMapping(value = "/addPlayerIntoTournament")
    public ResponseEntity<Tournament> registerPlayerToATournament(@RequestParam String tournamentId, @RequestBody @Valid PlayerRequest playerRequest) throws TournamentNotFoundException {
        log.info("Creating a new player with request payload {} and adding to tournament {}", playerRequest, tournamentId);
        return new ResponseEntity<>(tournamentService.addPlayerIntoTournament(tournamentId, playerRequest), HttpStatus.CREATED);
    }

    //Deleting a player from a particular tournament using the tournament id and player id
    @DeleteMapping(value = "/removePlayerFromTournament")
    public void removeAPlayerFromATournament(@RequestParam String tournamentId, @RequestParam String playerId) throws TournamentNotFoundException, PlayerNotFoundException {
        log.info("Initiating the process of removing a player with id {} from the tournament with id {}", playerId, tournamentId);
        tournamentService.removePlayerFromTournament(tournamentId, playerId);
    }

    //Fetching all players in a particular tournament using the tournament id
    @GetMapping(value = "/getPlayersInTournament")
    public ResponseEntity<List<Player>> getAllPlayersInATournament(@RequestParam String tournamentId) throws TournamentNotFoundException {
        log.info("Getting all available players registered for the tournament with id {}", tournamentId);
        return ResponseEntity.ok(tournamentService.getPlayersInTournament(tournamentId));
    }
}
