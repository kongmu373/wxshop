package com.kongmu373.wxshop.mock;

import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.api.rpc.OrderRpcService;
import com.kongmu373.wxshop.entity.OrderResponse;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.result.PageResult;
import org.apache.dubbo.config.annotation.Service;
import org.mockito.Mock;

import java.util.Map;

@Service(version = "${wxshop.orderservice.version}")
public class MockOrderRpcService implements OrderRpcService {

    @Mock
    public OrderRpcService orderRpcService;

    @Override
    public Order placeOrder(ShopCartRequest shopCartRequest, Map<Long, ShopCartItem> idToItem, Long shopId, User user) {
        return orderRpcService.placeOrder(shopCartRequest, idToItem, shopId, user);
    }

    @Override
    public OrderResponse deleteOrder(long orderId, Long userId) {
        return orderRpcService.deleteOrder(orderId, userId);
    }

    @Override
    public PageResult<OrderResponse> getPageOfOrders(Long userId, int pageNum, int pageSize, String status) {
        return orderRpcService.getPageOfOrders(userId, pageNum, pageSize, status);
    }

    @Override
    public Order getOrderByOrderId(long orderId) {
        return orderRpcService.getOrderByOrderId(orderId);
    }

    @Override
    public OrderResponse updateOrders(Order order) {
        return orderRpcService.updateOrders(order);
    }
}
