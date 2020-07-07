package com.kongmu373.wxshop.api.rpc;

import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.generate.User;

import java.util.Map;

public interface OrderRpcService {

    Order placeOrder(ShopCartRequest shopCartRequest, Map<Long, ShopCartItem> idToItem, Long shopId, User user);
}
