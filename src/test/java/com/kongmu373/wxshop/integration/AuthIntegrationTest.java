package com.kongmu373.wxshop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.kevinsawicki.http.HttpRequest;
import com.kongmu373.wxshop.WxshopApplication;
import com.kongmu373.wxshop.result.HttpResponse;
import com.kongmu373.wxshop.result.LoginResult;
import com.kongmu373.wxshop.result.TelAndCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthIntegrationTest extends AbstractIntegrationTest {


    @Test
    public void returnHttpOKWhenParameterIsCorrect() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/v1/code"))
                                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                                   .accept(MediaType.APPLICATION_JSON_VALUE)
                                   .send(objectMapper.writeValueAsString(TelAndCode.builder().setTel("13426777855").build()))
                                   .code();
        Assertions.assertEquals(HTTP_OK, responseCode);
    }

    @Test
    public void returnHttpBadRequestWhenParameterIsWrong() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/v1/code"))
                                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                                   .accept(MediaType.APPLICATION_JSON_VALUE)
                                   .send(objectMapper.writeValueAsString(TelAndCode.builder().setTel("134267778501").build()))
                                   .code();
        Assertions.assertEquals(HTTP_BAD_REQUEST, responseCode);
    }

    @Test
    public void testLoginAndLogout() throws JsonProcessingException {
        /*
            1. status
            2. code
            3. login
            4. status
            5. logout
            6. status
         */

        // 1. status  未登录
        HttpResponse http1 = getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/status", null, null);
        Assertions.assertEquals(HTTP_OK, http1.getCode());
        LoginResult value = objectMapper.readValue(http1.getBody(), LoginResult.class);
        Assertions.assertFalse(value.login() != null && value.login());

        String cookie = loginAndGetCookie("13426777856").getCookie();
        // 4. status
        HttpResponse http4 = getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/status", null, cookie);
        Assertions.assertEquals(HTTP_OK, http4.getCode());
        LoginResult loginResult = objectMapper.readValue(http4.getBody(), LoginResult.class);
        Assertions.assertTrue(loginResult != null && loginResult.login());
        Assertions.assertEquals("13426777856", Objects.requireNonNull(loginResult.user()).getTel());

//         5. logout
        HttpResponse http5 = getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/logout", null, cookie);
        Assertions.assertEquals(HTTP_OK, http5.getCode());

        // 6. status
        HttpResponse http6 = getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/status", null, cookie);
        Assertions.assertEquals(HTTP_OK, http6.getCode());
        LoginResult value6 = objectMapper.readValue(http6.getBody(), LoginResult.class);
        Assertions.assertFalse(value6.login() != null && value6.login());
    }

    @Test
    public void returnUnAuthorizedIfNotLogin() throws JsonProcessingException {
        HttpResponse http = getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/any", null, null);
        Assertions.assertEquals(HTTP_UNAUTHORIZED, http.getCode());
    }


}
