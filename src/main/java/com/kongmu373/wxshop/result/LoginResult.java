package com.kongmu373.wxshop.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import com.kongmu373.wxshop.generated.User;

import javax.annotation.Nullable;

@AutoValue
@JsonSerialize(as = LoginResult.class)
@JsonDeserialize(builder = AutoValue_LoginResult.Builder.class)
public abstract class LoginResult {

    @JsonProperty("login")
    @Nullable
    public abstract Boolean login();

    @JsonProperty("user")
    @Nullable
    public abstract User user();

    public static LoginResult create(Boolean login, User user) {
        return builder()
                       .login(login)
                       .user(user)
                       .build();
    }


    public static Builder builder() {
        return new AutoValue_LoginResult.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("login")
        public abstract Builder login(Boolean login);

        @JsonProperty("user")
        public abstract Builder user(User user);

        public abstract LoginResult build();
    }
}
