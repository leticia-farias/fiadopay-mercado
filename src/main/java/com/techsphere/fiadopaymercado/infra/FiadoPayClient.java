package com.techsphere.fiadopaymercado.infra;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsphere.fiadopaymercado.domain.RefundRequest;

public class FiadoPayClient {
    private static final String baseUrl = "http://localhost:8080";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public FiadoPayClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    public <T> T post(String endpoint, Object body, Class<T> responseType, String token) {
        try {
            String jsonBody = objectMapper.writeValueAsString(body);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            if (token != null) {
                builder.header("Authorization", "Bearer " + token);
            }

            HttpRequest request = builder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Erro API (" + response.statusCode() + "): " + response.body());
            }

            return objectMapper.readValue(response.body(), responseType);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erro na comunicação com FiadoPay: " + e.getMessage(), e);
        }
    }

    public <T> T get(String endpoint, Class<T> responseType, String token) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .timeout(Duration.ofSeconds(10))
                    .GET();

            if (token != null) {
                builder.header("Authorization", "Bearer " + token);
            }

            HttpRequest request = builder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Erro API (" + response.statusCode() + "): " + response.body());
            }

            return objectMapper.readValue(response.body(), responseType);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erro na comunicação com FiadoPay: " + e.getMessage(), e);
        }
    }

    // método para Devolução
    public void refund(String paymentId, String token) {
        try {
            RefundRequest bodyObj = new RefundRequest(paymentId);
            String jsonBody = objectMapper.writeValueAsString(bodyObj);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/fiadopay/gateway/refunds")) 
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 204) {
                throw new RuntimeException("Falha no estorno (Status " + response.statusCode() + "): " + response.body());
            }
            
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erro ao solicitar estorno: " + e.getMessage(), e);
        }
    }
}
