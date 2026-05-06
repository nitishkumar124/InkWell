package com.inkwell.post.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.inkwell.post.common.ApiResponse;
import com.inkwell.post.dto.PostResponse;
import com.inkwell.post.entity.Post;
import com.inkwell.post.service.PostService;
import com.inkwell.post.util.FileStorageUtil;
import com.inkwell.post.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/posts")
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private FileStorageUtil fileStorageUtil;

	@Autowired
	private JwtUtil jwtUtil;

	// COMMON METHOD
	private String getRole(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("Missing or invalid Authorization header");
		}

		String token = authHeader.substring(7);
		return jwtUtil.extractRole(token);
	}

	private Long getUserId(HttpServletRequest request) {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("Missing or invalid Authorization header");
		}

		String token = authHeader.substring(7);

		Long userId = jwtUtil.extractUserId(token);

		if (userId == null) {
			throw new RuntimeException("Invalid token: userId missing");
		}

		return userId;
	}

	// CREATE POST
	@PostMapping(consumes = "multipart/form-data")
	public ApiResponse<Post> createPost(@RequestParam String title, @RequestParam String content,
			@RequestParam(required = false) MultipartFile image, HttpServletRequest request) {

		String role = getRole(request);
		Long userId = getUserId(request);

		if (!"AUTHOR".equals(role)) {
			throw new RuntimeException("Only AUTHOR can create posts");
		}

		String imageUrl = null;

		if (image != null && !image.isEmpty()) {
			imageUrl = fileStorageUtil.saveFile(image);
		}

		Post post = Post.builder().title(title).content(content).authorId(userId) // ✅ FROM JWT
				.imageUrl(imageUrl).build();

		Post saved = postService.createPost(post);

		return ApiResponse.<Post>builder().success(true).message("Post created successfully").data(saved).build();
	}

	// PUBLISH POST
	@PutMapping("/{id}/publish")
	public ApiResponse<Post> publishPost(@PathVariable Long id, HttpServletRequest request) {

		String role = getRole(request);

		if (!"AUTHOR".equals(role)) {
			throw new RuntimeException("Only AUTHOR can publish posts");
		}

		Post post = postService.publishPost(id);

		return ApiResponse.<Post>builder().success(true).message("Post published successfully").data(post).build();
	}

	// GET ALL POSTS
	@GetMapping
	public ApiResponse<List<PostResponse>> getAllPosts() {

		List<PostResponse> posts = postService.getAllPublishedPosts();

		return ApiResponse.<List<PostResponse>>builder().success(true).message("Posts fetched successfully").data(posts)
				.build();
	}

	// GET LOGGED-IN AUTHOR POSTS
	@GetMapping("/author")
	public ApiResponse<List<PostResponse>> getAuthorPosts(HttpServletRequest request) {

		String role = getRole(request);
		Long userId = getUserId(request);

		if (!"AUTHOR".equals(role)) {
			throw new RuntimeException("Only AUTHOR can access own posts");
		}

		List<PostResponse> posts = postService.getPostsByAuthor(userId);

		return ApiResponse.<List<PostResponse>>builder().success(true).message("Author posts fetched successfully")
				.data(posts).build();
	}

	// GET SINGLE POST
	@GetMapping("/{id}")
	public ApiResponse<PostResponse> getPost(@PathVariable Long id) {

		PostResponse post = postService.getPostById(id);

		return ApiResponse.<PostResponse>builder().success(true).message("Post fetched successfully").data(post)
				.build();
	}

	// DELETE POST
	@DeleteMapping("/{id}")
	public ApiResponse<Void> deletePost(@PathVariable Long id, HttpServletRequest request) {

		String role = getRole(request);

		if (!"AUTHOR".equals(role) && !"ADMIN".equals(role)) {
			throw new RuntimeException("Unauthorized");
		}

		postService.deletePost(id);

		return ApiResponse.<Void>builder().success(true).message("Post deleted successfully").data(null).build();
	}

	// FEATURE POST
	@PutMapping("/{id}/feature")
	public ApiResponse<Post> featurePost(@PathVariable Long id, HttpServletRequest request) {

		String role = getRole(request);

		if (!"ADMIN".equals(role)) {
			throw new RuntimeException("Only ADMIN can feature posts");
		}

		Post post = postService.featurePost(id);

		return ApiResponse.<Post>builder().success(true).message("Post featured successfully").data(post).build();
	}

	// LIKE / UNLIKE
	@PostMapping("/{id}/like")
	public ApiResponse<String> likePost(@PathVariable Long id, HttpServletRequest request) {

		String role = getRole(request);
		Long userId = getUserId(request);

		if (!"READER".equals(role) && !"AUTHOR".equals(role)) {
			throw new RuntimeException("Unauthorized");
		}

		String result = postService.toggleLike(id, userId);

		return ApiResponse.<String>builder().success(true).message("Like updated successfully").data(result).build();
	}

	// LIKE COUNT
	@GetMapping("/{id}/likes")
	public ApiResponse<Long> getLikes(@PathVariable Long id) {

		long count = postService.getLikeCount(id);

		return ApiResponse.<Long>builder().success(true).message("Like count fetched").data(count).build();
	}
}