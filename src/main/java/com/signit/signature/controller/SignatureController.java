package com.signit.signature.controller;

import com.signit.signature.controller.dto.ResponseBodyDto;
import com.signit.signature.controller.dto.SignatureDto;
import com.signit.signature.service.SignatureService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@AllArgsConstructor
public class SignatureController {

    private final SignatureService signatureService;

    @PostMapping("/sign")
    public ResponseEntity<ResponseBodyDto> sign(@RequestBody @Valid SignatureDto input) {
        signatureService.sign();
        return new ResponseEntity<>(new ResponseBodyDto(RestResponseMessages.ADD_SIGN),
            HttpStatus.OK);
    }

}
