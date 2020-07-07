package com.kongmu373.wxshop.mapper;

import com.kongmu373.wxshop.entity.AddToShoppingCartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsCustomMapper {
    int deductStock(AddToShoppingCartItem item);
}
