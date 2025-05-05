package com.ecommerce.recommendations.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "user_likes")
@Data
public class LikeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int mainGameId;
    private int recommendedGameId;
    private boolean liked;
    private Instant timestamp = Instant.now();

    // getters/setters
}