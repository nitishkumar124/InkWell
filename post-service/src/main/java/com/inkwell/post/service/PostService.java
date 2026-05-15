package com.inkwell.post.service;

import java.util.List;

import com.inkwell.post.dto.PostResponse;
import com.inkwell.post.entity.Post;

public interface PostService {

    Post createPost(Post post);

    Post publishPost(Long id);

    List<PostResponse> getAllPublishedPosts();
    PostResponse getPostById(Long id);

    void deletePost(Long id);

    Post featurePost(Long id);

    String toggleLike(Long postId, Long userId);

    long getLikeCount(Long postId);
    
    List<PostResponse> getPostsByAuthor(Long authorId);
    
    void incrementView(Long postId, Long userId);
}