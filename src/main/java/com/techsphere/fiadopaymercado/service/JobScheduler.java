package com.techsphere.fiadopaymercado.service;

import com.techsphere.fiadopaymercado.domain.PaymentResponse;
import com.techsphere.fiadopaymercado.infra.FiadoPayClient;
import com.techsphere.fiadopaymercado.infra.PaymentRepository;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final AuthService authService;
    private final FiadoPayClient client;
    private final PaymentRepository repository;

    public JobScheduler(AuthService authService, FiadoPayClient client) {
        this.authService = authService;
        this.client = client;
        this.repository = PaymentRepository.getInstance();
    }

    public void iniciarJobs() {
        System.out.println(">>> Jobs de Background Iniciados");

        // job 1: Refresh Token (roda a cada 30 minutos)
        //verifica se o token está perto de expirar e renova
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Verificando validade do token...");
                SessionManager session = SessionManager.getInstance();
                // se faltar menos de 5 minutos para expirar, ou se já expirou, renova.
                //renova sempre para garantir na demo
                authService.autenticar(); 
            } catch (Exception e) {
                System.err.println("Falha ao renovar token: " + e.getMessage());
            }
        }, 10, 1800, TimeUnit.SECONDS); // delay inicial 10s, repete a cada 1800s

        // job 2: reconciliação (roda a cada 1 minuto)
        // busca pagamentos PENDING no banco e pergunta ao FiadoPay o status real
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<PaymentResponse> pendentes = repository.findPendingPayments();
                if (pendentes.isEmpty()) return;

                System.out.println("Processando " + pendentes.size() + " pagamentos pendentes...");
                
                String token = SessionManager.getInstance().getAccessToken();
                
                for (PaymentResponse p : pendentes) {
                    // GET /gateway/payments/{id}
                    PaymentResponse atualizado = client.get(
                        "/fiadopay/gateway/payments/" + p.id(), 
                        PaymentResponse.class, 
                        token
                    );
                    
                    // Se mudou de status, atualiza no banco
                    if (!atualizado.status().equals(p.status())) {
                        System.out.println("Status alterado! " + p.id() + ": " + p.status() + " -> " + atualizado.status());
                        repository.save(atualizado);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro na reconciliação: " + e.getMessage());
            }
        }, 30, 60, TimeUnit.SECONDS); // delay inicial de 30s, e repete a cada 60s
    }
    
    public void parar() {
        scheduler.shutdown(); //objeto que pede para o scheduler parar de executar novas tarefas
    }
}