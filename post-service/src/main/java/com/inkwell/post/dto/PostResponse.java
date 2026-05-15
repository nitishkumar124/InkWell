package com.inkwell.post.dto;

import com.inkwell.post.entity.PostStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponse {

	private Long id;
	private String title;
	private String content;
	private String imageUrl;
	private Long authorId;
	private long viewCount;
	private boolean isFeatured;
	private long likeCount;
	private PostStatus status;
}