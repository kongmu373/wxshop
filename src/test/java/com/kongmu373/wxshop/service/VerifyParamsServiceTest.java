package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.result.TelAndCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VerifyParamsServiceTest {
    private final String CORRECT_TEL = "13426777851";
    private final VerifyParamsService verifyParamsService = new VerifyParamsService();

    @Test
    void verifyTelParam() {
        assertTrue(verifyParamsService.verifyTelParam(TelAndCode.builder().setTel(CORRECT_TEL).build()));
    }

    @Test
    void verifyTelParamIfTelIsNull() {
        assertFalse(verifyParamsService.verifyTelParam(TelAndCode.builder().build()));
        assertFalse(verifyParamsService.verifyTelParam(null));
    }
}
