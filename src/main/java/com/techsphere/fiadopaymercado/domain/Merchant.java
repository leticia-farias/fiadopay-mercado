package com.techsphere.fiadopaymercado.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representa os dados da loja retornados pelo FiadoPay.
 * Espelha a entidade do servidor.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // evita erro se o servidor mandar campos extras
public class Merchant {

    private Long id;
    private String name;
    private String clientId;
    private String clientSecret;
    private String webhookUrl;
    private String status; // ACTIVE ou BLOCKED

    public Merchant() {}

    public Merchant(String name, String webhookUrl) {
        this.name = name;
        this.webhookUrl = webhookUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Merchant{" +
                "name='" + name + '\'' +
                ", clientId='" + clientId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}