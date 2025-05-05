package com.ecommerce.recommendations.services;

import com.ecommerce.recommendations.entity.Game;
import com.ecommerce.recommendations.repository.DataLoader;
import com.ecommerce.recommendations.utility.TfidfVectorizer;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendationService {

    private final DataLoader loader;
    private final GameService gameService;

    public List<TfidfVectorizer.IdxScore> recommend(int gameId, int k) {

        Integer idx = loader.getCleanIndexOfGame().get(gameId);
        if (idx == null)
            throw new IllegalArgumentException("Unknown gameId " + gameId);

        List<Integer> candidates = gameService.random25CleanIndices();

        return candidates.stream()
                .filter(j -> j != idx)
                .map(j -> new TfidfVectorizer.IdxScore(
                        j, loader.getVectorizer().similarity(idx, j)))
                .sorted((a, b) -> Double.compare(b.score(), a.score()))
                .limit(k)
                .toList();
    }


    public Game cleanGameByIndex(int idx) {
        return loader.getCleanGames().get(idx);
    }
}
