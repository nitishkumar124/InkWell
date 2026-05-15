package com.inkwell.comment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inkwell.comment.client.PostServiceClient;
import com.inkwell.comment.common.ApiResponse;
import com.inkwell.comment.dto.PostResponseDTO;
import com.inkwell.comment.entity.Comment;
import com.inkwell.comment.entity.CommentStatus;
import com.inkwell.comment.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostServiceClient postServiceClient;

    @Override
    public Comment addComment(Comment comment) {

        ApiResponse<PostResponseDTO> response =
                postServiceClient.getPostById(comment.getPostId());

        if (response == null || response.getData() == null) {
            throw new RuntimeException("Post not found");
        }

        comment.setPostAuthorId(
                response.getData().getAuthorId()
        );

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
    
    @Override
    public List<Comment> getAllPendingComments() {

        return commentRepository.findByStatus(
                CommentStatus.PENDING
        );
    }
}