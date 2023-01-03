package com.signit.signature.controller.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.signit.signature.controller.RestResponseMessages.EMPTY_VALIDATION_DOCS_DATA_ERROR;
import static com.signit.signature.controller.RestResponseMessages.EMPTY_VALIDATION_XMLBASE64_ERROR;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignatureDto {

    @JsonSerialize
    @NotNull(message = EMPTY_VALIDATION_XMLBASE64_ERROR)
    @NotBlank(message = EMPTY_VALIDATION_XMLBASE64_ERROR)
    private String xmlBase64;
    @NotNull(message = EMPTY_VALIDATION_DOCS_DATA_ERROR)
    @NotBlank(message = EMPTY_VALIDATION_DOCS_DATA_ERROR)
    @JsonSerialize
    private String docsData;

}
