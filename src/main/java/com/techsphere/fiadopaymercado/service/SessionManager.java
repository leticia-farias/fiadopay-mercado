package com.techsphere.fiadopaymercado.service;

import java.time.LocalDateTime;

// Guardar o accessToken atual e o momento que ele expira
public class SessionManager {
	private static SessionManager instance;
	private String currentToken;
	private LocalDateTime expirationTime;
	private String clientId; // salvo após o cadastro
	private String clientSecret; 
}
