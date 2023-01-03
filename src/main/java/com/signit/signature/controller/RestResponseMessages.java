package com.signit.signature.controller;

public class RestResponseMessages {
    public static final String ADD_SIGN = "Signature added Successfully!";
    public static final String EMPTY_VALIDATION_XMLBASE64_ERROR = "Please enter the xmlBase64 value.";
    public static final String EMPTY_VALIDATION_DOCS_DATA_ERROR = "Please enter the docsData value.";




    private RestResponseMessages() {
        throw new IllegalStateException("Utility class");
    }
}
