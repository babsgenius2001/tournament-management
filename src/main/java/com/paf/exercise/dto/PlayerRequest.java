package com.paf.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRequest {
    @NotBlank(message = "Name of the Player is required!")
    public String playerName;
}
