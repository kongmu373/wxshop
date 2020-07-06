package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.dao.GoodsDao;
import com.kongmu373.wxshop.dao.ShopDao;
import com.kongmu373.wxshop.exception.BadRequestException;
import com.kongmu373.wxshop.exception.ForbiddenException;
import com.kongmu373.wxshop.exception.NotFoundException;
import com.kongmu373.wxshop.generate.Goods;
import com.kongmu373.wxshop.generate.Shop;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        if (goods == null) {
            throw new BadRequestException();
        }
        validGoodsIsNotBelongToCurrentUser(goods, currentUser);
        createClean(goods);
        goodsDao.createGoods(goods);
        showClean(goods);
        return goods;
    }


    public Goods deleteGoods(Long id, User currentUser) {
        Goods goods = goodsDao.selectByPrimaryId(id).orElseThrow(NotFoundException::new);
        validGoodsIsNotBelongToCurrentUser(goods, currentUser);
        goodsDao.deleteGoods(id);
        showClean(goods);
        return goods;
    }


    private void createClean(Goods goods) {
        goods.setId(null);
        goods.setCreatedAt(new Date());
        goods.setUpdatedAt(new Date());
        goods.setStatus("ok");
    }

    private void validGoodsIsNotBelongToCurrentUser(Goods goods, User currentUser) {
        Shop shop = shopDao.selectById(goods.getShopId()).orElseThrow(ForbiddenException::new);
        if ( currentUser == null || !Objects.equals(shop.getOwnerUserId(), currentUser.getId())) {
            throw new ForbiddenException();
        }
    }

    public Goods updateGoods(Goods goods, User currentUser) {
        Goods oldGoods = goodsDao.selectByPrimaryId(goods.getId()).orElseThrow(NotFoundException::new);
        validGoodsIsNotBelongToCurrentUser(oldGoods, currentUser);
        updateClean(goods);
        Goods updateGoods = goodsDao.updateGoods(goods).orElse(null);
        showClean(Objects.requireNonNull(updateGoods));
        return updateGoods;
    }

    private Goods showClean(Goods goods) {
        goods.setStatus(null);
        goods.setShopId(null);
        return goods;
    }

    private void updateClean(Goods goods) {
        goods.setStatus(null);
        goods.setShopId(null);
        goods.setCreatedAt(null);
        goods.setUpdatedAt(new Date());
    }

    public PageResult<Goods> getAllGoods(Integer pageNum, Integer pageSize, Long shopId) {
        List<Goods> goodsList = goodsDao.getAllGoods(pageNum, pageSize, shopId).stream()
                                        .map(this::showClean)
                                        .collect(Collectors.toList());
        Integer total = goodsDao.getAllGoodsOfCount(shopId);
        Integer totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        return PageResult.create(pageNum, pageSize, totalPage, goodsList);
    }

    public Goods getGoods(Long id) {
        return goodsDao.selectByPrimaryId(id).orElseThrow(NotFoundException::new);
    }
}
