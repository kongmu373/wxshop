package com.kongmu373.wxshop.mapper;

import com.kongmu373.wxshop.entity.ShopCartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopCartCustomMapper {
    List<ShopCartItem> getCartDataList(@Param("userId") long userId,
                                       @Param("offset") int offset,
                                       @Param("pageSize") int pageSize);

    List<ShopCartItem> selectShoppingCartDataByUserIdShopId(@Param("userId") Long userId, @Param("shopId") Long shopId);

    int getCountShopsInShopCart(Long id);
}
