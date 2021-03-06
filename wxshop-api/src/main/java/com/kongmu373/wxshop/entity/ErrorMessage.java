package com.kongmu373.wxshop.entity;

public final class ErrorMessage {
    /* auth */
    public static final String UNAUTHORIZED = "用户未登录";

    /* goods */
    public static final String GOODS_BAD_REQUEST = "用户的请求中包含错误";
    public static final String FORBIDDEN = "用户尝试操作非自己管理店铺的商品";
    public static final String GOODS_NOT_FOUND = "商品未找到";

}
