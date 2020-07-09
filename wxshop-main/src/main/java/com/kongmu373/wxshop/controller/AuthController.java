package com.kongmu373.wxshop.controller;

import com.kongmu373.wxshop.entity.UserContext;
import com.kongmu373.wxshop.generate.User;
import com.kongmu373.wxshop.result.LoginResult;
import com.kongmu373.wxshop.result.TelAndCode;
import com.kongmu373.wxshop.service.AuthService;
import com.kongmu373.wxshop.service.VerifyParamsService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    private final VerifyParamsService verifyParamsService;


    @Autowired
    public AuthController(AuthService authService, VerifyParamsService verifyParamsService) {
        this.authService = authService;
        this.verifyParamsService = verifyParamsService;
    }

    @PostMapping("/code")
    public void code(@RequestBody TelAndCode telAndCode, HttpServletResponse response) {
        if (!verifyParamsService.verifyTelParam(telAndCode)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        authService.sendVerificationCode(telAndCode.getTel());
    }

    @PostMapping("/login")
    public void login(@RequestBody TelAndCode telAndCode) {
        UsernamePasswordToken token = new UsernamePasswordToken(telAndCode.getTel(), telAndCode.getCode());
        token.setRememberMe(true);
        SecurityUtils.getSubject().login(token);
    }

    @GetMapping("/status")
    public LoginResult status() {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return LoginResult.create(false, null);
        }
        return LoginResult.create(true, currentUser);
    }

    @GetMapping("/logout")
    public void logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
    }
}
