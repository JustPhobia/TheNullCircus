package com.thenullcircus.util;

import com.thenullcircus.model.User;
import lombok.Getter;
import java.util.logging.Logger;

public class Session {
    private static final Logger logger = Logger.getLogger(Session.class.getName());
    @Getter private static User currentUser;

    public static void login(User user) {
        currentUser = user;
        logger.info("User session started: " + user.getUsername() + " (ID: " + user.getUserId() + ")");
    }

    public static void logout() {
        if (currentUser != null) {
            logger.info("User session ended: " + currentUser.getUsername());
        }
        currentUser = null;
    }

    public static boolean isClown() {
        return currentUser != null && Boolean.TRUE.equals(currentUser.getClown());
    }

    public static boolean isRingleader() {
        return currentUser != null && Boolean.TRUE.equals(currentUser.getRingleader());
    }

    private Session() {}
}