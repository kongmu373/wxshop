package com.kongmu373.wxshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.kongmu373.wxshop.WxshopApplication;
import com.kongmu373.wxshop.result.HttpResponse;
import com.kongmu373.wxshop.result.LoginResult;
import com.kongmu373.wxshop.result.TelAndCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
public class AuthIntegrationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    Environment environment;

    private String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

    @Test
    public void returnHttpOKWhenParameterIsCorrect() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/code"))
                                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                                   .accept(MediaType.APPLICATION_JSON_VALUE)
                                   .send(objectMapper.writeValueAsString(TelAndCode.builder().setTel("13426777850").build()))
                                   .code();
        Assertions.assertEquals(HTTP_OK, responseCode);
    }

    @Test
    public void returnHttpBadRequestWhenParameterIsWrong() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/code"))
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
        HttpResponse http1 = getHttpResponseFromSendHttp(true, "/api/status", null, null);
        Assertions.assertEquals(HTTP_OK, http1.getCode());
        LoginResult value = new ObjectMapper().readValue(http1.getBody(), LoginResult.class);
        Assertions.assertFalse(value.login() != null && value.login());

        // 2. code
        HttpResponse http2 = getHttpResponseFromSendHttp(false, "/api/code",
                TelAndCode.builder().setTel("13426777850").build(),
                null);
        Assertions.assertEquals(HTTP_OK, http2.getCode());

        // 3. login
        HttpResponse http3 = getHttpResponseFromSendHttp(false, "/api/login",
                TelAndCode.create("13426777850", "000000"),
                null);
        Assertions.assertEquals(HTTP_OK, http3.getCode());
        String cookie = http3.getHeaders().get("Set-Cookie").stream().filter(l -> l.contains("JSESSIONID")).findFirst().get();

        // 4. status
        HttpResponse http4 = getHttpResponseFromSendHttp(true, "/api/status", null, cookie);
        Assertions.assertEquals(HTTP_OK, http4.getCode());
        LoginResult loginResult = new ObjectMapper().readValue(http4.getBody(), LoginResult.class);
        Assertions.assertTrue(loginResult != null && loginResult.login());
        Assertions.assertEquals("13426777850", Objects.requireNonNull(loginResult.user()).getTel());

        // 5. logout
        HttpResponse http5 = getHttpResponseFromSendHttp(true, "/api/logout", null, cookie);
        Assertions.assertEquals(HTTP_OK, http5.getCode());

        // 6. status
        HttpResponse http6 = getHttpResponseFromSendHttp(true, "/api/status", null, cookie);
        Assertions.assertEquals(HTTP_OK, http6.getCode());
        LoginResult value6 = new ObjectMapper().readValue(http1.getBody(), LoginResult.class);
        Assertions.assertFalse(value6.login() != null && value6.login());
    }

    private HttpResponse getHttpResponseFromSendHttp(boolean isGet, String apiName, Object requestBody, String cookie) throws JsonProcessingException {
        HttpRequest request = isGet ? HttpRequest.get(getUrl(apiName)) : HttpRequest.post(getUrl(apiName));
        request.contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        if (cookie != null) {

            request.header("Cookie", getSessionId(cookie));
        }
        if (requestBody != null) {
            request.send(new ObjectMapper().writeValueAsString(requestBody));
        }
        return HttpResponse.create(request.code(), request.body(), request.headers());
    }

    private String getSessionId(String cookie) {
        int semicolonIndex = cookie.indexOf(";");
        return cookie.substring(0, semicolonIndex);
    }

}
