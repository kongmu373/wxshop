package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.dao.ShopDao;
import com.kongmu373.wxshop.entity.UserContext;
import com.kongmu373.wxshop.exception.ForbiddenException;
import com.kongmu373.wxshop.exception.NotFoundException;
import com.kongmu373.wxshop.generate.Shop;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ShopService {
    private final ShopDao shopDao;

    @Autowired
    public ShopService(ShopDao shopDao) {
        this.shopDao = shopDao;
    }

    public PageResult<Shop> getShopList(Integer pageNum, Integer pageSize) {
        User user = UserContext.getCurrentUser();
        long count = shopDao.countAllWithUser(user.getId());
        List<Shop> shopList = shopDao.selectAllWithUser(pageNum, pageSize, user.getId());
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        return PageResult.create(pageNum, pageSize, Math.toIntExact(totalPage), shopList);
    }

    public Result<Shop> createShop(Shop shop) {
        shop.setOwnerUserId(UserContext.getCurrentUser().getId());
        shop.setCreatedAt(new Date());
        shop.setUpdatedAt(new Date());
        shop.setStatus("ok");
        shopDao.createShop(shop);
        shop.setStatus(null);
        return Result.create(null, shop);
    }

    public Result<Shop> updateShop(Long id, Shop shop) {
        shop.setId(id);

        Shop shopInDB = shopDao.selectById(id).orElseThrow(NotFoundException::new);
        if (!Objects.equals(shopInDB.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            throw new ForbiddenException();
        }

        shopDao.updateShop(shop);
        return Result.create(null, shopInDB);
    }

    public Result<Shop> getShop(Long id) {
        Shop shop = shopDao.selectById(id).orElseThrow(NotFoundException::new);
        return Result.create(null, shop);
    }
}
