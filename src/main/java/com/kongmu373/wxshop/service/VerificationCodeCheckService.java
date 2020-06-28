package com.kongmu373.wxshop.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeCheckService {
    private Map<String, String> tel2Code = new ConcurrentHashMap<>();

    public void add2Cache(String tel, String code) {
        tel2Code.put(tel, code);
    }

    public String getCode(String tel) {
        return tel2Code.get(tel);
    }
}
