package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.dao.GoodsDao;
import com.kongmu373.wxshop.dao.ShopDao;
import com.kongmu373.wxshop.exception.ErrorException;
import com.kongmu373.wxshop.generated.Goods;
import com.kongmu373.wxshop.generated.Shop;
import com.kongmu373.wxshop.generated.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoodsServiceTest {
    @Mock
    private GoodsDao goodsDao;

    @Mock
    private ShopDao shopDao;

    @InjectMocks
    private GoodsService goodsService;

    @Test
    public void createGoodsAndNormalReturn() {
        Goods goods = new Goods(null, 1L, "肥皂", "纯天然无污染肥皂",
                "https://img.url", 500L, 10, null, null, null, "这是一块好肥皂");


        Goods returnGoods = new Goods(1L, 1L, "肥皂", "纯天然无污染肥皂",
                "https://img.url", 500L, 10, "ok", new Date(), new Date(), "这是一块好肥皂");

        User user = new User();
        user.setId(1L);

        Shop shop = new Shop();
        shop.setOwnerUserId(1L);
        when(goodsDao.createGoods(goods)).thenReturn(returnGoods);
        when(shopDao.selectById(goods.getShopId())).thenReturn(shop);
        Goods newGoods = goodsService.createGoods(goods, user);
        Assertions.assertEquals(1L, newGoods.getId());
    }

    @Test
    public void createGoodsThrowErrorException() {
        Goods goods = new Goods();
        goods.setShopId(2L);
        User user = new User();
        user.setId(1L);
        Shop shop = new Shop();
        shop.setId(2L);
        shop.setOwnerUserId(2L);
        when(shopDao.selectById(goods.getShopId())).thenReturn(shop);
        Assertions.assertThrows(ErrorException.class, () -> goodsService.createGoods(goods, user));
        Assertions.assertThrows(ErrorException.class, () -> goodsService.createGoods(goods, null));

    }

}
