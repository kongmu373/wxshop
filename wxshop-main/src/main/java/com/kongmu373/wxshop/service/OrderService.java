package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.entity.AddToShoppingCartItem;
import com.kongmu373.wxshop.entity.OrderResponse;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.result.Result;

import java.util.List;

public interface OrderService {
    Result<OrderResponse> placeOrder(ShopCartRequest shopCartRequest);

    void deductStock(List<AddToShoppingCartItem> shopCartItemList);
}
