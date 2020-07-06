package com.kongmu373.wxshop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.kevinsawicki.http.HttpRequest;
import com.kongmu373.wxshop.WxshopApplication;
import com.kongmu373.wxshop.result.HttpResponse;
import com.kongmu373.wxshop.result.PageResult;
import com.kongmu373.wxshop.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedHashMap;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GoodsIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void testGoods() throws JsonProcessingException {
        /*
        1. code
        2. login
        3. createGoods
        4. updateGoods
        5. getGoods
        6. deleteGoods
        7. getAllGoods
         */
        // 1-2 code and login  get the cookie .
        String cookie = loginAndGetCookie("13426777850").getCookie();
        String cookie2 = loginAndGetCookie("13426777854").getCookie();
        // 3. create
        // 3.1 normal
        HttpResponse createHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST,
                "/api/v1/goods", "{\n"
                                         + "    \"name\": \"肥皂\",\n"
                                         + "    \"description\": \"纯天然无污染肥皂\",\n"
                                         + "    \"details\": \"这是一块好肥皂\",\n"
                                         + "    \"imgUrl\": \"https://img.url\",\n"
                                         + "    \"price\": 500,\n"
                                         + "    \"stock\": 10,\n"
                                         + "    \"shopId\": 1\n"
                                         + "}", cookie);
        Assertions.assertEquals(HTTP_CREATED, createHttp.getCode());
        // 3.2 badRequest
        HttpResponse createBadRequestHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST,
                "/api/v1/goods", "{\n"
                                         + "    \"name\": \"肥皂\",\n"
                                         + "    \"description\": \"纯天然无污染肥皂\",\n"
                                         + "    \"details\": \"这是一块好肥皂\",\n"
                                         + "    \"imgUrl\": \"https://img.url\",\n"
                                         + "    \"price\": 500,\n"
                                         + "    \"stock\": 10,\n"
                                         + "    \"shopId\": \"dsadasd\" \n"
                                         + "}", cookie);
        Assertions.assertEquals(HTTP_BAD_REQUEST, createBadRequestHttp.getCode());
        // 3.3 Unauthorized
        HttpResponse createUnauthorizedHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST,
                "/api/v1/goods", "{\n"
                                         + "    \"name\": \"肥皂\",\n"
                                         + "    \"description\": \"纯天然无污染肥皂\",\n"
                                         + "    \"details\": \"这是一块好肥皂\",\n"
                                         + "    \"imgUrl\": \"https://img.url\",\n"
                                         + "    \"price\": 500,\n"
                                         + "    \"stock\": 10,\n"
                                         + "    \"shopId\": 1\n"
                                         + "}", null);
        Assertions.assertEquals(HTTP_UNAUTHORIZED, createUnauthorizedHttp.getCode());
        // 3.4 Forbidden
        HttpResponse createForbiddenHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST,
                "/api/v1/goods", "{\n"
                                         + "    \"name\": \"肥皂\",\n"
                                         + "    \"description\": \"纯天然无污染肥皂\",\n"
                                         + "    \"details\": \"这是一块好肥皂\",\n"
                                         + "    \"imgUrl\": \"https://img.url\",\n"
                                         + "    \"price\": 500,\n"
                                         + "    \"stock\": 10,\n"
                                         + "    \"shopId\": 12345\n"
                                         + "}", cookie);
        HttpResponse createForbiddenHttp2 = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST,
                "/api/v1/goods", "{\n"
                                         + "    \"name\": \"肥皂\",\n"
                                         + "    \"description\": \"纯天然无污染肥皂\",\n"
                                         + "    \"details\": \"这是一块好肥皂\",\n"
                                         + "    \"imgUrl\": \"https://img.url\",\n"
                                         + "    \"price\": 500,\n"
                                         + "    \"stock\": 10,\n"
                                         + "    \"shopId\": 1\n"
                                         + "}", cookie2);
        Assertions.assertEquals(HTTP_FORBIDDEN, createForbiddenHttp.getCode());
        Assertions.assertEquals(HTTP_FORBIDDEN, createForbiddenHttp2.getCode());
        // 4. update
        // 4.1 normal
        String createGoodsJSON = createHttp.getBody();

        Result result = objectMapper.readValue(createGoodsJSON, Result.class);
        LinkedHashMap<String, Object> createGoods = (LinkedHashMap<String, Object>) (result.data());
        HttpResponse updateHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST, "/api/v1/goods/" + createGoods.get("id"),
                "{ \n"
                        + "    \"description\": \"纯天然无污染肥皂1\",\n"
                        + "    \"details\": \"这是一块好肥皂1\",\n"
                        + "    \"imgUrl\": \"https://img.url\",\n"
                        + "    \"price\": 500,\n"
                        + "    \"stock\": 10"
                        + "}", cookie);
        Assertions.assertEquals(HTTP_OK, updateHttp.getCode());

        // 4.2 Not Found
        HttpResponse updateNotFoundHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST, "/api/v1/goods/9999",
                "{ \n"
                        + "    \"description\": \"纯天然无污染肥皂\",\n"
                        + "    \"details\": \"这是一块好肥皂\",\n"
                        + "    \"imgUrl\": \"https://img.url\",\n"
                        + "    \"price\": 500,\n"
                        + "    \"stock\": 10"
                        + "}", cookie);
        Assertions.assertEquals(HTTP_NOT_FOUND, updateNotFoundHttp.getCode());

        // 4.3 Bad Request
        HttpResponse updateBadRequestHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST, "/api/v1/goods/" + createGoods.get("id"),
                "{ \n"
                        + "    \"description\": \"纯天然无污染肥皂\",\n"
                        + "    \"details\": \"这是一块好肥皂\",\n"
                        + "    \"imgUrl\": \"https://img.url\",\n"
                        + "    \"price\": 500,\n"
                        + "    \"stock\": \"aa\""
                        + "}", cookie);
        Assertions.assertEquals(HTTP_BAD_REQUEST, updateBadRequestHttp.getCode());

        // 4.4 Forbidden

        HttpResponse updateForbiddenHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST, "/api/v1/goods/" + createGoods.get("id"),
                "{ \n"
                        + "    \"description\": \"纯天然无污染肥皂\",\n"
                        + "    \"details\": \"这是一块好肥皂\",\n"
                        + "    \"imgUrl\": \"https://img.url\",\n"
                        + "    \"price\": 500,\n"
                        + "    \"stock\": 11"
                        + "}", cookie2);
        Assertions.assertEquals(HTTP_FORBIDDEN, updateForbiddenHttp.getCode());
        // 5. getGoods
        HttpResponse getGoodsHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/goods/" + createGoods.get("id"), null, cookie);
        Assertions.assertEquals(HTTP_OK, getGoodsHttp.getCode());

        // 6. getListGoods
        HttpResponse getGoodsListHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/goods?pageNum=1&pageSize=2", null, cookie);
        Assertions.assertEquals(HTTP_OK, getGoodsListHttp.getCode());
        PageResult pageResult = objectMapper.readValue(getGoodsListHttp.getBody(), PageResult.class);
        Assertions.assertEquals(1, pageResult.pageNum());
        Assertions.assertEquals(2, pageResult.pageSize());
        Assertions.assertNotNull(pageResult.totalPage());

        HttpResponse getGoodsListHttp2 = getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/goods?pageNum=1&pageSize=2&shopId=1", null, cookie);
        Assertions.assertEquals(HTTP_OK, getGoodsListHttp2.getCode());
        PageResult pageResult2 = objectMapper.readValue(getGoodsListHttp2.getBody(), PageResult.class);
        List<LinkedHashMap<String, Object>> list = pageResult2.data();
        Assertions.assertEquals(1, pageResult2.pageNum());
        Assertions.assertEquals(2, pageResult2.pageSize());
        Assertions.assertNotNull(pageResult.totalPage());

        // 7. deleteGoods
        // 7.1 bad request
        HttpResponse deleteHttp2 = getHttpResponseFromSendHttp(HttpRequest.METHOD_DELETE, "/api/v1/goods/dada" + createGoods.get("id"), null, cookie);
        Assertions.assertEquals(HTTP_BAD_REQUEST, deleteHttp2.getCode());
        // 7.2 NOT FOUND
        HttpResponse deleteHttp3 = getHttpResponseFromSendHttp(HttpRequest.METHOD_DELETE, "/api/v1/goods/999999", null, cookie);
        Assertions.assertEquals(HTTP_NOT_FOUND, deleteHttp3.getCode());
        // 7.3 Forbidden
        HttpResponse deleteHttp4 = getHttpResponseFromSendHttp(HttpRequest.METHOD_DELETE, "/api/v1/goods/" + createGoods.get("id"), null, cookie2);
        Assertions.assertEquals(HTTP_FORBIDDEN, deleteHttp4.getCode());
        // 7.4 normal
        HttpResponse deleteHttp = getHttpResponseFromSendHttp(HttpRequest.METHOD_DELETE, "/api/v1/goods/" + createGoods.get("id"), null, cookie);
        Assertions.assertEquals(HTTP_NO_CONTENT, deleteHttp.getCode());

        getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/logout", null, cookie);
        getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/logout", null, cookie2);

    }


}
