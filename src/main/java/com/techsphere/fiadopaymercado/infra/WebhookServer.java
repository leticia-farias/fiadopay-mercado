package com.techsphere.fiadopaymercado.infra;

//import br.edu.cliente.domain.PaymentResponse;
import com.techsphere.fiadopaymercado.domain.PaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer; // Classe padrão do Java (JDK 8+)

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
public class WebhookServer {

	 private static final int PORT = 8081;
	    // fila thread-safe (O servidor põe, o worker tira)
	    private final BlockingQueue<String> eventQueue = new LinkedBlockingQueue<>();
	    private final ObjectMapper mapper = new ObjectMapper();
	    private final PaymentRepository repository = PaymentRepository.getInstance();

	    public void start() throws IOException {
	        // cria servidor HTTP leve
	        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
	        
	        // define o endpoint que o fiadoPay vai chamar
	        server.createContext("/webhook", exchange -> {
	            if ("POST".equals(exchange.getRequestMethod())) {
	                // lê o corpo da requisição (json)
	                String jsonBody = new String(exchange.getRequestBody().readAllBytes());
	                
	                //  coloca na fila para processamento futuro (producer)
	                eventQueue.offer(jsonBody);
	                System.out.println("[Webhook-Server] Evento recebido e enfileirado. Tamanho da fila: " + eventQueue.size());

	                // responde 200 OK rápido para o fiadoPay não dar timeout
	                String response = "OK";
	                exchange.sendResponseHeaders(200, response.length());
	                OutputStream os = exchange.getResponseBody();
	                os.write(response.getBytes());
	                os.close();
	            } else {
	                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
	            }
	        });
	        
	        server.setExecutor(null); // Default executor
	        server.start();
	        System.out.println(">>> Servidor de Webhook ouvindo na porta " + PORT);

	        // inicia a thread consumidora (worker)
	        iniciarWorker();
	    }
	    private void iniciarWorker() {
	        new Thread(() -> {
	            System.out.println(">>> Worker de Webhook iniciado.");
	            while (true) {
	                try {
	                    //  fica bloqueado aqui até chegar algo na fila (consumer)
	                    String json = eventQueue.take(); 
	                    
	                    // simula processamento pesado (opcional)
	                    // Thread.sleep(1000); 

	                    // converte json para objeto e atualiza banco
	                    PaymentResponse pagamento = mapper.readValue(json, PaymentResponse.class);
	                    repository.save(pagamento);
	                    
	                    System.out.println("[Webhook-Worker] Pagamento " + pagamento.getId() + " atualizado via Webhook para: " + pagamento.getStatus());

	                } catch (Exception e) {
	                    System.err.println("[Webhook-Worker] Erro ao processar evento: " + e.getMessage());
	                }
	            }
	        }).start();
	        
	    }
}
