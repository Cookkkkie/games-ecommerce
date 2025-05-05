package com.ecommerce.recommendations.services;

import com.ecommerce.recommendations.entity.Game;
import com.ecommerce.recommendations.repository.DataLoader;
import com.ecommerce.recommendations.utility.TfidfVectorizer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    private final DataLoader  loader;
    private final GameService gameService;   // <-- NEW

    public RecommendationService(DataLoader loader, GameService gameService) {
        this.loader       = loader;
        this.gameService  = gameService;
    }

    public List<TfidfVectorizer.IdxScore> recommend(int gameId, int k) {

        Integer idx = loader.getCleanIndexOfGame().get(gameId);
        if (idx == null)
            throw new IllegalArgumentException("Unknown gameId " + gameId);

        List<Integer> candidates = gameService.random25CleanIndices();

        return candidates.stream()
                .filter(j -> j != idx)                                   // no self
                .map(j -> new TfidfVectorizer.IdxScore(
                        j, loader.getVectorizer().similarity(idx, j)))   // score
                .sorted((a, b) -> Double.compare(b.score(), a.score()))  // desc
                .limit(k)
                .toList();
    }


    public Game cleanGameByIndex(int idx) {
        return loader.getCleanGames().get(idx);
    }
}
