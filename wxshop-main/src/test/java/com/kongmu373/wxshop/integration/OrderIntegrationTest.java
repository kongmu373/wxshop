package com.kongmu373.wxshop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kongmu373.wxshop.WxshopApplication;
import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.entity.AddToShoppingCartItem;
import com.kongmu373.wxshop.entity.DataStatus;
import com.kongmu373.wxshop.entity.OrderResponse;
import com.kongmu373.wxshop.entity.ShopCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.generate.Shop;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.mock.MockOrderRpcService;
import com.kongmu373.wxshop.result.HttpResponse;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrderIntegrationTest extends AbstractIntegrationTest {
    private static final String API_PREFIX = "/api/v1/order";

    @Autowired
    private MockOrderRpcService mockOrderRpcService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(mockOrderRpcService);
    }

    @Test
    public void canPlaceOrder() throws JsonProcessingException {
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");

        AddToShoppingCartItem item = AddToShoppingCartItem.create(4, 3);
        AddToShoppingCartItem item2 = AddToShoppingCartItem.create(5, 5);
        ShopCartRequest cartRequest = ShopCartRequest.create(Arrays.asList(item, item2));

        when(mockOrderRpcService.placeOrder(any(ShopCartRequest.class), anyMap(), anyLong(), any(User.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Order order = new Order();
                order.setShopId(invocationOnMock.getArgument(2));
                User user = invocationOnMock.getArgument(3);
                order.setAddress(user.getAddress());
                order.setUserId(user.getId());
                return order;
            }
        });

        HttpResponse response = getHttpResponseFromSendHttp("POST", API_PREFIX, objectMapper.writeValueAsString(cartRequest), cookieAndUser.getCookie());
        String body = response.getBody();
        Result<LinkedHashMap> result = asJsonObject(body, new TypeReference<Result<LinkedHashMap>>() {
        });
        LinkedHashMap data = result.data();
        LinkedHashMap shop = (LinkedHashMap) data.get("shop");
        ArrayList goods = (ArrayList) data.get("goods");
        Assertions.assertEquals(2, shop.get("id"));
        Assertions.assertEquals("shop2", shop.get("name"));
        Assertions.assertEquals("火星", data.get("address"));
        Assertions.assertEquals(Arrays.asList(4, 5),
                goods.stream().map(good -> ((LinkedHashMap) good).get("id")).collect(toList())
        );
        Assertions.assertEquals(Arrays.asList(3, 5),
                goods.stream().map(good -> ((LinkedHashMap) good).get("number")).collect(toList())
        );
        logout(cookieAndUser.getCookie());
    }

    @Test
    public void canRollBackIfDeductStockFailed() throws JsonProcessingException {
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");

        AddToShoppingCartItem item = AddToShoppingCartItem.create(4, 3);
        AddToShoppingCartItem item2 = AddToShoppingCartItem.create(5, 6);
        ShopCartRequest cartRequest = ShopCartRequest.create(Arrays.asList(item, item2));

        HttpResponse response = getHttpResponseFromSendHttp("POST", API_PREFIX, objectMapper.writeValueAsString(cartRequest), cookieAndUser.getCookie());

        Assertions.assertEquals(410, response.getCode());
        logout(cookieAndUser.getCookie());
        canPlaceOrder();


    }

    @Test
    public void canDeleteOrder() throws Exception {
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");
        PageResult<OrderResponse> orderResponsePageResult = mockPageResultOfOrderResponse();
        when(mockOrderRpcService.getPageOfOrders(anyLong(), anyInt(), anyInt(), anyString()))
                .thenReturn(orderResponsePageResult);
        // 获取当前订单
        HttpResponse getPageOfOrders = getHttpResponseFromSendHttp("GET", API_PREFIX + "?pageSize=2&pageNum=1&status=delivered", null, cookieAndUser.getCookie());

//        objectMapper.getTypeF
        PageResult<OrderResponse> pageResultOfOrderResponse = objectMapper.readValue(getPageOfOrders.getBody(), new TypeReference<PageResult<OrderResponse>>() {
        });
        String orderResponseListJson = objectMapper.writeValueAsString(pageResultOfOrderResponse.data());
        List<OrderResponse> orderResponseList = objectMapper.readValue(orderResponseListJson, new TypeReference<List<OrderResponse>>() {
        });
//        Assertions.assertEquals();
        Assertions.assertEquals(1, pageResultOfOrderResponse.pageNum());
        Assertions.assertEquals(1, pageResultOfOrderResponse.totalPage());
        Assertions.assertEquals(2, pageResultOfOrderResponse.pageSize());
        Assertions.assertEquals(Arrays.asList("shop1"),
                orderResponseList.stream().map(OrderResponse::shop).map(Shop::getName).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList("goods1"),
                orderResponseList.stream().map(OrderResponse::goods).flatMap(List::stream).map(ShopCartItem::getName).collect(Collectors.toList()));
        // 删除某个订单
        when(mockOrderRpcService.deleteOrder(1L, 1L))
                .thenReturn(mockDeletedOrderResponse());
        HttpResponse delete = getHttpResponseFromSendHttp("DELETE", API_PREFIX + "/1", null, cookieAndUser.getCookie());
        Result<OrderResponse> orderResponseResult = objectMapper.readValue(delete.getBody(), new TypeReference<Result<OrderResponse>>() {
        });
        String orderResponseResultJson = objectMapper.writeValueAsString(orderResponseResult.data());
        OrderResponse orderResponse = objectMapper.readValue(orderResponseResultJson, new TypeReference<OrderResponse>() {
        });
        Assertions.assertEquals(DataStatus.DELETED.getName(), orderResponse.status());
        Assertions.assertEquals(1L, orderResponse.id());
        Assertions.assertEquals(1, orderResponse.goods().size());
        Assertions.assertEquals(1L, orderResponse.goods().get(0).getId());
        Assertions.assertEquals(5L, orderResponse.goods().get(0).getNumber());
        // 再次获取当前订单


    }

    @Test
    public void updateOrderReturnNotFound() throws Exception {
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");
    }

    @Test
    public void updateOrder() throws Exception {
        // userId = 1
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");
        // userId = 2
        CookieAndUser cookieAndUser2 = loginAndGetCookie("13426777854");

        // bad request
        HttpResponse response = getHttpResponseFromSendHttp("POST", API_PREFIX + "/1", null, cookieAndUser.getCookie());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());


        Order orderRequest = new Order();
        orderRequest.setId(2L);
        orderRequest.setExpressCompany("圆通");
        orderRequest.setExpressId("YTO1234");

        Order orderInDB = new Order();
        orderInDB.setId(2L);
        orderInDB.setUserId(3L);
        orderInDB.setTotalPrice(700L);
        orderInDB.setAddress("火星");
        orderInDB.setStatus("pending");
        orderInDB.setShopId(1L);

        when(mockOrderRpcService.getOrderByOrderId(2L)).thenReturn(orderInDB);
        // forbidden
        HttpResponse response2 = getHttpResponseFromSendHttp("POST", API_PREFIX + "/2", objectMapper.writeValueAsString(orderRequest), cookieAndUser2.getCookie());
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response2.getCode());

        // success of Express
        Order copy = new Order();
        copy.setId(2L);
        copy.setExpressCompany("圆通");
        copy.setExpressId("YTO1234");
        when(mockOrderRpcService.updateOrders(copy)).thenReturn(mockUpdateOrderResponse(
                "圆通",
                "YT01234",
                "pending"
        ));
        HttpResponse response3 = getHttpResponseFromSendHttp("POST", API_PREFIX + "/2", objectMapper.writeValueAsString(orderRequest), cookieAndUser.getCookie());
        Assertions.assertEquals(HttpStatus.OK.value(), response3.getCode());

        // success of status
        orderRequest.setStatus(DataStatus.DELIVERED.getName());
        orderInDB.setUserId(2L);
        Order copy2 = new Order();
        copy2.setId(2L);
        copy2.setStatus(DataStatus.DELIVERED.getName());
        when(mockOrderRpcService.getOrderByOrderId(2L)).thenReturn(orderInDB);
        when(mockOrderRpcService.updateOrders(copy2)).thenReturn(mockUpdateOrderResponse(
                null,
                null,
                DataStatus.DELIVERED.getName()
        ));
        HttpResponse response4 = getHttpResponseFromSendHttp("POST", API_PREFIX + "/2", objectMapper.writeValueAsString(orderRequest), cookieAndUser2.getCookie());
        Assertions.assertEquals(HttpStatus.OK.value(), response4.getCode());

    }

    private OrderResponse mockUpdateOrderResponse(String expressCompany, String expressId, String status) {
        Shop shop = mockShop();
        OrderResponse orderResponse = OrderResponse.create(2L,
                expressCompany,
                expressId,
                status,
                "火星",
                shop,
                shopCartItems(),
                700L
        );
        return orderResponse;
    }

    private OrderResponse mockDeletedOrderResponse() {
        Shop shop = mockShop();
        OrderResponse orderResponse = OrderResponse.create(1L,
                "顺丰",
                "运单1234567",
                "deleted",
                "火星",
                shop,
                shopCartItems(),
                1400L
        );
        return orderResponse;
    }

    private PageResult<OrderResponse> mockPageResultOfOrderResponse() {
        Shop shop = mockShop();
        OrderResponse orderResponse = OrderResponse.create(1L,
                "顺丰",
                "运单1234567",
                "delivered",
                "火星",
                shop,
                shopCartItems(),
                1400L
        );
        List<OrderResponse> orderResponses = Collections.singletonList(orderResponse);
        return PageResult.create(1, 2, 1, orderResponses);
    }

    private List<ShopCartItem> shopCartItems() {
        ShopCartItem item = new ShopCartItem();
        item.setId(1L);
        item.setNumber(5L);
        return Collections.singletonList(item);
    }

    private Shop mockShop() {
        Shop shop = new Shop();
        shop.setId(1L);
        return shop;
    }
}
