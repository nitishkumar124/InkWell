package com.inkwell.comment.repository;

import com.inkwell.comment.entity.Comment;
import com.inkwell.comment.entity.CommentStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdAndStatus(Long postId, CommentStatus status);
    
    List<Comment> findByPostAuthorIdAndStatus(
            Long postAuthorId,
            CommentStatus status
    );
}