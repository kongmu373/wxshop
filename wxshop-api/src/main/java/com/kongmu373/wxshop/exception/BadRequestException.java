package com.kongmu373.wxshop.exception;

import com.kongmu373.wxshop.entity.ErrorMessage;
import org.springframework.http.HttpStatus;

public class BadRequestException extends ErrorException {
    public BadRequestException() {
        super(ErrorMessage.GOODS_BAD_REQUEST, HttpStatus.BAD_REQUEST.value());

    }
}
