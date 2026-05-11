package com.thenullcircus.dao;

import com.thenullcircus.model.User;

import java.util.UUID;

public interface UserDao {
    boolean registerUser(User user);
    User findByUsername(String username);
    boolean updateRole(UUID userId,  String newRole);
    boolean updateProfile(UUID userId, String field, String value);
    User findById(UUID userId);
}
