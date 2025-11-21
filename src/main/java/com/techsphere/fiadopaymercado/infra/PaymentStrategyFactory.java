package com.techsphere.fiadopaymercado.infra;

import com.techsphere.fiadopaymercado.service.PaymentProcessor;
import com.techsphere.mercado.annotations.AntiFraud;
import com.techsphere.mercado.annotations.PaymentStrategy;
import org.reflections.Reflections;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PaymentStrategyFactory {

    private static final Map<String, Class<? extends PaymentProcessor>> strategies = new HashMap<>();

    // bloco estático para carregar as classes via Reflexão ao iniciar
    static {
        try {
            Reflections reflections = new Reflections("com.techsphere");
            
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(PaymentStrategy.class);

            for (Class<?> clazz : annotatedClasses) {
                if (PaymentProcessor.class.isAssignableFrom(clazz)) {
                    PaymentStrategy annotation = clazz.getAnnotation(PaymentStrategy.class);
                    strategies.put(annotation.value().toUpperCase(), (Class<? extends PaymentProcessor>) clazz);
                    System.out.println("[Reflexão] Estratégia encontrada: " + annotation.value() + " -> " + clazz.getSimpleName());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar plugins de pagamento: " + e.getMessage());
        }
    }

    public static PaymentProcessor getProcessor(String method, BigDecimal amount) {
        Class<? extends PaymentProcessor> processorClass = strategies.get(method.toUpperCase());
        
        if (processorClass == null) {
            throw new IllegalArgumentException("Método de pagamento não suportado ou plugin não encontrado: " + method);
        }

        // validação de antifraude com reflexão
        if (processorClass.isAnnotationPresent(AntiFraud.class)) {
            AntiFraud antiFraud = processorClass.getAnnotation(AntiFraud.class);
            if (amount.doubleValue() > antiFraud.maxAmount()) {
                throw new SecurityException("Pagamento rejeitado por regra de AntiFraude (Limite: " + antiFraud.maxAmount() + ")");
            }
        }

        try {
            return processorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao instanciar processador de pagamento", e);
        }
    }
    
    public static Set<String> getAvailableMethods() {
        return strategies.keySet();
    }
}