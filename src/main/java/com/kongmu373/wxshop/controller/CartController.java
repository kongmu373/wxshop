package com.kongmu373.wxshop.controller;

import com.kongmu373.wxshop.entity.ShopCartData;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import com.kongmu373.wxshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shoppingCart")
public class CartController {

    private final CartService carService;

    @Autowired
    public CartController(CartService carService) {
        this.carService = carService;
    }


    /**
     * 查看购物车
     *
     * @param pageNum  第几页
     * @param pageSize 商店的数目
     * @return 返回 PageResult<ShopCartData>
     */
    @GetMapping("")
    public PageResult<ShopCartData> getShopCart(@RequestParam("pageNum") int pageNum,
                                                @RequestParam("pageSize") int pageSize) {
        return carService.getCart(pageNum, pageSize);
    }


    @PostMapping("")
    public Result<ShopCartData> addShopCart(@RequestBody ShopCartRequest shopCartRequest) {
        return carService.addCart(shopCartRequest);
    }
}
