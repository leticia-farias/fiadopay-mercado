package com.techsphere.fiadopaymercado.domain;

import java.math.BigDecimal;

public class PaymentResponse {

    private String id;
    private String status;
    private String method;
    private BigDecimal amount;
    private Integer installments;
    private BigDecimal total;

    public PaymentResponse(String id, String status, String method, BigDecimal amount, Integer installments, BigDecimal total) {
        this.id = id;
        this.status = status;
        this.method = method;
        this.amount = amount;
        this.installments = installments;
        this.total = total;
    }

    public PaymentResponse() {
	}

	public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getMethod() {
        return method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}