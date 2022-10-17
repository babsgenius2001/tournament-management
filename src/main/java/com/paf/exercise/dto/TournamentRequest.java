package com.paf.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentRequest {
    @NotEmpty(message = "Name of Tournament is required!")
    private String tournamentName;
    @Min(value = 1, message = "rewardAmount cannot be less than 1")
    private int rewardAmount;
    @NotEmpty(message = "Currency is required!")
    private String currency;
    private List<PlayerRequest> players;
}
