package com.kongmu373.wxshop.dao;

import com.kongmu373.wxshop.generated.Shop;
import com.kongmu373.wxshop.generated.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopDao {
    private final ShopMapper shopMapper;

    @Autowired
    public ShopDao(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    public Shop selectById(Long shopId) {
        return shopMapper.selectByPrimaryKey(shopId);
    }
}
