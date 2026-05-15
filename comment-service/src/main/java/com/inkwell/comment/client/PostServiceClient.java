package com.inkwell.comment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.inkwell.comment.common.ApiResponse;
import com.inkwell.comment.dto.PostResponseDTO;

@FeignClient(name = "post-service")
public interface PostServiceClient {

    @GetMapping("/posts/{id}")
    ApiResponse<PostResponseDTO> getPostById(
            @PathVariable Long id
    );
}