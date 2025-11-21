package com.techsphere.fiadopaymercado.service;

import com.techsphere.fiadopaymercado.domain.PaymentRequest;
import com.techsphere.fiadopaymercado.domain.PaymentResponse;
import com.techsphere.fiadopaymercado.infra.FiadoPayClient;
import com.techsphere.fiadopaymercado.infra.PaymentRepository;
import com.techsphere.fiadopaymercado.infra.PaymentStrategyFactory;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentService {

    private final FiadoPayClient client;
    private final PaymentRepository repository;
    private final SessionManager sessionManager;

    public PaymentService(FiadoPayClient client) {
        this.client = client;
        this.repository = PaymentRepository.getInstance();
        this.sessionManager = SessionManager.getInstance();
    }

    public void processarPagamento(String method, BigDecimal amount, Integer installments) {
        try {
            // 1. Obtém o processador via Reflexão (com validação de Antifraude)
            PaymentProcessor processor = PaymentStrategyFactory.getProcessor(method, amount);

            // 2. Cria o DTO de requisição
            String orderId = UUID.randomUUID().toString();
            PaymentRequest request = processor.createRequest(amount, "BRL", installments, orderId);

            // 3. Envia para o FiadoPay
            String token = sessionManager.getAccessToken();
            System.out.println("Enviando pagamento [" + method + "] de R$ " + amount + "...");
            
            PaymentResponse response = client.post(
                "/fiadopay/gateway/payments", 
                request, 
                PaymentResponse.class, 
                token
            );

            // 4. Salva no banco local
            repository.save(response);
            System.out.println("Pagamento CRIADO com sucesso! ID: " + response.id() + " Status: " + response.status());

        } catch (Exception e) {
            System.err.println("Erro ao processar pagamento: " + e.getMessage());
        }
    }

    public void solicitarEstorno(String paymentId) {
        try {
            String token = sessionManager.getAccessToken();
            System.out.println("Solicitando estorno para o ID: " + paymentId);
            
            client.refund(paymentId, token);
            
            System.out.println("Estorno solicitado com sucesso. O Job de reconciliação atualizará o status em breve.");
        } catch (Exception e) {
            System.err.println("Erro ao solicitar estorno: " + e.getMessage());
        }
    }
}
