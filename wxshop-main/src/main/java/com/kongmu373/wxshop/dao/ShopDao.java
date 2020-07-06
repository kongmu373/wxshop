package com.kongmu373.wxshop.dao;

import com.kongmu373.wxshop.generate.Shop;
import com.kongmu373.wxshop.generate.ShopExample;
import com.kongmu373.wxshop.generate.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShopDao {
    private final ShopMapper shopMapper;

    @Autowired
    public ShopDao(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    public Optional<Shop> selectById(Long shopId) {
        ShopExample example = new ShopExample();
        example.createCriteria()
                .andIdEqualTo(shopId)
                .andStatusEqualTo("ok");
        return shopMapper.selectByExample(example).stream().findFirst();
    }

    public long countAllWithUser(Long id) {
        ShopExample example = new ShopExample();
        example.createCriteria().andOwnerUserIdEqualTo(id).andStatusEqualTo("ok");
        return shopMapper.countByExample(example);
    }

    public List<Shop> selectAllWithUser(Integer pageNum, Integer pageSize, Long id) {
        ShopExample example = new ShopExample();
        example.setOffset((pageNum - 1) * pageSize);
        example.setLimit(pageSize);
        example.createCriteria().andOwnerUserIdEqualTo(id).andStatusEqualTo("ok");
        return shopMapper.selectByExample(example);
    }

    public void createShop(Shop shop) {
        shopMapper.insertSelective(shop);
    }

    public void updateShop(Shop shop) {
        shop.setUpdatedAt(new Date());
        ShopExample example = new ShopExample();
        example.createCriteria().andIdEqualTo(shop.getId()).andStatusEqualTo("ok");
        shopMapper.updateByExampleSelective(shop, example);
    }
}
