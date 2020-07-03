package com.kongmu373.wxshop.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kongmu373.wxshop.generated.Goods;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopCartData extends Goods {
    private Long number;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
