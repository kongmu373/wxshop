package com.kongmu373.wxshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.api.rpc.OrderRpcService;
import com.kongmu373.wxshop.dao.ShopDao;
import com.kongmu373.wxshop.entity.AddToShoppingCartItem;
import com.kongmu373.wxshop.entity.OrderResponse;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.entity.UserContext;
import com.kongmu373.wxshop.exception.BadRequestException;
import com.kongmu373.wxshop.exception.ErrorException;
import com.kongmu373.wxshop.generate.Goods;
import com.kongmu373.wxshop.mapper.GoodsCustomMapper;
import com.kongmu373.wxshop.result.Result;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class OrderServiceImpl implements OrderService {

    private final GoodsService goodsService;
    private final ShopDao shopDao;
    private final GoodsCustomMapper goodsCustomMapper;
    @Reference(version = "${wxshop.orderservice.version}")
    private OrderRpcService orderRpcService;

    @Autowired
    public OrderServiceImpl(GoodsService goodsService, ShopDao shopDao, GoodsCustomMapper goodsCustomMapper) {
        this.goodsService = goodsService;
        this.shopDao = shopDao;
        this.goodsCustomMapper = goodsCustomMapper;
    }

    @Override
    public Result<OrderResponse> placeOrder(ShopCartRequest shopCartRequest) {
        Map<Long, ShopCartItem> idToItem = shopCartRequest.goods().stream()
                                                   .map(this::createCartItem)
                                                   .collect(toMap(ShopCartItem::getId, x -> x));
        Long shopId = new ArrayList<>(idToItem.values()).get(0).getShopId();
        Order createdOrder = orderRpcService.placeOrder(shopCartRequest, idToItem, shopId, UserContext.getCurrentUser());
        OrderResponse orderResponse = OrderResponse.create(createdOrder.getId(),
                createdOrder.getExpressCompany(),
                createdOrder.getExpressId(),
                createdOrder.getStatus(),
                createdOrder.getAddress(),
                shopDao.selectById(shopId).orElseThrow(BadRequestException::new),
                new ArrayList<>(idToItem.values()),
                createdOrder.getTotalPrice());
        return Result.create(null, orderResponse);

    }

    @Transactional
    @Override
    public void deductStock(List<AddToShoppingCartItem> shopCartItemList) {
        for (AddToShoppingCartItem shopCartItem : shopCartItemList) {
            if (goodsCustomMapper.deductStock(shopCartItem) <= 0) {
                throw new ErrorException("库存不足", 410);
            }
        }
    }


    private ShopCartItem createCartItem(AddToShoppingCartItem goods) {
        try {
            Goods query = goodsService.getGoods(goods.id());
            ObjectMapper mapper = new ObjectMapper();
            String valueAsString = mapper.writeValueAsString(query);
            ShopCartItem item = mapper.readValue(valueAsString, ShopCartItem.class);
            item.setNumber(goods.number());
            return item;
        } catch (JsonProcessingException e) {
            throw new ErrorException("序列化错误", 500);
        }
    }
}
