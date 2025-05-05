package com.ecommerce.recommendations.controller;


import com.ecommerce.recommendations.entity.Game;
import com.ecommerce.recommendations.entity.LikeEntity;
import com.ecommerce.recommendations.repository.LikeRepository;
import com.ecommerce.recommendations.services.GameService;
import com.ecommerce.recommendations.services.RecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/games")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class GameController {

    private final GameService gameService;
    private final RecommendationService recService;
    private final LikeRepository likeRepo;

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") int id, Model model) {
        Game game = gameService.findById(id);
        List<RecommendationView> recViews = recService.recommend(id, 3).stream()
                .map(s -> new RecommendationView(
                        gameService.findById(
                                gameService.allGames().get(s.index()).getGameId()
                        ),
                        s.score()
                ))
                .collect(Collectors.toList());

        model.addAttribute("game", game);
        model.addAttribute("recs", recViews);
        return "detail";
    }

    @PostMapping("/{id}/likes")
    @ResponseBody
    public void like(@PathVariable("id") int mainId,
                     @RequestParam int recId,
                     @RequestParam boolean liked) {
        LikeEntity e = new LikeEntity();
        e.setMainGameId(mainId);
        e.setRecommendedGameId(recId);
        e.setLiked(liked);
        likeRepo.save(e);
    }

    public static class RecommendationView {
        private final Game game;
        private final double score;

        public RecommendationView(Game game, double score) {
            this.game = game;
            this.score = score;
        }
        public Game getGame() { return game; }
        public double getScore() { return score; }
    }
}