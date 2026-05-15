package com.inkwell.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inkwell.post.entity.PostView;

public interface PostViewRepository
        extends JpaRepository<PostView, Long> {

    boolean existsByPostIdAndUserId(
        Long postId,
        Long userId
    );
}