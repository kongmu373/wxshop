package com.kongmu373.wxshop.exception;

import com.kongmu373.wxshop.entity.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends ErrorException {
    public ForbiddenException() {
        super(ErrorMessage.FORBIDDEN, HttpStatus.FORBIDDEN.value());
    }
}
