package com.thenullcircus.dao;
import com.thenullcircus.model.Post;

import java.util.ArrayList;

public interface PostDAO {

    boolean createPost(Post post);
    ArrayList<Post> findAllApproved();
    ArrayList<Post> findAllPending();
    Post findPostById(int id);
}
