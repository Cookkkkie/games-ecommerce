package com.ecommerce.recommendations.controller;

import com.ecommerce.recommendations.services.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService service;

    @Autowired
    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/recommendations/{gameId}")
    public List<RecommendationDTO> recommend(@PathVariable int gameId,
                                             @RequestParam(defaultValue = "10") int k) {
        return service.recommend(gameId, k).stream()
                .map(s -> {
                    var g = service.cleanGameByIndex(s.index());
                    return new RecommendationDTO(g.getGameId(), g.getName(), s.score());
                })
                .toList();
    }

    public record RecommendationDTO(int gameId, String name, double score) {}
}
