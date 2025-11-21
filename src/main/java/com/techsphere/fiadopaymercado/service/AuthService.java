package com.techsphere.fiadopaymercado.service;

import com.techsphere.fiadopaymercado.domain.Merchant;
import com.techsphere.fiadopaymercado.domain.MerchantDTO;
import com.techsphere.fiadopaymercado.domain.TokenRequest;
import com.techsphere.fiadopaymercado.domain.TokenResponse;
import com.techsphere.fiadopaymercado.infra.FiadoPayClient;

public class AuthService {
	private final FiadoPayClient client;
    private final SessionManager session;

    public AuthService(FiadoPayClient client) {
        this.client = client;
        this.session = SessionManager.getInstance();
    }

	public void inicializar() {
        // verificar se já tem cadastro
        if (!session.hasCredentials()) {
            System.out.println("Nenhuma credencial encontrada, registrando novo Merchant");
            registrarMerchant();
        } else {
            System.out.println("Credenciais carregadas");
        }
        autenticar();
    }

    private void registrarMerchant() {
        String webhook = "http://localhost:8081/webhook"; 
        MerchantDTO dto = new MerchantDTO("Loja Console Java", webhook);

        Merchant merchant = client.post(
            "/fiadopay/admin/merchants", 
            dto, 
            Merchant.class, 
            null // Sem token para cadastro
        );

        System.out.println("Merchant registrado: " + merchant.getName());
        
        session.saveCredentials(merchant.getClientId(), merchant.getClientSecret());
    }

    public void autenticar() {
        TokenRequest req = new TokenRequest(session.getClientId(), session.getClientSecret());

        TokenResponse resp = client.post(
            "/fiadopay/auth/token",
            req,
            TokenResponse.class,
            null
        );

        session.setToken(resp.access_token(), resp.expires_in());
        System.out.println("Token expira em " + resp.expires_in() + " segundos");
    }
}