package com.kongmu373.wxshop.order.service;

import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.api.generate.OrderExample;
import com.kongmu373.wxshop.api.generate.OrderGoods;
import com.kongmu373.wxshop.api.generate.OrderGoodsExample;
import com.kongmu373.wxshop.api.rpc.OrderRpcService;
import com.kongmu373.wxshop.entity.DataStatus;
import com.kongmu373.wxshop.entity.OrderResponse;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.exception.BadRequestException;
import com.kongmu373.wxshop.exception.ForbiddenException;
import com.kongmu373.wxshop.exception.NotFoundException;
import com.kongmu373.wxshop.generate.Shop;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.order.generate.OrderGoodsMapper;
import com.kongmu373.wxshop.order.generate.OrderMapper;
import com.kongmu373.wxshop.order.mapper.OrderGoodsCustomMapper;
import com.kongmu373.wxshop.result.PageResult;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service(version = "${wxshop.orderservice.version}")
public class OrderRpcServiceImpl implements OrderRpcService {

    private final OrderMapper orderMapper;

    private final OrderGoodsCustomMapper orderGoodsCustomMapper;

    private final OrderGoodsMapper orderGoodsMapper;

    @Autowired
    public OrderRpcServiceImpl(OrderMapper orderMapper, OrderGoodsCustomMapper orderGoodsCustomMapper, OrderGoodsMapper orderGoodsMapper) {
        this.orderMapper = orderMapper;
        this.orderGoodsCustomMapper = orderGoodsCustomMapper;
        this.orderGoodsMapper = orderGoodsMapper;
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

    @Override
    public OrderResponse deleteOrder(long orderId, Long userId) {
        Order order = getOrderByOrderId(orderId);
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new ForbiddenException();
        }
        List<OrderGoods> orderGoods = getOrderGoodsListByOrderId(orderId);
        order.setStatus(DataStatus.DELETED.getName());
        order.setUpdatedAt(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
        return getOrderResponse(order, orderGoods);
    }

    private OrderResponse getOrderResponse(Order order, List<OrderGoods> orderGoods) {
        List<ShopCartItem> shopCartItems = orderGoods.stream()
                                                   .map(this::orderGoods2ShopCartItem)
                                                   .collect(Collectors.toList());
        Shop shop = new Shop();
        shop.setId(order.getShopId());
        return OrderResponse.create(order.getId(),
                order.getExpressCompany(),
                order.getExpressId(),
                order.getStatus(),
                order.getAddress(),
                shop,
                shopCartItems,
                order.getTotalPrice());
    }

    @Override
    public PageResult<OrderResponse> getPageOfOrders(Long userId, int pageNum, int pageSize, String status) {
        long totalOrderResponse = countAllByUserIdAndStatus(userId, DataStatus.fromStatus(status));
        List<Order> orders = getPageOfOrder(userId, pageNum, pageSize, status);
        for (Order order : orders) {
            List<OrderGoods> list = getOrderGoodsListByOrderId(order.getId());
            System.out.println(list);
        }
        Map<Long, List<OrderGoods>> orderGoodsMap = orders.stream()
                                                            .map(item -> getOrderGoodsListByOrderId(item.getId()))
                                                            .flatMap(Collection::stream)
                                                            .collect(groupingBy(OrderGoods::getOrderId));
        Map<Long, Order> orderMap = orders.stream().collect(toMap(Order::getId, x -> x));
        List<OrderResponse> responses = orderGoodsMap.entrySet()
                                                .stream()
                                                .map(entry -> {
                                                    Long key = entry.getKey();
                                                    List<OrderGoods> orderGoods = entry.getValue();
                                                    Order order = orderMap.get(key);
                                                    return getOrderResponse(order, orderGoods);
                                                })
                                                .collect(Collectors.toList());

        PageResult<OrderResponse> result = PageResult.create(pageNum,
                pageSize,
                Math.toIntExact(totalOrderResponse % pageSize == 0 ? totalOrderResponse / pageSize : totalOrderResponse / pageSize + 1),
                responses);
        return result;
    }


    private List<Order> getPageOfOrder(Long userId, int pageNum, int pageSize, String status) {
        OrderExample example = new OrderExample();
        example.setOffset((pageNum - 1) * pageSize);
        example.setLimit(pageSize);
        example.createCriteria()
                .andStatusEqualTo(status)
                .andUserIdEqualTo(userId);
        return orderMapper.selectByExample(example);
    }

    private long countAllByUserIdAndStatus(Long userId, DataStatus status) {
        OrderExample example = new OrderExample();
        example.createCriteria()
                .andUserIdEqualTo(userId)
                .andStatusEqualTo(status.getName());
        return orderMapper.countByExample(example);

    }


    private ShopCartItem orderGoods2ShopCartItem(OrderGoods orderGoods) {
        ShopCartItem shopCartItem = new ShopCartItem();
        shopCartItem.setId(orderGoods.getGoodsId());
        shopCartItem.setNumber(orderGoods.getNumber());
        return shopCartItem;
    }

    private List<OrderGoods> getOrderGoodsListByOrderId(long orderId) {
        OrderGoodsExample example = new OrderGoodsExample();
        example.createCriteria()
                .andOrderIdEqualTo(orderId);
        return orderGoodsMapper.selectByExample(example);
    }

    @Override
    public Order getOrderByOrderId(long orderId) {
        OrderExample example = new OrderExample();
        example.createCriteria()
                .andIdEqualTo(orderId);
        return orderMapper.selectByExample(example).stream().findFirst().orElseThrow(NotFoundException::new);
    }

    @Override
    public OrderResponse updateOrders(Order order) {
        order.setUpdatedAt(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
        Order newOrder = getOrderByOrderId(order.getId());
        List<OrderGoods> orderGoods = getOrderGoodsListByOrderId(order.getId());
        return getOrderResponse(newOrder, orderGoods);
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
