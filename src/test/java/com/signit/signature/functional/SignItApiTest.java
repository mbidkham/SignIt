package com.signit.signature.functional;

import com.signit.signature.SpringMockMVCHelper;
import com.signit.signature.controller.dto.SignatureDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration()
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SignItApiTest extends SpringMockMVCHelper {
    @Test
    void should_throw_exception_WHEN_input_is_null() throws Exception {
        //************************
        //          Given
        //************************
        SignatureDto input = new SignatureDto(null, "");
        //************************
        //          WHEN
        //************************
        String restResponseMessage = performPostRequest("/sign", input, 400);
        //************************
        //          THEN
        //************************
        Assertions.assertTrue(restResponseMessage.contains("Please enter the xmlBase64 value."));
    }


}

