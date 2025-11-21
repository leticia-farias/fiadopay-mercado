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
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    public String post(String endpoint, Object body, String token) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(body); // serializa DTO para JSON string
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token) // adiciona o token de autorização
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        return response.body();
    }
    
    public String get(String endpoint, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Authorization", "Bearer " + token) // adiciona o token de autorização
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
