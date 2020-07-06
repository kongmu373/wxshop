package com.kongmu373.wxshop.exception;

import com.kongmu373.wxshop.entity.ErrorMessage;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ErrorException {

    public UnauthorizedException() {
        super(ErrorMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value());
    }
}
