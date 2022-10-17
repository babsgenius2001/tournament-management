package com.paf.exercise;

import com.google.gson.Gson;
import com.paf.exercise.dto.PlayerRequest;
import com.paf.exercise.dto.TournamentRequest;
import com.paf.exercise.entity.Player;
import com.paf.exercise.entity.Tournament;
import com.paf.exercise.exceptions.TournamentNotFoundException;
import com.paf.exercise.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExerciseControllerTests {
    @LocalServerPort
    private int port;

    private String getRootUrl() {
        return "http://localhost:" + port;
    }

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TournamentService tournamentService;

    @Test
    void testAddNewTournament() throws Exception {
        TournamentRequest tournamentRequest = new TournamentRequest();
        tournamentRequest.setTournamentName("NPL");
        tournamentRequest.setRewardAmount(20000);
        tournamentRequest.setCurrency("EUR");

        Tournament expectedResponse = new Tournament();
        expectedResponse.setTournamentName("NPL");
        expectedResponse.setRewardAmount(20000);
        expectedResponse.setCurrency("EUR");
        expectedResponse.setPlayers(new ArrayList<>());
        System.out.println("url to use is::: " + getRootUrl());

        when(tournamentService.addTournament(tournamentRequest)).thenReturn(expectedResponse);
        this.mockMvc.perform(post(getRootUrl() + "/api/addTournament")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(tournamentRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(String.valueOf(new Gson().toJson(expectedResponse))));
    }

    @Test
    void testGetAllTournaments() throws Exception {
        List<Tournament> expectedResponse = new ArrayList<>();
        Tournament tournament = new Tournament();
        tournament.setTournamentName("NPL");
        tournament.setRewardAmount(2000);
        tournament.setCurrency("EUR");
        tournament.setPlayers(new ArrayList<>());
        expectedResponse.add(tournament);

        when(tournamentService.getTournaments()).thenReturn(expectedResponse);
        this.mockMvc.perform(get(getRootUrl() + "/api/getTournaments")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((expectedResponse.size())))
                .andExpect(jsonPath("$.*").isNotEmpty())
                .andExpect(content().json(String.valueOf(new Gson().toJson(expectedResponse))));
    }

    @Test
    void testGetParticularTournamentWithId() throws Exception {
        String tournamentId = UUID.randomUUID().toString();
        Tournament expectedResponse = new Tournament(1L,tournamentId, "NPL", 20000, "EUR", new ArrayList<>());

        when(tournamentService.getTournament(tournamentId)).thenReturn(expectedResponse);
        this.mockMvc.perform(get(getRootUrl() + "/api/getTournament")
                        .queryParam("tournamentId", tournamentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(String.valueOf(new Gson().toJson(expectedResponse))));
    }

    @Test
    void testUpdateParticularTournamentWithId() throws Exception {
        String tournamentId = UUID.randomUUID().toString();
        Tournament expectedResponse = new Tournament(1L,tournamentId, "NPL", 59900, "EUR", new ArrayList<>());
        Tournament updatedTournament = new Tournament();
        updatedTournament.setTournamentId(tournamentId);
        updatedTournament.setTournamentName("NPL");
        updatedTournament.setRewardAmount(59900);
        updatedTournament.setCurrency("EUR");
        updatedTournament.setPlayers(new ArrayList<>());

        when(tournamentService.updateTournament(tournamentId, updatedTournament)).thenReturn(expectedResponse);
        this.mockMvc.perform(put(getRootUrl() + "/api/updateTournament")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("tournamentId", tournamentId)
                        .content(new Gson().toJson(updatedTournament)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(String.valueOf(new Gson().toJson(expectedResponse))));
    }

    @Test
    void testAddPlayerIntoATournament() throws Exception {
        String tournamentId = UUID.randomUUID().toString();
        String playerId = UUID.randomUUID().toString();
        PlayerRequest player = new PlayerRequest();
        player.setPlayerName("Albert Einstein");

        Tournament expectedResponse = new Tournament(1L, tournamentId, "NPL", 20000, "EUR", List.of(new Player(1L,playerId, "Albert Einstein")));

        when(tournamentService.addPlayerIntoTournament(tournamentId, player)).thenReturn(expectedResponse);
        this.mockMvc.perform(post(getRootUrl() + "/api/addPlayerIntoTournament")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("tournamentId", tournamentId)
                        .content(new Gson().toJson(player)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(String.valueOf(new Gson().toJson(expectedResponse))));
    }

    @Test
    void testGetAllPlayersInAParticularTournamentWithId() throws Exception {
        String tournamentId = UUID.randomUUID().toString();
        String playerId = UUID.randomUUID().toString();
        List<Player> expectedResponse = new ArrayList<>();
        expectedResponse.add(new Player(1L, playerId, "Albert Einstein"));

        when(tournamentService.getPlayersInTournament(tournamentId)).thenReturn(expectedResponse);
        this.mockMvc.perform(get(getRootUrl() + "/api/getPlayersInTournament")
                        .queryParam("tournamentId", tournamentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((expectedResponse.size())))
                .andExpect(jsonPath("$.*").isNotEmpty())
                .andExpect(content().json(String.valueOf(new Gson().toJson(expectedResponse))));
    }

    @Test
    void testRemoveAPlayerFromAParticularTournament() throws Exception {
        String tournamentId = UUID.randomUUID().toString();
        String playerId = UUID.randomUUID().toString();
        this.mockMvc.perform(delete(getRootUrl() + "/api/removePlayerFromTournament")
                        .queryParam("tournamentId", tournamentId)
                        .queryParam("playerId", playerId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveAParticularTournamentWithId() throws Exception {
        String tournamentId = UUID.randomUUID().toString();
        this.mockMvc.perform(delete(getRootUrl() + "/api/removeTournament")
                .queryParam("tournamentId", tournamentId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testAddNewTournamentWithNullOrEmptyRequestParameters() throws Exception {
        TournamentRequest tournamentRequest = new TournamentRequest();
        tournamentRequest.setTournamentName("");
        tournamentRequest.setRewardAmount(20000);
        tournamentRequest.setCurrency("EUR");

        List<String> errorResponse = new ArrayList<>();
        errorResponse.add("tournamentRequest : Name of Tournament is required!");

        this.mockMvc.perform(post(getRootUrl() + "/api/addTournament")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(tournamentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(errorResponse));
    }

    @Test
    void testAddNewTournamentWithInvalidAmount() throws Exception {
        TournamentRequest tournamentRequest = new TournamentRequest();
        tournamentRequest.setTournamentName("Europa League");
        tournamentRequest.setRewardAmount(-5000);
        tournamentRequest.setCurrency("EUR");

        List<String> errorResponse = new ArrayList<>();
        errorResponse.add("tournamentRequest : rewardAmount cannot be less than 1");

        this.mockMvc.perform(post(getRootUrl() + "/api/addTournament")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(tournamentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(errorResponse));
    }

    @Test
    void testGetParticularTournamentWithIdNotFound() throws Exception {
        List<String> errorResponse = new ArrayList<>();
        errorResponse.add("Tournament not found with id: 1000");

        when(tournamentService.getTournament("1000")).thenThrow(new TournamentNotFoundException("Tournament not found with id: 1000"));
        this.mockMvc.perform(get(getRootUrl() + "/api/getTournament")
                        .queryParam("tournamentId", "1000"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(errorResponse));
    }

    @Test
    void testUpdateParticularTournamentWithIdNotFound() throws Exception {
        Tournament updatedTournament = new Tournament();
        updatedTournament.setTournamentId("100");
        updatedTournament.setTournamentName("NPL");
        updatedTournament.setRewardAmount(59900);
        updatedTournament.setCurrency("EUR");
        updatedTournament.setPlayers(new ArrayList<>());

        List<String> errorResponse = new ArrayList<>();
        errorResponse.add("Tournament not found with id: 100");

        when(tournamentService.updateTournament("100", updatedTournament)).thenThrow(new TournamentNotFoundException("Tournament not found with id: 100"));
        this.mockMvc.perform(put(getRootUrl() + "/api/updateTournament")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("tournamentId", "100")
                        .content(new Gson().toJson(updatedTournament)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(errorResponse));
    }

    @Test
    void testAddPlayerIntoATournamentWithEmptyRequestParameters() throws Exception {
        String tournamentId = UUID.randomUUID().toString();
        PlayerRequest player = new PlayerRequest();
        player.setPlayerName("");

        List<String> errorResponse = new ArrayList<>();
        errorResponse.add("playerRequest : Name of the Player is required!");

        this.mockMvc.perform(post(getRootUrl() + "/api/addPlayerIntoTournament")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("tournamentId", tournamentId)
                        .content(new Gson().toJson(player)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(errorResponse));
    }

    @Test
    void testAddPlayerIntoATournamentWithNullRequestParameters() throws Exception {
        String tournamentId = UUID.randomUUID().toString();
        PlayerRequest player = new PlayerRequest();
        player.setPlayerName(null);

        List<String> errorResponse = new ArrayList<>();
        errorResponse.add("playerRequest : Name of the Player is required!");

        this.mockMvc.perform(post(getRootUrl() + "/api/addPlayerIntoTournament")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("tournamentId", tournamentId)
                        .content(new Gson().toJson(player)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(errorResponse));
    }

    @Test
    void testGetPlayersInAParticularTournamentWithUnExistingId() throws Exception {
        List<String> errorResponse = new ArrayList<>();
        errorResponse.add("Tournament not found with id: 1000");

        when(tournamentService.getPlayersInTournament("1000")).thenThrow(new TournamentNotFoundException("Tournament not found with id: 1000"));
        this.mockMvc.perform(get(getRootUrl() + "/api/getPlayersInTournament")
                        .queryParam("tournamentId", "1000"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(errorResponse));
    }

    @Test
    void testInvalidMethodCall() throws Exception {
        this.mockMvc.perform(post(getRootUrl() + "/api/getPlayersInTournament")
                        .queryParam("tournamentId", "1000"))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }
}
