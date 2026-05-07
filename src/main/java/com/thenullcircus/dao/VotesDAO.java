package com.thenullcircus.dao;

import com.thenullcircus.model.VoteType;

import java.util.UUID;

public interface VotesDAO {
    boolean upvotePost(UUID postId, UUID userId);
    boolean downvotePost(UUID postId, UUID userId);
    VoteType hasVoted(UUID postId, UUID userId);
}
