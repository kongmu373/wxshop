package com.kongmu373.wxshop.order.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface OrderGoodsCustomMapper {
    void insertOrderGoods(Map<String, Object> map);
}
