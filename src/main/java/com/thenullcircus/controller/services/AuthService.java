package com.thenullcircus.controller.services;

import com.thenullcircus.dao.UserDao;
import com.thenullcircus.dao.UserDaoImpl;
import com.thenullcircus.model.User;

public class AuthService {

    public static UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public static boolean login(String username, String password){
        User user = userDao.findByUsername(username);
        if(user == null){
            return false;
        }

        return user.getPassword().equals(password);
    }

    public static boolean register(User user){
        if (userDao.findByUsername(user.getUsername()) != null){
            return false;
        }
        return userDao.registerUser(user);
    }
}
