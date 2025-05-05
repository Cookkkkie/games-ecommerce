package com.ecommerce.recommendations.repository;

import com.ecommerce.recommendations.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {}