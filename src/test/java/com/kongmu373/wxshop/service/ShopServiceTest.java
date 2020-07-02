package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.dao.ShopDao;
import com.kongmu373.wxshop.entity.UserContext;
import com.kongmu373.wxshop.exception.ForbiddenException;
import com.kongmu373.wxshop.exception.NotFoundException;
import com.kongmu373.wxshop.generated.Shop;
import com.kongmu373.wxshop.generated.User;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShopServiceTest {
    @Mock
    private ShopDao shopDao;

    @InjectMocks
    private ShopService shopService;

    @Mock
    private Shop shop;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(1L);
        UserContext.setCurrentUser(user);
    }

    @Test
    public void getShopListSucceed() {
        int pageNum = 5;
        int pageSize = 10;
        List<Shop> shopList = mock(List.class);

        when(shopDao.countAllWithUser(any())).thenReturn(55L);
        when(shopDao.selectAllWithUser(any(), any(), any())).thenReturn(shopList);
        PageResult<Shop> result = shopService.getShopList(pageNum, pageSize);
        assertEquals(6, result.totalPage());
        assertEquals(5, result.pageNum());
        assertEquals(10, result.pageSize());
        assertEquals(shopList, result.data());


        when(shopDao.countAllWithUser(any())).thenReturn(50L);
        PageResult<Shop> result2 = shopService.getShopList(pageNum, pageSize);
        assertEquals(5, result2.totalPage());
        assertEquals(5, result2.pageNum());
        assertEquals(10, result2.pageSize());
        assertEquals(shopList, result2.data());
    }

    @Test
    public void createShopTest() {
        shopService.createShop(shop);
        verify(shopDao).createShop(shop);
    }

    @Test
    public void updateShopTest() {
        Long shopId = 1L;
        Long userId = 1L;
        when(shop.getOwnerUserId()).thenReturn(userId);
        when(shopDao.selectById(shopId)).thenReturn(Optional.of(shop));
        Result<Shop> result = shopService.updateShop(shopId, shop);
        Assertions.assertNull(result.message());
        Assertions.assertEquals(userId, result.data().getOwnerUserId());
        verify(shopDao).updateShop(shop);
    }

    @Test
    public void updateShopForbiddenTest() {
        Long shopId = 1L;
        Long userId = 2L;
        when(shopDao.selectById(shopId)).thenReturn(Optional.of(shop));
        when(shop.getOwnerUserId()).thenReturn(userId);
        Assertions.assertThrows(ForbiddenException.class, () -> shopService.updateShop(shopId, shop));
    }

    @Test
    public void updateShopNotFoundTest() {
        Long shopId = 111L;
        when(shopDao.selectById(shopId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> shopService.updateShop(shopId, shop));
    }

    @Test
    public void selectByShopIdTest() {
        Long shopId = 1L;
        when(shopDao.selectById(shopId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> shopService.getShop(shopId));

        when(shopDao.selectById(shopId)).thenReturn(Optional.of(shop));
        Result<Shop> result = shopService.getShop(shopId);
        Assertions.assertNull(result.message());
        Assertions.assertEquals(shop, result.data());
    }
}
