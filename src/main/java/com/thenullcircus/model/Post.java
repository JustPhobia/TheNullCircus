package com.thenullcircus.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Post {
    private UUID postId;
    private UUID userId;
    private String body;
    private String comments;
    private Status status;
    private String moderatorId;
    private LocalDateTime timestamp;
    private int upvotes;
    private int downvotes;

    public Post(UUID userId, String body, String comments, Status status, String moderatorId, LocalDateTime timestamp) {
        this.postId = UUID.randomUUID();
        this.userId = userId;
        this.body = body;
        this.comments = comments;
        this.status = status;
        this.moderatorId = moderatorId;
        this.timestamp = timestamp;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    public Post(UUID postId, UUID userId, String body, String comments, Status status, String moderatorId, LocalDateTime timestamp, int upvotes, int downvotes) {
        this.postId = postId;
        this.userId = userId;
        this.body = body;
        this.comments = comments;
        this.status = status;
        this.moderatorId = moderatorId;
        this.timestamp = timestamp;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }
}
