package com.ecommerce.recommendations.controller;

import com.ecommerce.recommendations.entity.Game;
import com.ecommerce.recommendations.services.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class MainController {

    private final GameService gameService;
    public MainController(GameService gs) { this.gameService = gs; }

    @GetMapping("/")
    public String home(Model model) {
        List<Game> games = gameService.allGamesClean().stream()
                .filter(g -> g.getGenres().matches(".*(action|indie|adventure|casual).*$"))
                .toList();
        model.addAttribute("games", games);
        return "index";
    }
}