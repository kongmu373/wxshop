package com.kongmu373.wxshop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import com.kongmu373.wxshop.generated.Shop;

import java.util.List;

@AutoValue
@JsonSerialize(as = ShopCartItem.class)
@JsonDeserialize(builder = AutoValue_ShopCartItem.Builder.class)
public abstract class ShopCartItem {

    @JsonProperty("shop")
    public abstract Shop shop();

    @JsonProperty("goods")
    public abstract List<ShopCartData> goods();

    public static ShopCartItem create(Shop shop, List<ShopCartData> goods) {
        return builder()
                       .shop(shop)
                       .goods(goods)
                       .build();
    }

    public static Builder builder() {
        return new AutoValue_ShopCartItem.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("shop")
        public abstract Builder shop(Shop shop);

        @JsonProperty("goods")
        public abstract Builder goods(List<ShopCartData> goods);

        public abstract ShopCartItem build();
    }
}
