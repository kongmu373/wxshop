package com.kongmu373.wxshop.entity;

import com.kongmu373.wxshop.generate.User;

public final class UserContext {

    private static final ThreadLocal<User> users = new ThreadLocal<>();

    public static void setCurrentUser(User user) {
        users.set(user);
    }

    public static User getCurrentUser() {
        return users.get();
    }
}
