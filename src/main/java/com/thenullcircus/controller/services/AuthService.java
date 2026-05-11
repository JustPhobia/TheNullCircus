package com.thenullcircus.controller.services;

import com.thenullcircus.dao.UserDao;
import com.thenullcircus.model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.util.logging.Logger;

public class AuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String username, String password) {
        logger.info("Login attempt for user: " + username);
        User user = userDao.findByUsername(username);
        if (user == null) {
            logger.warning("Authentication failed: User " + username + " does not exist.");
            return null;
        }

        if (BCrypt.checkpw(password, user.getPassword())) {
            logger.info("Authentication successful for user: " + username);
            return user;
        } else {
            logger.warning("Authentication failed: Invalid password for user " + username);
        }
        return null;
    }

    public boolean register(User user) {
        logger.info("Registering new user profile: " + user.getUsername());
        if (userDao.findByUsername(user.getUsername()) != null) {
            logger.warning("Registration failed: Username " + user.getUsername() + " is already taken.");
            return false;
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        boolean result = userDao.registerUser(user);
        logger.info("Registration final result for " + user.getUsername() + ": " + result);
        return result;
    }
}