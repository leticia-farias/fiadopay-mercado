package com.techsphere.fiadopaymercado.infra;

import com.techsphere.fiadopaymercado.domain.PaymentResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {

    private static PaymentRepository instance;

    private PaymentRepository() {
        DatabaseManager.initialize();
    }

    public static synchronized PaymentRepository getInstance() {
        if (instance == null) instance = new PaymentRepository();
        return instance;
    }

    public void save(PaymentResponse payment) {
        String updateSQL = "UPDATE payments SET status = ?, amount = ?, method = ? WHERE id = ?";
        String insertSQL = "INSERT INTO payments (id, status, amount, method) VALUES (?, ?, ?, ?)";

        try (Connection conc = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conc.prepareStatement(updateSQL)) {
                stmt.setString(1, payment.status());
                stmt.setBigDecimal(2, payment.amount());
                stmt.setString(3, payment.method());
                stmt.setString(4, payment.id());
                
                int linhas = stmt.executeUpdate();
                
                if (linhas == 0) {
                    try (PreparedStatement insertStmt = conc.prepareStatement(insertSQL)) {
                        insertStmt.setString(1, payment.id());
                        insertStmt.setString(2, payment.status());
                        insertStmt.setBigDecimal(3, payment.amount());
                        insertStmt.setString(4, payment.method());
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar pagamento: " + e.getMessage());
        }
    }

    public List<PaymentResponse> findPendingPayments() {
        List<PaymentResponse> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE status = 'PENDING'";

        try (Connection conc = DatabaseManager.getConnection();
             Statement stmt = conc.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PaymentResponse p = new PaymentResponse(
                    rs.getString("id"),
                    rs.getString("status"),
                    rs.getString("method"),
                    rs.getBigDecimal("amount"),
                    null, // installments
                    null, // total
                    null  // interestRate
                );
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pendentes: " + e.getMessage());
        }
        return list;
    }
}