package com.inkwell.comment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private Long userId;
    
    private Long postAuthorId;

    @Column(length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    private LocalDateTime createdAt;
}