package com.kongmu373.wxshop.integration;

import com.kongmu373.wxshop.generate.User;

public class CookieAndUser {
    private String cookie;
    private User user;

    public CookieAndUser(String cookie, User user) {
        this.cookie = cookie;
        this.user = user;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
