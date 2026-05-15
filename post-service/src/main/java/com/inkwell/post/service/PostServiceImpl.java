package com.inkwell.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inkwell.post.dto.PostResponse;
import com.inkwell.post.entity.Post;
import com.inkwell.post.entity.PostLike;
import com.inkwell.post.entity.PostStatus;
import com.inkwell.post.entity.PostView;
import com.inkwell.post.repository.PostLikeRepository;
import com.inkwell.post.repository.PostRepository;
import com.inkwell.post.repository.PostViewRepository;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PostLikeRepository likeRepository;
	
	@Autowired
	private PostViewRepository postViewRepository;

	// ✅ ONLY business logic here
	@Override
	public Post createPost(Post post) {

		post.setStatus(PostStatus.DRAFT);
		post.setCreatedAt(LocalDateTime.now());

		return postRepository.save(post);
	}

	@Override
	public Post publishPost(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

		post.setStatus(PostStatus.PUBLISHED);
		return postRepository.save(post);
	}

	@Override
	public List<PostResponse> getAllPublishedPosts() {
		return postRepository.findByStatus(PostStatus.PUBLISHED).stream().map(this::mapToResponse).toList();
	}

	@Override
	public PostResponse getPostById(Long id) {

		Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

//		post.setViewCount(post.getViewCount() + 1);
//		postRepository.save(post);

		return mapToResponse(post);
	}

	@Override
	public void deletePost(Long id) {
		postRepository.deleteById(id);
	}

	@Override
	public Post featurePost(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

		post.setFeatured(true);
		return postRepository.save(post);
	}

	@Override
	public String toggleLike(Long postId, Long userId) {

		var existing = likeRepository.findByPostIdAndUserId(postId, userId);

		if (existing.isPresent()) {
			likeRepository.delete(existing.get());
			return "Unliked";
		} else {
			PostLike like = PostLike.builder().postId(postId).userId(userId).build();

			likeRepository.save(like);
			return "Liked";
		}
	}

	@Override
	public List<PostResponse> getPostsByAuthor(Long authorId) {

		return postRepository.findByAuthorId(authorId).stream().map(this::mapToResponse).toList();
	}

	@Override
	public long getLikeCount(Long postId) {
		return likeRepository.countByPostId(postId);
	}
	
	@Override
	public void incrementView(Long postId, Long userId) {

	    // guests don't count
	    if (userId == null) {
	        return;
	    }

	    boolean alreadyViewed =
	        postViewRepository
	            .existsByPostIdAndUserId(
	                postId,
	                userId
	            );

	    if (alreadyViewed) {
	        return;
	    }

	    Post post = postRepository.findById(postId)
	        .orElseThrow(() ->
	            new RuntimeException("Post not found"));

	    post.setViewCount(
	        post.getViewCount() + 1
	    );

	    postRepository.save(post);

	    PostView view = PostView.builder()
	        .postId(postId)
	        .userId(userId)
	        .build();

	    postViewRepository.save(view);
	}

	private PostResponse mapToResponse(Post post) {
		return PostResponse.builder().id(post.getId()).title(post.getTitle()).content(post.getContent())
				.imageUrl(post.getImageUrl()).authorId(post.getAuthorId()).viewCount(post.getViewCount())
				.isFeatured(post.isFeatured()).likeCount(likeRepository.countByPostId(post.getId())).status(post.getStatus()).build();
	}
}