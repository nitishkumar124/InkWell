package com.inkwell.comment.service;

import com.inkwell.comment.entity.Comment;

import java.util.List;

public interface CommentService {

    Comment addComment(Comment comment);

    List<Comment> getCommentsByPost(Long postId);

    Comment approveComment(Long id);

    Comment rejectComment(Long id);

    void deleteComment(Long id);

    Comment getById(Long id);
    
    List<Comment> getPendingCommentsForAuthor(Long authorId);
}