package com.kongmu373.wxshop.controller;

import com.kongmu373.wxshop.entity.UserContext;
import com.kongmu373.wxshop.exception.ErrorException;
import com.kongmu373.wxshop.generated.Goods;
import com.kongmu373.wxshop.generated.User;
import com.kongmu373.wxshop.result.Result;
import com.kongmu373.wxshop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class GoodsController {
    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("/goods")
    public Result<Goods> createGoods(@RequestBody Goods goods, HttpServletResponse response) {

        try {
            User currentUser = UserContext.getCurrentUser();
            Goods newGoods = goodsService.createGoods(goods, currentUser);
            return Result.create(null, newGoods);
        } catch (ErrorException e) {
            response.setStatus(e.getCode());
            return Result.create(e.getMessage(), null);
        }
    }

}
