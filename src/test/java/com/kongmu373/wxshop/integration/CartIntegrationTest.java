package com.kongmu373.wxshop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kongmu373.wxshop.WxshopApplication;
import com.kongmu373.wxshop.entity.AddToShoppingCartItem;
import com.kongmu373.wxshop.entity.ShopCartData;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.result.HttpResponse;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CartIntegrationTest extends AbstractIntegrationTest {
    private static final String API_PREFIX = "/api/v1/shoppingCart";

    @Test
    public void getShopCart() throws JsonProcessingException {
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");
        HttpResponse get = getHttpResponseFromSendHttp("GET", API_PREFIX + "?pageNum=2&pageSize=1", null, cookieAndUser.getCookie());
        PageResult<LinkedHashMap> result = asJsonObject(get.getBody(), new TypeReference<PageResult<LinkedHashMap>>() {
        });
        System.out.println();
        LinkedHashMap shopCartItem = result.data().get(0);
        LinkedHashMap shop = (LinkedHashMap) shopCartItem.get("shop");
        List goods = (List) shopCartItem.get("goods");
        Assertions.assertEquals(2, result.pageNum());
        Assertions.assertEquals(1, result.pageSize());
        Assertions.assertEquals(2, result.totalPage());
        Assertions.assertEquals(1, result.data().size());
        Assertions.assertEquals(2, shop.get("id"));
        Assertions.assertEquals(Arrays.asList(4, 5), goods.stream().map(item -> ((LinkedHashMap) item).get("id")).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(100, 200), goods.stream().map(item -> ((LinkedHashMap) item).get("price")).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(200, 300), goods.stream().map(item -> ((LinkedHashMap) item).get("number")).collect(Collectors.toList()));

    }

    @Test
    public void addCart() throws JsonProcessingException {
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");
        AddToShoppingCartItem item = AddToShoppingCartItem.create(2L, 2);
        AddToShoppingCartItem item2 = AddToShoppingCartItem.create(1L, 2);

        ShopCartRequest shopCartRequest = ShopCartRequest.create(Arrays.asList(item, item2));
        HttpResponse post = getHttpResponseFromSendHttp("POST", API_PREFIX,
                objectMapper.writeValueAsString(shopCartRequest),
                cookieAndUser.getCookie());
        Result<ShopCartData> result = objectMapper.readValue(post.getBody(), new TypeReference<Result<ShopCartData>>() {
        });

        System.out.println();
    }
}
