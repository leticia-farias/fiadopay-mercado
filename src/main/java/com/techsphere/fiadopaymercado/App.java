package com.techsphere.fiadopaymercado;

import com.techsphere.fiadopaymercado.infra.FiadoPayClient;
import com.techsphere.fiadopaymercado.infra.PaymentStrategyFactory;
import com.techsphere.fiadopaymercado.infra.WebhookServer;
import com.techsphere.fiadopaymercado.service.AuthService;
import com.techsphere.fiadopaymercado.service.JobScheduler;
import com.techsphere.fiadopaymercado.service.PaymentService;

import java.math.BigDecimal;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        FiadoPayClient client = new FiadoPayClient();
        AuthService authService = new AuthService(client);
        JobScheduler scheduler = new JobScheduler(authService, client);
        WebhookServer webhookServer = new WebhookServer();
        PaymentService paymentService = new PaymentService(client);
        
        try {
            System.out.println("=== INICIALIZANDO SISTEMA ===");
            
            // 1. Inicializa Servidor de Webhook (Thread separada)
            webhookServer.start();
            
            // 2. Autenticação (Merchant)
            authService.inicializar();
            
            // 3. Inicia Jobs de Background (Refresh Token e Reconciliação)
            scheduler.iniciarJobs();
            
            // 4. Menu Interativo
            exibirMenu(paymentService);
            
        } catch (Exception e) {
            System.err.println("Falha fatal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scheduler.parar();
            System.out.println("Sistema encerrado.");
            System.exit(0);
        }
    }

    private static void exibirMenu(PaymentService service) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- FIADOPAY MERCADO (CONSOLE) ---");
            System.out.println("1. Novo Pagamento");
            System.out.println("2. Solicitar Devolução (Refund)");
            System.out.println("3. Sair");
            System.out.print("Escolha: ");
            
            String opcao = scanner.nextLine();
            
            if ("1".equals(opcao)) {
                System.out.println("Métodos disponíveis: " + PaymentStrategyFactory.getAvailableMethods());
                System.out.print("Método (ex: PIX, CARD): ");
                String method = scanner.nextLine();
                
                System.out.print("Valor (ex: 100.50): ");
                try {
                    BigDecimal amount = new BigDecimal(scanner.nextLine());
                    
                    Integer installments = 1;
                    if ("CARD".equalsIgnoreCase(method)) {
                        System.out.print("Parcelas (1-12): ");
                        installments = Integer.parseInt(scanner.nextLine());
                    }
                    
                    service.processarPagamento(method, amount, installments);
                } catch (NumberFormatException e) {
                    System.out.println("Valor inválido!");
                }
                
            } else if ("2".equals(opcao)) {
                System.out.print("ID do Pagamento para estorno: ");
                String id = scanner.nextLine();
                service.solicitarEstorno(id);
                
            } else if ("3".equals(opcao)) {
                break;
            } else {
                System.out.println("Opção inválida.");
            }
        }
        scanner.close();
    }
}
