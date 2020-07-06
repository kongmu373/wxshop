package com.kongmu373.wxshop.order.service;

import com.kongmu373.wxshop.api.rpc.OrderService;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "${wxshop.orderservice.version}")
public class OrderServiceImpl implements OrderService {
    @Override
    public void hello(String name) {
        System.out.println("hello:"+ name);
    }
}
