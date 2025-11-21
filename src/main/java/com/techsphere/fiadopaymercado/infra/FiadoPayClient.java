package com.techsphere.fiadopaymercado.infra;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;

//Encapsular java.net.http.HttpClient
public class FiadoPayClient {
	private static final String baseUrl = "http://localhost:8080";
	private static final Duration TIMEOUT = Duration.ofSeconds(5);
	
	private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public FiadoPayClient() {
    	// cria um cliente HTTP com timeout de conexão para não travar o sistema
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
        // instancia o serializador JSON do Jackson
        this.objectMapper = new ObjectMapper();
    }
    
    // chamada POST genérica
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

            return objectMapper.readValue(response.body(), responseType);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erro na comunicação com FiadoPay: " + e.getMessage(), e);
        }
    }

    // chamada GET genérica.
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

            return objectMapper.readValue(response.body(), responseType);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erro na comunicação com FiadoPay: " + e.getMessage(), e);
        }
    }
}