package com.ecommerce.recommendations.controller;

import com.ecommerce.recommendations.entity.LikeEntity;
import com.ecommerce.recommendations.repository.LikeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeRepository likeRepo;
    public LikeController(LikeRepository likeRepo) {
        this.likeRepo = likeRepo;
    }

    @GetMapping
    public List<LikeEntity> allLikes() {
        return likeRepo.findAll();
    }
}
