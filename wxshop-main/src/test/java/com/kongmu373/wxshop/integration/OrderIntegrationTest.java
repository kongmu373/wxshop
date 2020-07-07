package com.kongmu373.wxshop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kongmu373.wxshop.WxshopApplication;
import com.kongmu373.wxshop.api.generate.Order;
import com.kongmu373.wxshop.entity.AddToShoppingCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.mock.MockOrderRpcService;
import com.kongmu373.wxshop.result.HttpResponse;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
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
}
