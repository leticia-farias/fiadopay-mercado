package com.techsphere.fiadopaymercado.domain;

import java.math.BigDecimal;

public record PaymentResponse(String id, String status, String method, BigDecimal amount, Integer installments, BigDecimal total) {}