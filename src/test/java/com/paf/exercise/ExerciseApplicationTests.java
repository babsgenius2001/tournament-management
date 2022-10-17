package com.paf.exercise;

import com.paf.exercise.dto.PlayerRequest;
import com.paf.exercise.dto.TournamentRequest;
import com.paf.exercise.entity.Player;
import com.paf.exercise.entity.Tournament;
import com.paf.exercise.exceptions.PlayerNotFoundException;
import com.paf.exercise.exceptions.TournamentNotFoundException;
import com.paf.exercise.repository.PlayerRepository;
import com.paf.exercise.repository.TournamentRepository;
import com.paf.exercise.service.TournamentService;
import com.paf.exercise.service.TournamentServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(
        classes = {
                TournamentRepository.class,
                TournamentService.class,
                PlayerRepository.class,
                TournamentServiceImpl.class
        })
@EnableAutoConfiguration
@TestPropertySource("classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExerciseApplicationTests {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TournamentService tournamentService;

    @Order(1)
    @Test
    @DisplayName("Test that a new tournament can be created successfully")
    void should_create_a_new_tournament_successfully() {
        TournamentRequest tournamentRequest = new TournamentRequest();
        tournamentRequest.setTournamentName("NPL");
        tournamentRequest.setRewardAmount(20000);
        tournamentRequest.setCurrency("EUR");
        tournamentRequest.setPlayers(new ArrayList<>());

        Tournament createdTournament = tournamentService.addTournament(tournamentRequest);

        assertThat(createdTournament.getTournamentName()).isEqualTo(tournamentRequest.getTournamentName());
        assertThat(createdTournament.getRewardAmount()).isEqualTo(tournamentRequest.getRewardAmount());
        assertThat(createdTournament.getCurrency()).isEqualTo(tournamentRequest.getCurrency());
    }

    @Order(2)
    @Test
    @DisplayName("Test that all tournaments can be fetched successfully")
    void should_fetch_all_tournaments_successfully() {
        createTournament("UEFA", 10000, "USD");
        List<Tournament> tournamentList = tournamentService.getTournaments();

        assertThat(tournamentList).hasSize(1);
        assertThat(tournamentList.stream()
                .map(Tournament::getTournamentName)
                .collect(Collectors.toList()))
                .contains("UEFA");
    }

    @Order(3)
    @Test
    @DisplayName("Test that a particular tournament information can be fetched successfully")
    void should_fetch_specific_tournament_information_successfully() throws TournamentNotFoundException {
        Tournament tournament = createTournament("LALIGA", 30000, "EUR");
        Tournament persistedTournament = tournamentService.getTournament(tournament.getTournamentId());

        assertThat(persistedTournament.getTournamentName()).isEqualTo("LALIGA");
        assertThat(persistedTournament.getRewardAmount()).isEqualTo(30000);
        assertThat(persistedTournament.getCurrency()).isEqualTo("EUR");
    }

    @Order(4)
    @Test
    @DisplayName("Test that a particular tournament information can be updated successfully")
    void should_update_specific_tournament_information_successfully() throws TournamentNotFoundException {
        Tournament createdTournament = createTournament("LALIGA", 30000, "EUR");
        Tournament tournamentReq = new Tournament();
        tournamentReq.setTournamentId(createdTournament.getTournamentId());
        tournamentReq.setTournamentName("LALIGA");
        tournamentReq.setRewardAmount(59900);
        tournamentReq.setCurrency("EUR");
        tournamentReq.setPlayers(new ArrayList<>());

        Tournament updatedTournament = tournamentService.updateTournament(createdTournament.getTournamentId(), tournamentReq);

        assertThat(updatedTournament.getTournamentId()).isEqualTo(createdTournament.getTournamentId());
        assertThat(updatedTournament.getTournamentName()).isEqualTo("LALIGA");
        assertThat(updatedTournament.getRewardAmount()).isEqualTo(59900);
        assertThat(updatedTournament.getCurrency()).isEqualTo("EUR");
    }

    @Order(5)
    @Test
    @DisplayName("Test that a player can be added to a particular tournament successfully")
    void should_add_player_into_a_specific_tournament_successfully() throws TournamentNotFoundException {
        Tournament tournament = createTournament("LALIGA", 30000, "EUR");

        PlayerRequest playerReq = new PlayerRequest();
        playerReq.setPlayerName("Albert Einstein");

        Tournament tournamentDetailsAfterPlayerAddition = tournamentService.addPlayerIntoTournament(tournament.getTournamentId(), playerReq);

        assertThat(tournamentDetailsAfterPlayerAddition.getTournamentId()).isEqualTo(tournament.getTournamentId());
        assertThat(tournamentDetailsAfterPlayerAddition.getTournamentName()).isEqualTo("LALIGA");
        assertThat(tournamentDetailsAfterPlayerAddition.getRewardAmount()).isEqualTo(30000);
        assertThat(tournamentDetailsAfterPlayerAddition.getCurrency()).isEqualTo("EUR");
        assertThat(tournamentDetailsAfterPlayerAddition.getPlayers().stream()
                .map(Player::getPlayerName)
                .collect(Collectors.toList()))
                .contains("Albert Einstein");
    }

    @Order(6)
    @Test
    @DisplayName("Test that players from a particular tournament can be fetched successfully")
    void should_fetch_players_in_a_specific_tournament_successfully() throws TournamentNotFoundException {
        Tournament tournament = createTournament("LALIGA", 30000, "EUR");
        Tournament tournamentDetailsAfterPlayerAddition = tournamentService.addPlayerIntoTournament(tournament.getTournamentId(), new PlayerRequest("Albert Einstein"));
        List<Player> playerInTournamentList = tournamentService.getPlayersInTournament(tournament.getTournamentId());

        assertThat(playerInTournamentList).isNotEmpty();
        assertThat(playerInTournamentList.stream()
                .map(Player::getPlayerName)
                .collect(Collectors.toList()))
                .contains("Albert Einstein");
        assertThat(tournamentDetailsAfterPlayerAddition.getPlayers().stream()
                .map(Player::getPlayerName)
                .collect(Collectors.toList()))
                .contains("Albert Einstein");

    }

    @Order(7)
    @Test
    @DisplayName("Test that a player from a particular tournament can be removed successfully")
    void should_remove_a_player_in_a_specific_tournament_successfully() throws TournamentNotFoundException, PlayerNotFoundException {
        Tournament tournament = createTournament("LALIGA", 30000, "EUR");
        Tournament tournamentDetailsAfterPlayerAddition = tournamentService.addPlayerIntoTournament(tournament.getTournamentId(), new PlayerRequest("Albert Einstein"));
        String createdPlayerId = tournamentDetailsAfterPlayerAddition.getPlayers().get(0).getPlayerId();
        tournamentService.removePlayerFromTournament(tournament.getTournamentId(), createdPlayerId);

        List<Tournament> tournamentList = tournamentService.getTournaments();

        assertFalse(tournamentList.stream()
                .map(Tournament::getPlayers)
                .collect(Collectors.toList())
                .contains(createdPlayerId));
    }

    @Order(8)
    @Test
    @DisplayName("Test that a particular tournament can be removed successfully")
    void should_remove_a_specific_tournament_successfully() throws TournamentNotFoundException {
        Tournament tournament = createTournament("LALIGA", 30000, "EUR");
        tournamentService.deleteTournament(tournament.getTournamentId());

        List<Tournament> tournamentList = tournamentService.getTournaments();

        assertFalse(tournamentList.stream()
                .map(Tournament::getTournamentId)
                .collect(Collectors.toList())
                .contains(tournament.getTournamentId()));
    }

    @Order(9)
    @Test
    @DisplayName("Test that creation of tournament with non or empty parameters returns error")
    void should_return_an_error_when_creating_new_tournament_with_null_or_empty_parameters() {
        TournamentRequest tournamentRequest = new TournamentRequest();
        tournamentRequest.setTournamentName("");
        tournamentRequest.setRewardAmount(20000);
        tournamentRequest.setCurrency("EUR");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            tournamentService.addTournament(tournamentRequest);
        });
        assertThat(exception.getMessage()).contains("Invalid tournament request: " + tournamentRequest);
    }

    @Order(10)
    @Test
    @DisplayName("Test that creation of tournament with invalid amount returns error")
    void should_return_an_error_when_creating_new_tournament_with_invalid_amount() {
        TournamentRequest tournamentRequest = new TournamentRequest();
        tournamentRequest.setTournamentName("Europa League");
        tournamentRequest.setRewardAmount(-5000);
        tournamentRequest.setCurrency("EUR");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            tournamentService.addTournament(tournamentRequest);
        });
        assertThat(exception.getMessage()).contains("Invalid tournament request: " + tournamentRequest);
    }

    @Order(11)
    @Test
    @DisplayName("Test that fetching of a specific tournament with a non-existent id returns error")
    void should_return_an_error_when_fetching_a_tournament_with_non_existent_id() {
        String tournamentId = "test";
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.getTournament(tournamentId);
        });
        assertThat(exception.getMessage()).contains("Tournament not found with id: " + tournamentId);
    }

    @Order(12)
    @Test
    @DisplayName("Test that the update of a specific tournament with a non-existent id returns error")
    void should_return_an_error_when_updating_a_tournament_with_non_existent_id() {
        String tournamentId = "test";
        Tournament tournament = createTournament("LALIGA", 30000, "EUR");
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.updateTournament(tournamentId, tournament);
        });
        assertThat(exception.getMessage()).contains("Tournament not found with id: " + tournamentId);
    }

    @Order(13)
    @Test
    @DisplayName("Test that adding a player with empty parameters into a tournament returns error")
    void should_return_an_error_when_adding_a_player_into_a_tournament_with_empty_parameter() {
        Tournament tournament = createTournament("LALIGA", 30000, "EUR");
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            tournamentService.addPlayerIntoTournament(tournament.getTournamentId(), new PlayerRequest(""));
        });
        assertThat(exception.getMessage()).contains("Invalid player request: ");
    }

    @Order(14)
    @Test
    @DisplayName("Test that adding a player with null parameters into a tournament returns error")
    void should_return_an_error_when_adding_a_player_into_a_tournament_with_null_parameter() {
        Tournament tournament = createTournament("LALIGA", 30000, "EUR");
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            tournamentService.addPlayerIntoTournament(tournament.getTournamentId(), new PlayerRequest(null));
        });
        assertThat(exception.getMessage()).contains("Invalid player request: ");
    }

    @Order(15)
    @Test
    @DisplayName("Test that fetching players from a tournament with a non-existent id returns error")
    void should_return_an_error_when_fetching_players_from_a_tournament_with_non_existent_id() {
        String tournamentId = "test";
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.getPlayersInTournament(tournamentId);
        });
        assertThat(exception.getMessage()).contains("Tournament not found with id: " + tournamentId);
    }

    @Order(16)
    @Test
    @DisplayName("Test that removing a player from a specific tournament with a non-existent id returns error")
    void should_return_an_error_when_removing_a_player_from_a_tournament_with_non_existent_player_id() throws TournamentNotFoundException {
        String playerId = "test";
        Tournament tournament = createTournament("LALIGA", 30000, "EUR");

        PlayerRequest playerReq = new PlayerRequest();
        playerReq.setPlayerName("Albert Einstein");

        tournamentService.addPlayerIntoTournament(tournament.getTournamentId(), playerReq);

        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class, () -> {
            tournamentService.removePlayerFromTournament(tournament.getTournamentId(), playerId);
        });
        assertThat(exception.getMessage()).contains("Player not found with id: " + playerId);
    }

    @Order(17)
    @Test
    @DisplayName("Test that removing a specific tournament with a non-existent id returns error")
    void should_return_an_error_when_removing_a_tournament_with_non_existent_id() {
        String tournamentId = "test";
        Tournament tournament = createTournament("LALIGA", 30000, "EUR");

        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.deleteTournament(tournamentId);
        });
        assertThat(exception.getMessage()).contains("Tournament not found with id: " + tournamentId);
    }

    private Tournament createTournament(String name, Integer rewardAmount, String currency) {
        TournamentRequest tournamentRequest = new TournamentRequest();
        tournamentRequest.setTournamentName(name);
        tournamentRequest.setRewardAmount(rewardAmount);
        tournamentRequest.setCurrency(currency);
        tournamentRequest.setPlayers(new ArrayList<>());
        return tournamentService.addTournament(tournamentRequest);
    }
}
