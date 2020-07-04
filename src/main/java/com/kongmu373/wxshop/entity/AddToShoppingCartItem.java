package com.kongmu373.wxshop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonSerialize(as = AddToShoppingCartItem.class)
@JsonDeserialize(builder = AutoValue_AddToShoppingCartItem.Builder.class)
public abstract class AddToShoppingCartItem {
    @JsonProperty("id")
    public abstract long id();

    @JsonProperty("number")
    public abstract long number();

    public static AddToShoppingCartItem create(long id, long number) {
        return builder()
                       .id(id)
                       .number(number)
                       .build();
    }

    public static Builder builder() {
        return new AutoValue_AddToShoppingCartItem.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("id")
        public abstract Builder id(long id);

        @JsonProperty("number")
        public abstract Builder number(long number);

        public abstract AddToShoppingCartItem build();
    }
}
