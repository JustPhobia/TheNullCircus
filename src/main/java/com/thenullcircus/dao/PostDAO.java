package com.thenullcircus.dao;
import com.thenullcircus.model.Post;

import java.util.ArrayList;
import java.util.UUID;

public interface PostDAO {

    boolean createPost(Post post);
    ArrayList<Post> findAllApproved();
    ArrayList<Post> findAllPending();
    Post findPostById(UUID id);
    Post findJokeOfTheDay();
    boolean approvePost(UUID postId, UUID moderatorId);
    boolean rejectPost(UUID postId, UUID moderatorId);
}
