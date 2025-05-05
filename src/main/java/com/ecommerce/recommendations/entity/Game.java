package com.ecommerce.recommendations.entity;

import lombok.*;

@Data @Builder
public class Game {
    private int    gameId;
    private String name;
    private String price;
    private String about;
    private String categories;
    private String genres;
    private String publishers;

    /** concatenated document string used for TF-IDF */
    public String document() {
        return String.join(" ",
                name == null ? "" : name,
                price == null ? "" : price,
                about == null ? "" : about,
                categories == null ? "" : categories,
                genres == null ? "" : genres,
                publishers == null ? "" : publishers
        ).trim();
    }
}
