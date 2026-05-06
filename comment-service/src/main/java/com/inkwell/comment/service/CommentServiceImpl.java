package com.inkwell.comment.service;

import com.inkwell.comment.entity.*;
import com.inkwell.comment.repository.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment addComment(Comment comment) {
        comment.setStatus(CommentStatus.PENDING);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdAndStatus(postId, CommentStatus.APPROVED);
    }

    @Override
    public Comment approveComment(Long id) {
        Comment comment = getById(id);
        comment.setStatus(CommentStatus.APPROVED);
        return commentRepository.save(comment);
    }

    @Override
    public Comment rejectComment(Long id) {
        Comment comment = getById(id);
        comment.setStatus(CommentStatus.REJECTED);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }
    
    @Override
    public List<Comment> getPendingCommentsForAuthor(Long authorId) {

        return commentRepository.findByPostAuthorIdAndStatus(
                authorId,
                CommentStatus.PENDING
        );
    }
}