package com.kongmu373.wxshop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import com.kongmu373.wxshop.generated.Shop;

import java.io.Serializable;
import java.util.List;

@AutoValue
@JsonSerialize(as = ShopCartData.class)
@JsonDeserialize(builder = AutoValue_ShopCartData.Builder.class)
public abstract class ShopCartData implements Serializable {

    @JsonProperty("shop")
    public abstract Shop shop();

    @JsonProperty("goods")
    public abstract List<ShopCartItem> goods();

    public static ShopCartData create(Shop shop, List<ShopCartItem> goods) {
        return builder()
                       .shop(shop)
                       .goods(goods)
                       .build();
    }

    public static Builder builder() {
        return new AutoValue_ShopCartData.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("shop")
        public abstract Builder shop(Shop shop);

        @JsonProperty("goods")
        public abstract Builder goods(List<ShopCartItem> goods);

        public abstract ShopCartData build();
    }
}
