package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.result.TelAndCode;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class VerifyParamsService {
    /**
     * 是否为11位电话号码
     */
    private static final Pattern PATTERN = Pattern.compile("^1\\d{10}$");

    /**
     * 验证手机号码是否合法
     *
     * @param telAndCode 电话号码以及验证码
     * @return true 为合法, false 为不合法
     */
    public boolean verifyTelParam(TelAndCode telAndCode) {
        if (telAndCode == null) {
            return false;
        }
        if (telAndCode.getTel() == null) {
            return false;
        }
        return PATTERN.matcher(telAndCode.getTel()).find();
    }
}
