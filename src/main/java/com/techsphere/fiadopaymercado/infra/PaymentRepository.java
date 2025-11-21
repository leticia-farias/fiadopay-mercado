package com.techsphere.fiadopaymercado.infra;

import com.techsphere.fiadopaymercado.domain.PaymentResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentRepository {
    private static PaymentRepository instance;

    private final Map<String, PaymentResponse> database = new ConcurrentHashMap<>();

    private PaymentRepository() {}

    public static synchronized PaymentRepository getInstance() {
        if (instance == null) instance = new PaymentRepository();
        return instance;
    }
    
    public void save(PaymentResponse payment) {
        database.put(payment.getId(), payment);
        // log simples para ver acontecendo
    }
    
    public PaymentResponse findById(String id) {
        return database.get(id);
    }
    
 // retorna a   lista de pagamentos pendentes pra a thread de reconciliação usar
    public List<PaymentResponse> findPendingPayments() {
        List<PaymentResponse> pending = new ArrayList<>();
        for (PaymentResponse p : database.values()) {
            if ("PENDING".equalsIgnoreCase(p.getStatus())) {
                pending.add(p);
            }
        }
        return pending;
    }

}
