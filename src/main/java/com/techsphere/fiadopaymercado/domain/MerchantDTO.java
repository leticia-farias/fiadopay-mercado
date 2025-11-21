package com.techsphere.fiadopaymercado.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MerchantDTO(
	    @NotBlank @Size(max = 120) String name,
	    @NotBlank String webhookUrl
	) {}
