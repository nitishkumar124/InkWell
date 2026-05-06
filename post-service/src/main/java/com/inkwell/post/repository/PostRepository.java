package com.inkwell.post.repository;

import com.inkwell.post.entity.Post;
import com.inkwell.post.entity.PostStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByStatus(PostStatus status);

	List<Post> findByIsFeaturedTrue();

	List<Post> findByAuthorId(Long authorId);
}
