package com.techsphere.fiadopaymercado.infra;

import com.techsphere.fiadopaymercado.service.PaymentProcessor;
import com.techsphere.fiadopaymercado.service.PixProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class PaymentStrategyFactoryTest {

    @Test
    void deveRetornarProcessadorPixCorretamente() {
        String metodo = "PIX";
        BigDecimal valor = new BigDecimal("100.00");

        PaymentProcessor processor = PaymentStrategyFactory.getProcessor(metodo, valor);

        Assertions.assertNotNull(processor, "O processador não deveria ser nulo");
        Assertions.assertInstanceOf(PixProcessor.class, processor, "Deveria retornar uma instância de PixProcessor");
    }

    @Test
    void deveRejeitarPagamentoAcimaDoLimiteAntiFraude() {
        String metodo = "PIX";
        BigDecimal valorAlto = new BigDecimal("501.00");

        Exception exception = Assertions.assertThrows(SecurityException.class, () -> {
            PaymentStrategyFactory.getProcessor(metodo, valorAlto);
        });

        Assertions.assertTrue(exception.getMessage().contains("Pagamento rejeitado"), 
            "A mensagem de erro deve informar rejeição por fraude");
    }

    @Test
    void deveLancarErroParaMetodoDesconhecido() {
        String metodoInvalido = "DINHEIRO_VIVO";
        BigDecimal valor = new BigDecimal("50.00");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PaymentStrategyFactory.getProcessor(metodoInvalido, valor);
        });
    }
}