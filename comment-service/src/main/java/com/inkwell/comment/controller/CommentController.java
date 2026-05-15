package com.inkwell.comment.controller;

import com.inkwell.comment.common.ApiResponse;
import com.inkwell.comment.entity.Comment;
import com.inkwell.comment.service.CommentService;
import com.inkwell.comment.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtil jwtUtil;

    private String getRole(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = auth.substring(7);
        return jwtUtil.extractRole(token);
    }

    private Long getUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = auth.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId missing");
        }
        return userId;
    }

    // ADD COMMENT
    @PostMapping
    public ApiResponse<Comment> addComment(@RequestBody Comment comment,
                                           HttpServletRequest request) {

        String role = getRole(request);
        Long userId = getUserId(request);

        if (!"READER".equals(role) && !"AUTHOR".equals(role)) {
            throw new RuntimeException("Only users can comment");
        }

        // 🔥 set from JWT (not from frontend)
        comment.setUserId(userId);

        Comment saved = commentService.addComment(comment);

        return ApiResponse.<Comment>builder()
                .success(true)
                .message("Comment added successfully")
                .data(saved)
                .build();
    }

    // GET COMMENTS
    @GetMapping("/post/{postId}")
    public ApiResponse<List<Comment>> getComments(@PathVariable Long postId) {

        List<Comment> comments = commentService.getCommentsByPost(postId);

        return ApiResponse.<List<Comment>>builder()
                .success(true)
                .message("Comments fetched successfully")
                .data(comments)
                .build();
    }

    // APPROVE (ADMIN OR POST AUTHOR)
    @PutMapping("/{id}/approve")
    public ApiResponse<Comment> approve(@PathVariable Long id,
                                        HttpServletRequest request) {

        String role = getRole(request);
        Long userId = getUserId(request);

        Comment comment = commentService.getById(id);

        if (!"ADMIN".equals(role) && !userId.equals(comment.getPostAuthorId())) {
            throw new RuntimeException("Unauthorized");
        }

        Comment updated = commentService.approveComment(id);

        return ApiResponse.<Comment>builder()
                .success(true)
                .message("Comment approved")
                .data(updated)
                .build();
    }

    // REJECT (ADMIN OR POST AUTHOR)
    @PutMapping("/{id}/reject")
    public ApiResponse<Comment> reject(@PathVariable Long id,
                                       HttpServletRequest request) {

        String role = getRole(request);
        Long userId = getUserId(request);

        Comment comment = commentService.getById(id);

        if (!"ADMIN".equals(role) && !userId.equals(comment.getPostAuthorId())) {
            throw new RuntimeException("Unauthorized");
        }

        Comment updated = commentService.rejectComment(id);

        return ApiResponse.<Comment>builder()
                .success(true)
                .message("Comment rejected")
                .data(updated)
                .build();
    }

    // DELETE (ADMIN ONLY)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                   HttpServletRequest request) {

        String role = getRole(request);

        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Only ADMIN can delete");
        }

        commentService.deleteComment(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Comment deleted")
                .data(null)
                .build();
    }
    
 // GET PENDING COMMENTS FOR LOGGED-IN AUTHOR
    @GetMapping("/pending")
    public ApiResponse<List<Comment>> getPendingComments(
            HttpServletRequest request) {

        String role = getRole(request);
        Long userId = getUserId(request);

        if (!"AUTHOR".equals(role)) {
            throw new RuntimeException("Only AUTHOR can access pending comments");
        }

        List<Comment> comments =
                commentService.getPendingCommentsForAuthor(userId);

        return ApiResponse.<List<Comment>>builder()
                .success(true)
                .message("Pending comments fetched successfully")
                .data(comments)
                .build();
    }
    
 // GET ALL PENDING COMMENTS (ADMIN)
    @GetMapping("/pending/all")
    public ApiResponse<List<Comment>> getAllPendingComments(
            HttpServletRequest request) {

        String role = getRole(request);

        if (!"ADMIN".equals(role)) {
            throw new RuntimeException(
                    "Only ADMIN can access all pending comments"
            );
        }

        List<Comment> comments =
                commentService.getAllPendingComments();

        return ApiResponse.<List<Comment>>builder()
                .success(true)
                .message("All pending comments fetched successfully")
                .data(comments)
                .build();
    }
}