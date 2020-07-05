package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.dao.CartDao;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartData;
import com.kongmu373.wxshop.entity.UserContext;
import com.kongmu373.wxshop.generated.User;
import com.kongmu373.wxshop.result.PageResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private CartDao cartDao;


    @InjectMocks
    private CartService cartService;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(1L);
        UserContext.setCurrentUser(user);
    }

    @Test
    public void getCartTest() {
        int totalShopInCart = 3;
        List<ShopCartItem> listData = Mockito.mock(List.class);
        when(cartDao.getCountShopsInShopCart(1L)).thenReturn(totalShopInCart);
        when(cartDao.getCartDataList(1L, 1, 2)).thenReturn(listData);
        PageResult<ShopCartData> cart = cartService.getCart(1, 2);
        Assertions.assertEquals(1, cart.pageNum());
        Assertions.assertEquals(2, cart.pageSize());
        Assertions.assertEquals(2, cart.totalPage());
    }
}
