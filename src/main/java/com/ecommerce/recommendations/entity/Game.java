package com.ecommerce.recommendations.entity;

import lombok.*;

@Data @Builder
public class Game {
    private int    gameId;
    private String name;
    private String about;        // already lemmatised / de-stopworded
    private String categories;   // space-separated tokens
    private String genres;
    private String publishers;
    private double price;

    /** concatenated document string used for TF-IDF */
    public String document() {
        return String.join(" ",
                name == null ? "" : name,
                about == null ? "" : about,
                categories == null ? "" : categories,
                genres == null ? "" : genres,
                publishers == null ? "" : publishers
        ).trim();
    }
}
