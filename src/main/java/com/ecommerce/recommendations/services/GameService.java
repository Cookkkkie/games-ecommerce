package com.ecommerce.recommendations.services;

import com.ecommerce.recommendations.entity.Game;
import com.ecommerce.recommendations.repository.DataLoader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.shuffle;

@Service
public class GameService {
    private final DataLoader loader;
    private List<Game> random25Games;
    private List<Integer> random25CleanIdx;
    public GameService(DataLoader loader) {
        this.loader = loader;
    }


    public synchronized List<Game> allGamesClean() {
        if (random25Games == null) {
            List<Game> copy = new ArrayList<>(loader.getCleanGames());
            Collections.shuffle(copy);
            random25Games = Collections.unmodifiableList(copy.subList(0, Math.min(25, copy.size())));
        }
        return random25Games;
    }
    public synchronized List<Integer> random25CleanIndices() {
        if (random25CleanIdx == null) {
            random25CleanIdx = allGamesClean().stream()
                    .map(g -> loader.getCleanIndexOfGame().get(g.getGameId()))
                    .toList();
        }
        return random25CleanIdx;
    }

    public Game findCleanById(int gameId) {
        return loader.getCleanGames().stream()
                .filter(g -> g.getGameId() == gameId)
                .findFirst()
                .orElseThrow();
    }

    public List<Game> allGames() {
        return loader.getRawGames();
    }
    public Game findById(int gameId) {
        return loader.getRawGames().stream()
                .filter(g -> g.getGameId() == gameId)
                .findFirst()
                .orElseThrow();
    }

}