package com.kongmu373.wxshop.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kongmu373.wxshop.generate.Goods;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopCartItem extends Goods implements Serializable {
    private Long number;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public ShopCartItem goods2ShopCartItem(Goods goods) {
        this.setId(goods.getId());
        this.setShopId(goods.getShopId());
        this.setCreatedAt(goods.getCreatedAt());
        this.setDescription(goods.getDescription());
        this.setDetails(goods.getDetails());
        this.setImgUrl(goods.getImgUrl());
        this.setName(goods.getName());
        this.setPrice(goods.getPrice());
        this.setUpdatedAt(goods.getUpdatedAt());
        return this;
    }
}
