package com.kongmu373.wxshop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;

import java.io.Serializable;
import java.util.List;

@AutoValue
@JsonSerialize(as = ShopCartRequest.class)
@JsonDeserialize(builder = AutoValue_ShopCartRequest.Builder.class)
public abstract class ShopCartRequest implements Serializable {

    @JsonProperty("goods")
    public abstract List<AddToShoppingCartItem> goods();

    public static ShopCartRequest create(List<AddToShoppingCartItem> goods) {
        return builder()
                       .goods(goods)
                       .build();
    }

    public static Builder builder() {
        return new AutoValue_ShopCartRequest.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("goods")
        public abstract Builder goods(List<AddToShoppingCartItem> goods);

        public abstract ShopCartRequest build();
    }
}
