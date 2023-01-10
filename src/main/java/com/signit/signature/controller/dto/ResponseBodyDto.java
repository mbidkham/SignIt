package com.signit.signature.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBodyDto {
    private String signedDoc;
    private int status;
    private String errorMessage;
    private String errorCode;
}
