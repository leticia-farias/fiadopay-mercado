package com.techsphere.fiadopaymercado.domain;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PaymentRequest(
	    @NotBlank @Pattern(regexp = "(?i)CARD|PIX|DEBIT|BOLETO") String method, // preencher com plugin
	    @NotBlank String currency,
	    @NotNull @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount,
	    @Min(1) @Max(12) Integer installments,
	    @Size(max = 255) String metadataOrderId
	) {}