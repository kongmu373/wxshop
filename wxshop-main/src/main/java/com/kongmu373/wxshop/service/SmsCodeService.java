package com.kongmu373.wxshop.service;

/**
 * 风险：
 *  1. 批量调用这个sendSmsCode服务，轰炸别人的手机，发大量垃圾短信，向短信服务商给钱的 (需要做限流)
 *  2. 暴力破解
 */
public interface SmsCodeService {
    /**
     * 向一个指定手机号发验证码，返回正确答案
     *
     * @param tel 目标手机号
     * @return 正确答案
     */
    String sendSmsCode(String tel);
}
