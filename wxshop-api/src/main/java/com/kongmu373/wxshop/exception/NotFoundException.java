package com.kongmu373.wxshop.exception;

import com.kongmu373.wxshop.entity.ErrorMessage;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ErrorException {

    public NotFoundException() {
        super(ErrorMessage.GOODS_NOT_FOUND, HttpStatus.NOT_FOUND.value());
    }

    public NotFoundException(String message, int code) {
        super(message, code);
    }
}
