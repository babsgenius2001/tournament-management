package com.paf.exercise.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "TOURNAMENT_TBL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String tournamentId;
    @Column(unique = true, nullable = false)
    private String tournamentName;
    private int rewardAmount;
    private String currency;
    @ManyToMany
    private List<Player> players;
}
