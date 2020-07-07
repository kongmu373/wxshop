package com.kongmu373.wxshop.order.service;

import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.api.rpc.OrderRpcService;
import com.kongmu373.wxshop.entity.DataStatus;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.exception.BadRequestException;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.order.generate.OrderMapper;
import com.kongmu373.wxshop.order.mapper.OrderGoodsCustomMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Service(version = "${wxshop.orderservice.version}")
public class OrderRpcServiceImpl implements OrderRpcService {

    private final OrderMapper orderMapper;

    private final OrderGoodsCustomMapper orderGoodsCustomMapper;

    @Autowired
    public OrderRpcServiceImpl(OrderMapper orderMapper, OrderGoodsCustomMapper orderGoodsCustomMapper) {
        this.orderMapper = orderMapper;
        this.orderGoodsCustomMapper = orderGoodsCustomMapper;
    }

    @Override
    public Order placeOrder(ShopCartRequest shopCartRequest, Map<Long, ShopCartItem> idToItem, Long shopId, User user) {
        Order order = insertOrder(idToItem, shopId, user);
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", order.getId());
        map.put("goods", shopCartRequest.goods());
        orderGoodsCustomMapper.insertOrderGoods(map);
        return order;
    }

    private Order insertOrder(Map<Long, ShopCartItem> idToItem, Long shopId, User user) {
        Order order = new Order();
        order.setUserId(user.getId());
        order.setShopId(shopId);
        order.setAddress(user.getAddress());
        order.setStatus(DataStatus.PENDING.getName());
        long sum = idToItem.values().stream()
                           .mapToLong(goods -> goods.getNumber() * goods.getPrice())
                           .sum();
        order.setTotalPrice(sum);
        verify(() -> order.getUserId() == null, "userId不能为空！");
        verify(() -> order.getTotalPrice() == null || order.getTotalPrice().doubleValue() < 0, "totalPrice非法！");
        verify(() -> order.getAddress() == null, "address不能为空！");

        order.setExpressCompany(null);
        order.setExpressId(null);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());

        orderMapper.insert(order);
        return order;
    }

    private void verify(BooleanSupplier supplier, String message) {
        if (supplier.getAsBoolean()) {
            throw new BadRequestException(message);
        }
    }
}
