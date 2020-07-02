package com.kongmu373.wxshop.controller;

import com.kongmu373.wxshop.generated.Shop;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import com.kongmu373.wxshop.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/shop")
public class ShopController {
    private final ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }


    @GetMapping("")
    public PageResult<Shop> getShopList(@RequestParam("pageNum") Integer pageNum,
                                        @RequestParam("pageSize") Integer pageSize) {
        return shopService.getShopList(pageNum, pageSize);
    }

    @PostMapping("")
    public Result<Shop> createShop(@RequestBody Shop shop, HttpServletResponse response) {
        response.setStatus(HttpStatus.CREATED.value());
        return shopService.createShop(shop);
    }

    @PostMapping("/{id}")
    public Result<Shop> updateShop(@PathVariable("id") Long id, @RequestBody Shop shop) {
        return shopService.updateShop(id, shop);
    }

    @GetMapping("/{id}")
    public Result<Shop> getShop(@PathVariable("id") Long id) {
        return shopService.getShop(id);
    }
}
