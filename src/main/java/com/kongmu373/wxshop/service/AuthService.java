package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.generated.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final VerificationCodeCheckService verificationCodeCheckService;
    private final SmsCodeService smsCodeService;

    @Autowired
    public AuthService(UserService userService, VerificationCodeCheckService verificationCodeCheckService, SmsCodeService smsCodeService) {
        this.userService = userService;
        this.verificationCodeCheckService = verificationCodeCheckService;
        this.smsCodeService = smsCodeService;
    }

    public User sendVerificationCode(String tel) {
        User user = userService.createUserIfNotExist(tel);
        String code = smsCodeService.sendSmsCode(tel);
        verificationCodeCheckService.add2Cache(tel, code);
        return user;
    }
}
