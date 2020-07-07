package com.kongmu373.wxshop.controller;

import com.kongmu373.wxshop.entity.OrderResponse;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.result.Result;
import com.kongmu373.wxshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("")
    public Result<OrderResponse> placeOrder(@RequestBody ShopCartRequest shopCartRequest) {
        orderService.deductStock(shopCartRequest.goods());
        return orderService.placeOrder(shopCartRequest);
    }
}

