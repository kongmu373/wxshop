package com.kongmu373.wxshop.api.rpc;

import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.entity.OrderResponse;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.result.PageResult;

import java.util.Map;
import java.util.Optional;

public interface OrderRpcService {

    Order placeOrder(ShopCartRequest shopCartRequest, Map<Long, ShopCartItem> idToItem, Long shopId, User user);

    OrderResponse deleteOrder(long orderId, Long userId);

    PageResult<OrderResponse> getPageOfOrders(Long userId, int pageNum, int pageSize, String status);

    Order getOrderByOrderId(long orderId);

    OrderResponse updateOrders(Order order);
}
