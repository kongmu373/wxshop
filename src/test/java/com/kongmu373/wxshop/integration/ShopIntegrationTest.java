package com.kongmu373.wxshop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kongmu373.wxshop.WxshopApplication;
import com.kongmu373.wxshop.generated.Shop;
import com.kongmu373.wxshop.result.HttpResponse;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedHashMap;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ShopIntegrationTest extends AbstractIntegrationTest {

    private static final String API_PREFIX = "/api/v1/shop";


    @Test
    public void testGetShopListSucceed() throws JsonProcessingException {
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");
        HttpResponse get = getHttpResponseFromSendHttp("GET", API_PREFIX + "?pageNum=1&pageSize=5", null, cookieAndUser.getCookie());
        Assertions.assertEquals(HttpStatus.OK.value(), get.getCode());
        PageResult<List<Shop>> pageResult = objectMapper.readValue(get.getBody(), new TypeReference<PageResult<List<Shop>>>() {
        });
        Assertions.assertEquals(1, pageResult.pageNum());
        Assertions.assertEquals(5, pageResult.pageSize());
        Assertions.assertEquals(1, pageResult.totalPage());
        Assertions.assertEquals(2, pageResult.data().size());

        logout(cookieAndUser.getCookie());
        HttpResponse getWithUserLogout = getHttpResponseFromSendHttp("GET", API_PREFIX + "?pageNum=1&pageSize=5", null, cookieAndUser.getCookie());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), getWithUserLogout.getCode());
    }

    @Test
    public void testCreateShop() throws JsonProcessingException {
        // Unauthorized
        HttpResponse unauthorizedHttp = getHttpResponseFromSendHttp("POST", API_PREFIX,
                "{\n"
                        + "    \"name\": \"我的店铺\",\n"
                        + "    \"description\": \"我的苹果专卖店\",\n"
                        + "    \"imgUrl\": \"https://img.url\",\n"
                        + "}", null);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), unauthorizedHttp.getCode());
        // Bad Request
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");
        HttpResponse badRequestHttp = getHttpResponseFromSendHttp("POST", API_PREFIX, null, cookieAndUser.getCookie());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), badRequestHttp.getCode());
        HttpResponse badRequestHttp2 = getHttpResponseFromSendHttp("POST", API_PREFIX,
                "{\n"
                        + "    \"name\": 123,\n"
                        + "    \"description\": \"我的苹果专卖店\",\n"
                        + "    \"imgUrl\": \"https://img.url\",\n"
                        + "}", cookieAndUser.getCookie());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), badRequestHttp2.getCode());
        // Succeed
        HttpResponse succeedHttp = getHttpResponseFromSendHttp("POST", API_PREFIX,
                "{\n"
                        + "    \"name\": \"我的店铺\",\n"
                        + "    \"description\": \"我的苹果专卖店\",\n"
                        + "    \"imgUrl\": \"https://img.url\"\n"
                        + "}", cookieAndUser.getCookie());
        String body = succeedHttp.getBody();
        Result<LinkedHashMap> shopResult = objectMapper.readValue(body, new TypeReference<Result<LinkedHashMap>>() {
        });

        Assertions.assertEquals(HttpStatus.CREATED.value(), succeedHttp.getCode());
        Assertions.assertEquals(4, shopResult.data().get("id"));
        Assertions.assertEquals("我的店铺", shopResult.data().get("name"));
        Assertions.assertEquals("我的苹果专卖店", shopResult.data().get("description"));
        Assertions.assertEquals("https://img.url", shopResult.data().get("imgUrl"));
        Assertions.assertEquals(Math.toIntExact(cookieAndUser.getUser().getId()), shopResult.data().get("ownerUserId"));
        logout(cookieAndUser.getCookie());
    }

    @Test
    public void updateShopTest() throws JsonProcessingException {
        // Unauthorized
        HttpResponse unauthorizedHttp = getHttpResponseFromSendHttp("POST", API_PREFIX + "/1",
                "{\n"
                        + "\"id\": 1 ,\n"
                        + "    \"name\": \"我的店铺\",\n"
                        + "    \"description\": \"我的苹果专卖店\",\n"
                        + "    \"imgUrl\": \"https://img.url\",\n"
                        + "}", null);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), unauthorizedHttp.getCode());
        // Bad Request
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");
        HttpResponse badRequestHttp = getHttpResponseFromSendHttp("POST", API_PREFIX + "/1", null, cookieAndUser.getCookie());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), badRequestHttp.getCode());
        HttpResponse badRequestHttp2 = getHttpResponseFromSendHttp("POST", API_PREFIX + "/1",
                "{\n"
                        + "\"id\": 1aaaa, \n"
                        + "    \"name\": 123,\n"
                        + "    \"description\": \"我的苹果专卖店\",\n"
                        + "    \"imgUrl\": \"https://img.url\"\n"
                        + "}", cookieAndUser.getCookie());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), badRequestHttp2.getCode());

        // Forbidden
        HttpResponse forbiddenHttp = getHttpResponseFromSendHttp("POST", API_PREFIX + "/3",
                "{\n"
                        + "    \"id\": 3,\n"
                        + "    \"name\": \"我的店铺\",\n"
                        + "    \"description\": \"我的苹果专卖店\",\n"
                        + "    \"imgUrl\": \"https://img.url\"\n"
                        + "}", cookieAndUser.getCookie());

        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), forbiddenHttp.getCode());

        // NOT FOUND
        HttpResponse notFoundHttp = getHttpResponseFromSendHttp("POST", API_PREFIX + "/333",
                "{\n"
                        + "\"id\": 333, \n"
                        + "    \"name\": \"我的店铺\",\n"
                        + "    \"description\": \"我的苹果专卖店\",\n"
                        + "    \"imgUrl\": \"https://img.url\"\n"
                        + "}", cookieAndUser.getCookie());

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), notFoundHttp.getCode());

        HttpResponse normalHttp = getHttpResponseFromSendHttp("POST", API_PREFIX + "/1",
                "{\n"
                        + "\"id\": 1, \n"
                        + "    \"name\": \"我的店铺\",\n"
                        + "    \"description\": \"我的苹果专卖店\",\n"
                        + "    \"imgUrl\": \"https://img.url\"\n"
                        + "}", cookieAndUser.getCookie());

        Assertions.assertEquals(HttpStatus.OK.value(), normalHttp.getCode());

        logout(cookieAndUser.getCookie());
    }

    @Test
    public void getShopTest() throws JsonProcessingException {
        // Unauthorized
        HttpResponse unauthorizedHttp = getHttpResponseFromSendHttp("GET", API_PREFIX + "/1",
                null, null);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), unauthorizedHttp.getCode());

        // NOT FOUND
        CookieAndUser cookieAndUser = loginAndGetCookie("13426777850");

        HttpResponse notFoundHttp = getHttpResponseFromSendHttp("GET", API_PREFIX + "/11111", null, cookieAndUser.getCookie());
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), notFoundHttp.getCode());

        // NORMAL
        HttpResponse normalHttp = getHttpResponseFromSendHttp("GET", API_PREFIX + "/1", null, cookieAndUser.getCookie());
        Result<LinkedHashMap<String, Object>> readValue = objectMapper.readValue(normalHttp.getBody(), new TypeReference<Result<LinkedHashMap<String, Object>>>() {
        });
        Assertions.assertEquals(HttpStatus.OK.value(), normalHttp.getCode());
        Assertions.assertEquals("shop1", readValue.data().get("name"));
        Assertions.assertEquals("desc1", readValue.data().get("description"));
        Assertions.assertEquals("url1", readValue.data().get("imgUrl"));
        logout(cookieAndUser.getCookie());
    }
}
