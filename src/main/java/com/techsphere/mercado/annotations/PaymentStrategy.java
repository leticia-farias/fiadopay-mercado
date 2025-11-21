package com.techsphere.mercado.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Importante: precisa estar disponível enquanto roda
@Target(ElementType.TYPE) // Só pode ser usada em Classes
public @interface PaymentStrategy {
    String value(); // Ex: "PIX", "CARD", "BOLETO"
}