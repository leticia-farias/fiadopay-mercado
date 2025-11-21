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
			System.out.println("\n========================================");
			System.out.println("--- FIADOPAY MERCADO (CONSOLE) ---");
			System.out.println("1. Novo Pagamento");
			System.out.println("2. Solicitar Devolução (Refund)");
			System.out.println("3. Sair");
			System.out.print("Escolha: ");

			String opcao = scanner.nextLine().trim();

			if ("1".equals(opcao)) {
				System.out.println("\n[Novo Pagamento]");
				System.out.println("Métodos disponíveis: " + PaymentStrategyFactory.getAvailableMethods());

				// LOOP DE VALIDAÇÃO: Garante que não pule se o usuário der Enter sem querer
				String method = "";
				while (method.isEmpty()) {
					System.out.print("Digite o Método (ex: PIX, CARD): ");
					method = scanner.nextLine().trim();
				}

				System.out.print("Valor (ex: 100.50): ");
				try {
					String valorStr = scanner.nextLine().trim();
					if (valorStr.isEmpty())
						continue; // Volta pro menu se vazio

					BigDecimal amount = new BigDecimal(valorStr);

					Integer installments = 1;
					if ("CARD".equalsIgnoreCase(method)) {
						System.out.print("Número de Parcelas (1-12): ");
						String parcStr = scanner.nextLine().trim();
						installments = parcStr.isEmpty() ? 1 : Integer.parseInt(parcStr);
					}

					service.processarPagamento(method, amount, installments);
				} catch (NumberFormatException e) {
					System.out.println(">>> Erro: Valor inválido! Use ponto para decimais (ex: 50.00)");
				}

			} else if ("2".equals(opcao)) {
                System.out.println("\n[Solicitar Devolução]");
                System.out.print("ID do Pagamento (copie do banco ou console): ");
                String rawId = scanner.nextLine().trim();
                
                String id = rawId.split(" ")[0];
                
                if (!id.isEmpty()) {
                    service.solicitarEstorno(id);
                } else {
                    System.out.println(">>> ID não informado.");
                }
                
            } else if ("3".equals(opcao)) {
				System.out.println("Saindo...");
				break;
			} else {
				// Se não for 1, 2 ou 3, apenas ignora (evita erro com enter vazio)
				if (!opcao.isEmpty()) {
					System.out.println("Opção inválida.");
				}
			}
		}
		scanner.close();
	}
}
