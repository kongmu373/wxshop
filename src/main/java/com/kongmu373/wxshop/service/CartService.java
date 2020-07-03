package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.dao.CartDao;
import com.kongmu373.wxshop.dao.ShopDao;
import com.kongmu373.wxshop.entity.ShopCartData;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.UserContext;
import com.kongmu373.wxshop.generated.Shop;
import com.kongmu373.wxshop.generated.User;
import com.kongmu373.wxshop.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class CartService {
    private final CartDao cartDao;
    private final ShopDao shopDao;

    @Autowired
    public CartService(CartDao cartDao, ShopDao shopDao) {
        this.cartDao = cartDao;
        this.shopDao = shopDao;
    }

    public PageResult<ShopCartItem> getCart(int pageNum, int pageSize) {
        // 1. 返回总共有多少商店
        User currentUser = UserContext.getCurrentUser();
        int count = cartDao.getCountShopsInShopCart(currentUser.getId());
        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        // 2. 返回data数据
        List<ShopCartItem> collect = cartDao.getCartDataList(currentUser.getId(), pageNum, pageSize)
                                             .stream()
                                             .collect(groupingBy(ShopCartData::getShopId))
                                             .entrySet()
                                             .stream()
                                             .map(this::getShopCartItem)
                                             .collect(Collectors.toList());
        return PageResult.create(pageNum, pageSize, totalPage, collect);
    }

    private ShopCartItem getShopCartItem(Map.Entry<Long, List<ShopCartData>> entry) {
        Shop shop = shopDao.selectById(entry.getKey()).orElse(null);
        return ShopCartItem.create(shop, entry.getValue());
    }
}
