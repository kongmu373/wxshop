package com.kongmu373.wxshop.dao;

import com.kongmu373.wxshop.generate.Goods;
import com.kongmu373.wxshop.generate.GoodsExample;
import com.kongmu373.wxshop.generate.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GoodsDao {

    private final GoodsMapper goodsMapper;

    @Autowired
    public GoodsDao(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    public void createGoods(Goods goods) {
        goodsMapper.insertSelective(goods);
    }

    public Optional<Goods> selectByPrimaryId(Long id) {
        GoodsExample example = new GoodsExample();
        GoodsExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id).andStatusEqualTo("ok");
        return goodsMapper.selectByExample(example).stream().findFirst();
    }

    public void deleteGoods(Long id) {
        Goods goods = new Goods();
        goods.setId(id);
        goods.setStatus("deleted");
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    public Optional<Goods> updateGoods(Goods goods) {
        goodsMapper.updateByPrimaryKeySelective(goods);
        return selectByPrimaryId(goods.getId());
    }

    public List<Goods> getAllGoods(Integer pageNum, Integer pageSize, Long shopId) {
        GoodsExample example = new GoodsExample();
        example.setOffset((pageNum - 1) * pageSize);
        example.setLimit(pageSize);
        if (shopId != null) {
            example.createCriteria().andShopIdEqualTo(shopId);
        }
        return goodsMapper.selectByExample(example);
    }

    public Integer getAllGoodsOfCount(Long shopId) {
        GoodsExample example = new GoodsExample();
        if (shopId != null) {
            example.createCriteria().andShopIdEqualTo(shopId);
        }
        return Math.toIntExact(goodsMapper.countByExample(example));
    }
}
