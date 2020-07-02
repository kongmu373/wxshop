package com.kongmu373.wxshop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kongmu373.wxshop.entity.ErrorMessage;
import com.kongmu373.wxshop.result.Result;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

@Component
public class ShiroLoginFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.getWriter().print(new ObjectMapper().writeValueAsString(Result.create(ErrorMessage.UNAUTHORIZED, null)));
        return false;
    }
}
