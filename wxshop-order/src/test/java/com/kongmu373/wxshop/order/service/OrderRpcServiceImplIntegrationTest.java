package com.kongmu373.wxshop.order.service;

import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.entity.AddToShoppingCartItem;
import com.kongmu373.wxshop.entity.DataStatus;
import com.kongmu373.wxshop.entity.OrderResponse;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.order.generate.OrderGoodsMapper;
import com.kongmu373.wxshop.order.generate.OrderMapper;
import com.kongmu373.wxshop.order.mapper.OrderGoodsCustomMapper;
import com.kongmu373.wxshop.result.PageResult;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.assertj.core.util.Sets;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

class OrderRpcServiceImplIntegrationTest {
    String databaseUrl = "jdbc:mysql://localhost:3308/test-order?useSSL=false&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    String databaseUsername = "root";
    String databasePassword = "my-secret-pw";
    OrderRpcServiceImpl orderRpcService;

    SqlSession sqlSession;

    @BeforeEach
    public void setUpDB() throws IOException {
        // init DB
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();

        // get session
        String resource = "test-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession(true);

        orderRpcService = new OrderRpcServiceImpl(
                sqlSession.getMapper(OrderMapper.class),
                sqlSession.getMapper(OrderGoodsCustomMapper.class),
                sqlSession.getMapper(OrderGoodsMapper.class));
    }

    @AfterEach
    public void cleanUp() {
        sqlSession.close();
    }

    @Test
    void placeOrder() {
        AddToShoppingCartItem cartItem1 = AddToShoppingCartItem.create(1L, 9);
        AddToShoppingCartItem cartItem2 = AddToShoppingCartItem.create(2L, 4);
        ShopCartItem shopCartItem1 = mockShopCartItem(1L, 9);
        ShopCartItem shopCartItem2 = mockShopCartItem(2L, 4);
        Map<Long, ShopCartItem> itemMap = Arrays.asList(shopCartItem1, shopCartItem2).stream()
                                                  .collect(Collectors.toMap(ShopCartItem::getId, x -> x));
        ShopCartRequest shopCartRequest = ShopCartRequest.create(Arrays.asList(cartItem1, cartItem2));
        User user = new User();
        user.setId(2L);
        user.setAddress("火星");
        Order order = orderRpcService.placeOrder(shopCartRequest, itemMap, 1L, user);
        Assertions.assertNotNull(order.getId());
        Assertions.assertEquals(2L, order.getUserId());
        Assertions.assertEquals(1300L, order.getTotalPrice());
        Assertions.assertEquals("火星", order.getAddress());
        Assertions.assertEquals("pending", order.getStatus());
        Assertions.assertEquals(1L, order.getShopId());
    }

    private ShopCartItem mockShopCartItem(long id, long number) {
        ShopCartItem shopCartItem = new ShopCartItem();
        shopCartItem.setId(id);
        shopCartItem.setShopId(1L);
        shopCartItem.setDescription("desc1");
        shopCartItem.setDetails("details1");
        shopCartItem.setImgUrl("url1");
        shopCartItem.setPrice(100L);
        shopCartItem.setStock(5);
        shopCartItem.setStatus("ok");
        shopCartItem.setNumber(number);
        return shopCartItem;
    }

    @Test
    void deleteOrder() {
        OrderResponse orderResponse = orderRpcService.deleteOrder(1L, 1L);
        Assertions.assertEquals(1L, orderResponse.id());
        Assertions.assertEquals("顺丰", orderResponse.expressCompany());
        Assertions.assertEquals("运单1234567", orderResponse.expressId());
        Assertions.assertEquals("deleted", orderResponse.status());
        Assertions.assertEquals("火星", orderResponse.address());
        Assertions.assertEquals(1L, orderResponse.shop().getId());
        Assertions.assertEquals(Sets.newHashSet(Arrays.asList(5L, 9L)), orderResponse.goods().stream()
                                                                                .map(ShopCartItem::getNumber).collect(Collectors.toSet()));
        Assertions.assertEquals(1400L, orderResponse.totalPrice());
    }

    @Test
    void getPageOfOrders() {
        PageResult<OrderResponse> pageOfOrders = orderRpcService.getPageOfOrders(1L, 1, 2, "pending");
        Assertions.assertEquals(1, pageOfOrders.pageNum());
        Assertions.assertEquals(2, pageOfOrders.pageSize());
        Assertions.assertEquals(1, pageOfOrders.totalPage());
        Assertions.assertEquals(2L, pageOfOrders.data().get(0).id());
        Assertions.assertEquals("顺丰", pageOfOrders.data().get(0).expressCompany());
        Assertions.assertEquals("运单1234567", pageOfOrders.data().get(0).expressId());
        Assertions.assertEquals("pending", pageOfOrders.data().get(0).status());
        Assertions.assertEquals("火星", pageOfOrders.data().get(0).address());
        Assertions.assertEquals(700L, pageOfOrders.data().get(0).totalPrice());
        Assertions.assertEquals(Sets.newHashSet(Arrays.asList(3L, 4L)), pageOfOrders.data().get(0).goods().stream()
                                                                                .map(ShopCartItem::getNumber).collect(Collectors.toSet()));
    }

    @Test
    void getOrderByOrderId() {
        Order order = orderRpcService.getOrderByOrderId(1L);
        Assertions.assertEquals(1L, order.getId());
        Assertions.assertEquals(1L, order.getUserId());
        Assertions.assertEquals(1400L, order.getTotalPrice());
        Assertions.assertEquals("火星", order.getAddress());
        Assertions.assertEquals("顺丰", order.getExpressCompany());
        Assertions.assertEquals("运单1234567", order.getExpressId());
        Assertions.assertEquals("delivered", order.getStatus());
        Assertions.assertEquals(1L, order.getShopId());
    }

    @Test
    void updateOrders() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(DataStatus.DELETED.getName());
        OrderResponse orderResponse = orderRpcService.updateOrders(order);
        Assertions.assertEquals(1L, orderResponse.id());
        Assertions.assertEquals("顺丰", orderResponse.expressCompany());
        Assertions.assertEquals("运单1234567", orderResponse.expressId());
        Assertions.assertEquals("deleted", orderResponse.status());
        Assertions.assertEquals("火星", orderResponse.address());
        Assertions.assertEquals(1L, orderResponse.shop().getId());
        Assertions.assertEquals(Sets.newHashSet(Arrays.asList(5L, 9L)), orderResponse.goods().stream()
                                                                                .map(ShopCartItem::getNumber).collect(Collectors.toSet()));
        Assertions.assertEquals(1400L, orderResponse.totalPrice());
    }
}
