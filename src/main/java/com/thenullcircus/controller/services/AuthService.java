package com.thenullcircus.controller.services;

import com.thenullcircus.dao.UserDao;
import com.thenullcircus.dao.UserDaoImpl;
import com.thenullcircus.model.User;

public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String username, String password){
        User user = userDao.findByUsername(username);
        if(user == null){
            return null;
        }

        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean register(User user){
        if (userDao.findByUsername(user.getUsername()) != null){
            return false;
        }
        boolean result = userDao.registerUser(user);

        System.out.println("DAO registration result: " + result);

        return result;
    }
}
