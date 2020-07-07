package com.kongmu373.wxshop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import com.kongmu373.wxshop.generate.Shop;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

@AutoValue
@JsonSerialize(as = OrderResponse.class)
@JsonDeserialize(builder = AutoValue_OrderResponse.Builder.class)
public abstract class OrderResponse implements Serializable {

    @JsonProperty("id")
    @Nullable
    public abstract Long id();

    @JsonProperty("expressCompany")
    @Nullable
    public abstract String expressCompany();

    @JsonProperty("expressId")
    @Nullable
    public abstract String expressId();

    @JsonProperty("status")
    @Nullable
    public abstract String status();


    @JsonProperty("address")
    @Nullable
    public abstract String address();

    @JsonProperty("shop")
    public abstract Shop shop();

    @JsonProperty("goods")
    public abstract List<ShopCartItem> goods();

    @JsonProperty("totalPrice")
    @Nullable
    public abstract Long totalPrice();

    public static OrderResponse create(Long id, String expressCompany, String expressId, String status, String address, Shop shop, List<ShopCartItem> goods, Long totalPrice) {
        return builder()
                       .id(id)
                       .expressCompany(expressCompany)
                       .expressId(expressId)
                       .status(status)
                       .address(address)
                       .shop(shop)
                       .goods(goods)
                       .totalPrice(totalPrice)
                       .build();
    }

    public static Builder builder() {
        return new AutoValue_OrderResponse.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("id")
        public abstract Builder id(Long id);

        @JsonProperty("expressCompany")
        public abstract Builder expressCompany(String expressCompany);

        @JsonProperty("expressId")
        public abstract Builder expressId(String expressId);

        @JsonProperty("status")
        public abstract Builder status(String status);

        @JsonProperty("address")
        public abstract Builder address(String address);

        @JsonProperty("shop")
        public abstract Builder shop(Shop shop);

        @JsonProperty("goods")
        public abstract Builder goods(List<ShopCartItem> goods);

        @JsonProperty("totalPrice")
        public abstract Builder totalPrice(Long totalPrice);

        public abstract OrderResponse build();
    }
}
