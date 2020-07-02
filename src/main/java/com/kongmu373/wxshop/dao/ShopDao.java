package com.kongmu373.wxshop.dao;

import com.kongmu373.wxshop.generated.Shop;
import com.kongmu373.wxshop.generated.ShopExample;
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
        ShopExample example = new ShopExample();
        example.createCriteria()
                .andIdEqualTo(shopId)
                .andStatusEqualTo("ok");
        return shopMapper.selectByExample(example).stream().findFirst().orElse(null);
    }
}
