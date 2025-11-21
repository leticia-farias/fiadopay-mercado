package com.techsphere.fiadopaymercado.service;

import java.math.BigDecimal;

import com.techsphere.fiadopaymercado.domain.PaymentRequest;
import com.techsphere.mercado.annotations.AntiFraud;
import com.techsphere.mercado.annotations.PaymentStrategy;

@PaymentStrategy("PIX")
@AntiFraud(maxAmount = 500.00) // acima de 500 falha
public class PixProcessor implements PaymentProcessor {

    @Override
    public PaymentRequest createRequest(BigDecimal amount, String currency, Integer installments, String orderId) {
        return new PaymentRequest("PIX", currency, amount, 1, orderId); // sem parcelas
    }
}