package com.kongmu373.wxshop.dao;

import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.generate.ShoppingCart;
import com.kongmu373.wxshop.generate.ShoppingCartExample;
import com.kongmu373.wxshop.generate.ShoppingCartMapper;
import com.kongmu373.wxshop.mapper.ShopCartCustomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CartDao {
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShopCartCustomMapper shopCartCustomMapper;
    @Autowired
    public CartDao(ShoppingCartMapper shoppingCartMapper, ShopCartCustomMapper shopCartCustomMapper) {
        this.shoppingCartMapper = shoppingCartMapper;
        this.shopCartCustomMapper = shopCartCustomMapper;
    }

    public int getCountShopsInShopCart(Long id) {
        return shopCartCustomMapper.getCountShopsInShopCart(id);
    }

    public List<ShopCartItem> getCartDataList(long userId, int pageNum, int pageSize) {
        return shopCartCustomMapper.getCartDataList(userId, (pageNum - 1) * pageSize, pageSize);
    }

    public List<ShopCartItem> selectShoppingCartDataByUserIdShopId(Long userId, Long shopId) {
        return shopCartCustomMapper.selectShoppingCartDataByUserIdShopId(userId, shopId);
    }

    public int deleteByGoodIdAndUserId(long goodId, Long id) {
        ShoppingCartExample example = new ShoppingCartExample();

        example.createCriteria().andGoodsIdEqualTo(goodId)
                .andUserIdEqualTo(id);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setStatus("deleted");
        shoppingCart.setUpdatedAt(new Date());
        return shoppingCartMapper.updateByExampleSelective(shoppingCart, example);
    }

}
