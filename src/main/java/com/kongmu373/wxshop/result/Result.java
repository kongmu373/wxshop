package com.kongmu373.wxshop.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.io.Serializable;

@AutoValue
@JsonSerialize(as = Result.class)
@JsonDeserialize(builder = AutoValue_Result.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Result<T> implements Serializable {

    @JsonProperty("message")
    @Nullable
    public abstract String message();

    @JsonProperty("data")
    @Nullable
    public abstract T data();

    public static <T> Result<T> create(String message, T data) {
        return Result.<T>builder()
                       .message(message)
                       .data(data)
                       .build();
    }

    public static <T> Builder<T> builder() {
        return new AutoValue_Result.Builder<>();
    }


    @AutoValue.Builder
    public abstract static class Builder<T> {
        @JsonProperty("message")
        public abstract Builder<T> message(String message);

        @JsonProperty("data")
        public abstract Builder<T> data(T data);

        public abstract Result<T> build();
    }
}
