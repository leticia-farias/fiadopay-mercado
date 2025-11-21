package com.techsphere.fiadopaymercado.service;

import java.math.BigDecimal;

import com.techsphere.fiadopaymercado.domain.PaymentRequest;

public interface PaymentProcessor {
	// Prepara o DTO com dados específicos do método
    PaymentRequest createRequest(BigDecimal amount, String currency, Integer installments, String orderId);
}