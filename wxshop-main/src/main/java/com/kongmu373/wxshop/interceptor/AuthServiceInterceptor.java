package com.kongmu373.wxshop.interceptor;


import com.kongmu373.wxshop.entity.UserContext;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Component
public class AuthServiceInterceptor implements HandlerInterceptor {
    private UserService userService;


    @Autowired
    public AuthServiceInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(200);
            return false;
        }

        Object tel = SecurityUtils.getSubject().getPrincipal();
        if (tel != null) {
            User user = userService.getUserByTel(tel.toString());
            Optional.of(user).ifPresent(UserContext::setCurrentUser);
        }

        if (isWhitelist(request)) {
            return true;
        } else if (UserContext.getCurrentUser() == null) {
            response.setStatus(401);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clearCurrentUser();
    }


    private boolean isWhitelist(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return Arrays.asList(
                "/api/v1/code",
                "/api/v1/login",
                "/api/v1/status",
                "/api/v1/logout",
                "/error",
                "/",
                "/index.html",
                "/manifest.json"
        ).contains(uri) || uri.startsWith("/static/");
    }

}
