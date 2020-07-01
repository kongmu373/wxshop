package com.kongmu373.wxshop.dao;

import com.kongmu373.wxshop.generated.Goods;
import com.kongmu373.wxshop.generated.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsDao {

    private final GoodsMapper goodsMapper;

    @Autowired
    public GoodsDao(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    public Goods createGoods(Goods goods) {
        goodsMapper.insertSelective(goods);
        return goods;
    }
}
