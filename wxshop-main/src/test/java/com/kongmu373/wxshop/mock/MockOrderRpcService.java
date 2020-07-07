package com.kongmu373.wxshop.mock;

import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.api.rpc.OrderRpcService;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.generate.User;
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
}
