package com.techsphere.fiadopaymercado.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentResponse(
    String id,
    String status,
    String method,
    BigDecimal amount,
    Integer installments,
    BigDecimal total,
    BigDecimal interestRate
) {}