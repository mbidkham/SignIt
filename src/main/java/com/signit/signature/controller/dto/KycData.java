package com.signit.signature.controller.dto;

import lombok.Data;

@Data
public class KycData {
    private int kycId;
    private String orgName;
    private String englishName;
    private String arabicName;
    private String email;
    private String city;
    private String region;
    private String country;
}
