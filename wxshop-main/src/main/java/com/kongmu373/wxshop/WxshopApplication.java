package com.kongmu373.wxshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class WxshopApplication {
    public static void main(String[] args) {
        SpringApplication.run(WxshopApplication.class, args);
    }
}
