package com.techsphere.fiadopaymercado;

import com.techsphere.fiadopaymercado.infra.FiadoPayClient;
import com.techsphere.fiadopaymercado.service.AuthService;

public class App {
    public static void main(String[] args) {
        FiadoPayClient client = new FiadoPayClient();
        AuthService authService = new AuthService(client);
        
        try {
            authService.inicializar();
            
        } catch (Exception e) {
            System.err.println("Falha na inicialização: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
    }
}