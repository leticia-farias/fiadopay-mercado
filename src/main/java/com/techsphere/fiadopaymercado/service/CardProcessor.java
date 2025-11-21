package com.techsphere.fiadopaymercado.service;

import java.math.BigDecimal;

import com.techsphere.fiadopaymercado.domain.PaymentRequest;
import com.techsphere.mercado.annotations.AntiFraud;
import com.techsphere.mercado.annotations.PaymentStrategy;

@PaymentStrategy("CARD")
public class CardProcessor implements PaymentProcessor {

 @Override
 public PaymentRequest createRequest(BigDecimal amount, String currency, Integer installments, String orderId) {
     return new PaymentRequest("CARD", currency, amount, installments, orderId);
 }
}