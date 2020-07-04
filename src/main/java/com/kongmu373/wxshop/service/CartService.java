package com.kongmu373.wxshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kongmu373.wxshop.dao.CartDao;
import com.kongmu373.wxshop.dao.ShopDao;
import com.kongmu373.wxshop.entity.AddToShoppingCartItem;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartData;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.entity.UserContext;
import com.kongmu373.wxshop.exception.BadRequestException;
import com.kongmu373.wxshop.exception.NotFoundException;
import com.kongmu373.wxshop.generated.Goods;
import com.kongmu373.wxshop.generated.Shop;
import com.kongmu373.wxshop.generated.ShoppingCart;
import com.kongmu373.wxshop.generated.ShoppingCartExample;
import com.kongmu373.wxshop.generated.ShoppingCartMapper;
import com.kongmu373.wxshop.generated.User;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Service
public class CartService {
    private final CartDao cartDao;
    private final ShopDao shopDao;
    private final GoodsService goodsService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SqlSessionFactory sqlSessionFactory;

    @Autowired
    public CartService(CartDao cartDao, ShopDao shopDao, GoodsService goodsService, SqlSessionFactory sqlSessionFactory) {
        this.cartDao = cartDao;
        this.shopDao = shopDao;
        this.goodsService = goodsService;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public PageResult<ShopCartData> getCart(int pageNum, int pageSize) {
        // 1. 返回总共有多少商店
        User currentUser = UserContext.getCurrentUser();
        int count = cartDao.getCountShopsInShopCart(currentUser.getId());
        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        // 2. 返回data数据
        List<ShopCartData> collect = cartDao.getCartDataList(currentUser.getId(), pageNum, pageSize)
                                             .stream()
                                             .collect(groupingBy(ShopCartItem::getShopId))
                                             .entrySet()
                                             .stream()
                                             .map(this::getShopCartItem)
                                             .collect(Collectors.toList());
        return PageResult.create(pageNum, pageSize, totalPage, collect);
    }

    private ShopCartData getShopCartItem(Map.Entry<Long, List<ShopCartItem>> entry) {
        Shop shop = shopDao.selectById(entry.getKey()).orElse(null);
        return ShopCartData.create(shop, entry.getValue());
    }

    public Result<ShopCartData> addCart(ShopCartRequest shopCartRequest) {
        // 1. 获取需要添加的ShopCartData
        List<ShoppingCart> shoppingCartRows = shopCartRequest.goods()
                                                      .stream()
                                                      .map(this::getShopCartData)
                                                      .collect(Collectors.toList());
        if (shoppingCartRows.stream().map(ShoppingCart::getShopId).collect(toSet()).size() != 1) {
            throw new BadRequestException();
        }
        if (shoppingCartRows.size() != shopCartRequest.goods().size()) {
            throw new NotFoundException();
        }
        // 2. 添加到购物车
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            shoppingCartRows.forEach(row -> insertGoodsToShoppingCart(UserContext.getCurrentUser().getId(), row, mapper));
            sqlSession.commit();
        }
        ShopCartData collect = cartDao.selectShoppingCartDataByUserIdShopId(UserContext.getCurrentUser().getId(), shoppingCartRows.get(0).getShopId())
                                             .stream()
                                             .collect(groupingBy(ShopCartItem::getShopId))
                                             .entrySet()
                                             .stream()
                                             .map(this::getShopCartItem)
                                             .findFirst().orElseThrow(NotFoundException::new);
        // 3. 显示已添加的购物车
        return Result.create(null, collect);

    }

    private void insertGoodsToShoppingCart(long userId, ShoppingCart shoppingCartRow, ShoppingCartMapper shoppingCartMapper) {
        ShoppingCartExample example = new ShoppingCartExample();
        example.createCriteria().andGoodsIdEqualTo(shoppingCartRow.getGoodsId()).andUserIdEqualTo(userId);
        shoppingCartMapper.deleteByExample(example);
        shoppingCartMapper.insert(shoppingCartRow);
    }

    private ShoppingCart getShopCartData(AddToShoppingCartItem goods) {
        Goods good = goodsService.getGoods(goods.id());
        ShoppingCart shopCart = new ShoppingCart();
        shopCart.setNumber(Math.toIntExact(goods.number()));
        shopCart.setUserId(UserContext.getCurrentUser().getId());
        shopCart.setGoodsId(good.getId());
        shopCart.setShopId(good.getShopId());
        shopCart.setStatus("ok");
        shopCart.setCreatedAt(new Date());
        shopCart.setUpdatedAt(new Date());
        return shopCart;
    }
}
