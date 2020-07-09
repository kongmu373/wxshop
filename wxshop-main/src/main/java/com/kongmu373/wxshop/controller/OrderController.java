package com.kongmu373.wxshop.controller;

import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.entity.OrderResponse;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import com.kongmu373.wxshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @DeleteMapping("/{id}")
    public Result<OrderResponse> deleteOrder(@PathVariable("id") long id) {
        return orderService.deleteOrder(id);
    }

    @GetMapping("")
    public PageResult<OrderResponse> getPageOfOrders(@RequestParam("pageNum") int pageNum,
                                                     @RequestParam("pageSize") int pageSize,
                                                     @RequestParam(value = "status", required = false, defaultValue = "pending") String status) {
        return orderService.getPageOfOrders(pageNum, pageSize, status);
    }

    @PostMapping("/{id}")
    public Result<OrderResponse> updateOrders(@PathVariable("id") long orderId, @RequestBody Order order) {
        return orderService.updateOrder(orderId, order);
    }
}

