package com.kongmu373.wxshop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kongmu373.wxshop.WxshopApplication;
import com.kongmu373.wxshop.entity.AddToShoppingCartItem;
import com.kongmu373.wxshop.entity.ShopCartRequest;
import com.kongmu373.wxshop.result.HttpResponse;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
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

        logout(cookieAndUser.getCookie());
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
        Result<LinkedHashMap> result = asJsonObject(post.getBody(), new TypeReference<Result<LinkedHashMap>>() {
        });


        Assertions.assertEquals(1, ((LinkedHashMap) result.data().get("shop")).get("id"));
        Assertions.assertEquals(Sets.newHashSet(Arrays.asList(1, 2)), ((ArrayList) result.data().get("goods")).stream().map(goods -> ((LinkedHashMap) goods).get("id")).collect(Collectors.toSet()));
        Assertions.assertEquals(Sets.newHashSet(Arrays.asList(2, 2)), ((ArrayList) result.data().get("goods")).stream().map(goods -> ((LinkedHashMap) goods).get("number")).collect(Collectors.toSet()));
        Assertions.assertTrue(((ArrayList) result.data().get("goods")).stream().allMatch(goods -> ((LinkedHashMap) goods).get("shopId").equals(1)));
        logout(cookieAndUser.getCookie());
    }

    @Test
    public void deleteCart() throws JsonProcessingException {
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");
        HttpResponse delete = getHttpResponseFromSendHttp("DELETE", API_PREFIX + "/5",
                null,
                cookieAndUser.getCookie());
        Result<LinkedHashMap> result = asJsonObject(delete.getBody(), new TypeReference<Result<LinkedHashMap>>() {
        });
        Assertions.assertEquals(2, ((LinkedHashMap) result.data().get("shop")).get("id"));
        Assertions.assertEquals(1, ((ArrayList) result.data().get("goods")).size());
        Assertions.assertEquals(4, ((LinkedHashMap) ((ArrayList) result.data().get("goods")).get(0)).get("id"));
        Assertions.assertEquals(200, ((LinkedHashMap) ((ArrayList) result.data().get("goods")).get(0)).get("number"));
        logout(cookieAndUser.getCookie());

    }
}
