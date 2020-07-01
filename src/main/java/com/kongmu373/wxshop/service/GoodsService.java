package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.dao.GoodsDao;
import com.kongmu373.wxshop.dao.ShopDao;
import com.kongmu373.wxshop.entity.ErrorMessage;
import com.kongmu373.wxshop.exception.ErrorException;
import com.kongmu373.wxshop.generated.Goods;
import com.kongmu373.wxshop.generated.Shop;
import com.kongmu373.wxshop.generated.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class GoodsService {

    private final GoodsDao goodsDao;

    private final ShopDao shopDao;

    @Autowired
    public GoodsService(GoodsDao goodsDao, ShopDao shopDao) {
        this.goodsDao = goodsDao;
        this.shopDao = shopDao;
    }

    public Goods createGoods(Goods goods, User currentUser) {
        clean(goods);
        Shop shop = shopDao.selectById(goods.getShopId());
        if (!Objects.equals(shop.getOwnerUserId(), currentUser.getId())) {
            throw new ErrorException(ErrorMessage.FORBIDDEN, HttpStatus.FORBIDDEN.value());
        }

        return goodsDao.createGoods(goods);
    }

    private void clean(Goods goods) {
        goods.setId(null);
        goods.setCreatedAt(new Date());
        goods.setUpdatedAt(new Date());
        goods.setStatus("ok");
    }
}
