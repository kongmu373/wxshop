package com.kongmu373.wxshop.result;

import com.kongmu373.wxshop.generate.User;


public class LoginResult2 {

    private Boolean login;

    private User user;

    public static LoginResult2 create(Boolean login, User user) {
        return builder()
                       .login(login)
                       .user(user)
                       .build();
    }


    public static Builder builder() {
        return new Builder();
    }

    public Boolean getLogin() {
        return login;
    }

    public void setLogin(Boolean login) {
        this.login = login;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LoginResult2() {

    }

    private LoginResult2(Boolean login, User user) {
        this.login = login;
        this.user = user;
    }

    public static class Builder {
        private Boolean login;
        private User user;

        Builder() {
        }

        public Builder login(Boolean login) {
            this.login = login;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public LoginResult2 build() {
            return new LoginResult2(
                    this.login,
                    this.user);
        }
    }
}
