package com.techsphere.mercado.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Anotação para descobrir Plugins de Pagamento
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PaymentMethodPlugin {
    String methodCode(); // ex: "PIX", "CARD"
}