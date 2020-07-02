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

import java.util.Optional;

import static org.mockito.Mockito.verify;
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
                "这是一块好肥皂", "https://img.url", 500L, 10, null, null, null);
//
        User user = new User();
        user.setId(1L);

        Shop shop = new Shop();
        shop.setId(1L);
        shop.setOwnerUserId(1L);
        when(shopDao.selectById(1L)).thenReturn(Optional.of(shop));
        goodsService.createGoods(goods, user);
        verify(goodsDao).createGoods(goods);
        verify(shopDao).selectById(1L);
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
        when(shopDao.selectById(goods.getShopId())).thenReturn(Optional.of(shop));
        Assertions.assertThrows(ErrorException.class, () -> goodsService.createGoods(goods, user));
        Assertions.assertThrows(ErrorException.class, () -> goodsService.createGoods(null, user));
        Assertions.assertThrows(ErrorException.class, () -> goodsService.createGoods(goods, null));
        goods.setShopId(12345L);
        Assertions.assertThrows(ErrorException.class, () -> goodsService.createGoods(goods, user));
    }


    @Test
    public void deleteGoodsAndNormalReturn() {
        Goods goods = new Goods(1L, 1L, "肥皂", "纯天然无污染肥皂",
                "这是一块好肥皂", "https://img.url", 500L, 10, "ok", null, null);
        User user = new User();
        user.setId(1L);

        Shop shop = new Shop();
        shop.setId(1L);
        shop.setOwnerUserId(1L);

        when(goodsDao.selectByPrimaryId(1L)).thenReturn(Optional.of(goods));
        when(shopDao.selectById(goods.getShopId())).thenReturn(Optional.of(shop));
        Goods deleteGoods = goodsService.deleteGoods(1L, user);
        verify(goodsDao).deleteGoods(1L);
        Assertions.assertNull(deleteGoods.getStatus());
    }

    @Test
    public void updateGoodsNormalReturn() {
        Goods goods = new Goods(1L, null, "肥皂", "纯天然无污染肥皂",
                "这是一块好肥皂", "https://img.url", 500L, 10, "ok", null, null);
        Goods oldGoods = new Goods(1L, 1L, "肥皂", "纯天然无污染肥皂",
                "这是一块好肥皂", "https://img.url", 5010L, 10, null, null, null);

        User user = new User();
        user.setId(1L);
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setOwnerUserId(1L);
        when(goodsDao.selectByPrimaryId(1L)).thenReturn(Optional.of(oldGoods));
        when(shopDao.selectById(oldGoods.getShopId())).thenReturn(Optional.of(shop));
        when(goodsDao.updateGoods(goods)).thenReturn(Optional.of(goods));
        goodsService.updateGoods(goods, user);
        verify(goodsDao).updateGoods(goods);

    }
}
