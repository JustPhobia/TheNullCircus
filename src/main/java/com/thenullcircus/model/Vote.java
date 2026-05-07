package com.thenullcircus.model;

import lombok.Data;

import java.util.UUID;
@Data
public class Vote {
    private  UUID voteId;
    private  UUID postId;
    private  UUID userId;
    private  VoteType type;

    public Vote(UUID voteId, UUID postId, UUID userId, VoteType type) {
        this.voteId = voteId;
        this.postId = postId;
        this.userId = userId;
        this.type = type;
    }
    public Vote(UUID postId, UUID userId, VoteType type) {
        this.voteId = UUID.randomUUID();
        this.postId = postId;
        this.userId = userId;
        this.type = type;
    }
}
