package com.techsphere.fiadopaymercado.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Properties;

// Guardar o accessToken atual e o momento que ele expira
public class SessionManager {
	private static SessionManager instance;
	private final Properties props = new Properties();
    private final File file = new File("credentials.properties");
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private Instant tokenExpiration;
	
	private SessionManager() {
        loadFromFile();
    }
	
	public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public boolean hasCredentials() {
        return clientId != null && clientSecret != null;
    }

    public void saveCredentials(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
     
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.setProperty("clientId", clientId);
            props.setProperty("clientSecret", clientSecret);
            props.store(fos, "FiadoPay Client Credentials");
        } catch (IOException e) {
            System.err.println("Erro ao salvar credenciais: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        if (!file.exists()) return;
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
            this.clientId = props.getProperty("clientId");
            this.clientSecret = props.getProperty("clientSecret");
        } catch (IOException e) {
            System.err.println("Erro ao carregar credenciais: " + e.getMessage());
        }
    }

    public void setToken(String token, long expiresInSeconds) {
        this.accessToken = token;
        this.tokenExpiration = Instant.now().plusSeconds(expiresInSeconds - 10);
    }

    public String getAccessToken() {
        return accessToken;
    }
    
    public boolean isTokenValid() {
        return accessToken != null && Instant.now().isBefore(tokenExpiration);
    }

    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
}