package com.thenullcircus.util;

import com.thenullcircus.model.User;
import lombok.Getter;

public class Session {

    @Getter
    private static User currentUser;

    public static void login(User user) {
        currentUser = user;
    }
    public static void logout() {
        currentUser = null;
    }
    public static boolean isClown() {
        return currentUser != null && currentUser.getClown();
    }
    public static boolean isRingleader(){
        return currentUser != null && currentUser.getRingleader();
    }

    private Session() {}

}
