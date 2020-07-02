package com.kongmu373.wxshop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.kongmu373.wxshop.result.HttpResponse;
import com.kongmu373.wxshop.result.LoginResult;
import com.kongmu373.wxshop.result.TelAndCode;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;

import static java.net.HttpURLConnection.HTTP_OK;

public class AbstractIntegrationTest {
    @Autowired
    Environment environment;

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    @BeforeEach
    public void initDatabase() {
        // 在每个测试开始前，执行一次flyway:clean flyway:migrate
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();
    }

    public static ObjectMapper objectMapper = new ObjectMapper();

    public String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

    public CookieAndUser loginAndGetCookie(String tel) throws JsonProcessingException {
        HttpResponse http = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST, "/api/v1/code",
                new ObjectMapper().writeValueAsString(TelAndCode.builder().setTel(tel).build()),
                null);
        Assertions.assertEquals(HTTP_OK, http.getCode());

        HttpResponse http2 = getHttpResponseFromSendHttp(HttpRequest.METHOD_POST, "/api/v1/login",
                new ObjectMapper().writeValueAsString(TelAndCode.create(tel, "000000")),
                null);
        Assertions.assertEquals(HTTP_OK, http2.getCode());
        String cookie = http2.getHeaders().get("Set-Cookie").stream().filter(l -> l.contains("JSESSIONID")).findFirst().get();
        HttpResponse http3 = getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/status", null, cookie);
        String userJSON = http3.getBody();
        LoginResult loginResult = objectMapper.readValue(userJSON, LoginResult.class);
        return new CookieAndUser(cookie, loginResult.user());
    }


    protected HttpResponse getHttpResponseFromSendHttp(String method, String apiName, String requestBody, String cookie) throws JsonProcessingException {
        HttpRequest request = createRequest(getUrl(apiName), method);

        if (cookie != null) {
            request.header("Cookie", getSessionId(cookie));
        }
        request.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE);
        if (requestBody != null) {
            request.send(requestBody);
        }
        return HttpResponse.create(request.code(), request.body(), request.headers());
    }

    protected void logout(String cookie) throws JsonProcessingException {
        getHttpResponseFromSendHttp(HttpRequest.METHOD_GET, "/api/v1/logout", null, cookie);
    }

    private HttpRequest createRequest(String url, String method) {
        if ("PATCH".equalsIgnoreCase(method)) {
            // workaround for https://bugs.openjdk.java.net/browse/JDK-8207840
            HttpRequest request = new HttpRequest(url, HttpRequest.METHOD_POST);
            request.header("X-HTTP-Method-Override", "PATCH");
            return request;
        } else {
            return new HttpRequest(url, method);
        }
    }

    protected String getSessionId(String cookie) {
        int semicolonIndex = cookie.indexOf(";");
        return cookie.substring(0, semicolonIndex);
    }
}
