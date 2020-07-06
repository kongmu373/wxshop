package com.kongmu373.wxshop.controller;

import com.kongmu373.wxshop.entity.UserContext;
import com.kongmu373.wxshop.generate.Goods;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import com.kongmu373.wxshop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/goods")
public class GoodsController {
    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("")
    public Result<Goods> createGoods(@RequestBody Goods goods, HttpServletResponse response) {
        Goods newGoods = goodsService.createGoods(goods, UserContext.getCurrentUser());
        response.setStatus(HttpStatus.CREATED.value());
        return Result.create(null, newGoods);
    }

    @DeleteMapping("/{id}")
    public Result<Goods> deleteGoods(@PathVariable("id") Long id, HttpServletResponse response) {
        Goods deleteGoods = goodsService.deleteGoods(id, UserContext.getCurrentUser());
        response.setStatus(HttpStatus.NO_CONTENT.value());

        return Result.create(null, deleteGoods);
    }

    @PostMapping("/{id}")
    public Result<Goods> updateGoods(@PathVariable("id") Long id, @RequestBody Goods goods) {
        goods.setId(id);
        Goods updateGoods = goodsService.updateGoods(goods, UserContext.getCurrentUser());
        return Result.create(null, updateGoods);
    }


    @GetMapping("")
    public PageResult<Goods> getAllGoods(@RequestParam("pageNum") Integer pageNum,
                                         @RequestParam("pageSize") Integer pageSize,
                                         @RequestParam(name = "shopId", required = false) Long shopId) {

        return goodsService.getAllGoods(pageNum, pageSize, shopId);
    }

    @GetMapping("/{id}")
    public Result<Goods> getGoods(@PathVariable("id") Long id) {
        Goods goods = goodsService.getGoods(id);
        return Result.create(null, goods);
    }
}
