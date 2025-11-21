package com.techsphere.fiadopaymercado.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenResponse(String access_token, String token_type, long expires_in) {}
