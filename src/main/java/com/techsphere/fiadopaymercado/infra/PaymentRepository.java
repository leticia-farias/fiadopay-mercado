package com.techsphere.fiadopaymercado.infra;

import com.techsphere.fiadopaymercado.domain.PaymentResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PaymentRepository {

    private static PaymentRepository instance;

    private PaymentRepository() {
        //garantir que a tabela existe ao iniciar o repositório
        DatabaseManager.initialize();
    }

    public static synchronized PaymentRepository getInstance() {
        if (instance == null) instance = new PaymentRepository();
        return instance;
    }

    public void save(PaymentResponse payment) {
        //tenta update, se não afetar linhas, faz insert (upsert manual)
        String updateSQL = "UPDATE payments SET status = ?, amount = ?, method = ? WHERE id = ?";
        String insertSQL = "INSERT INTO payments (id, status, amount, method) VALUES (?, ?, ?, ?)";

        try (Connection conc = DatabaseManager.getConnection()) {
            // vai tentar atualizar
            try (PreparedStatement stmt = conc.prepareStatement(updateSQL)) {
                stmt.setString(1, payment.getStatus());
                stmt.setBigDecimal(2, payment.getAmount());
                stmt.setString(3, payment.getMethod());
                stmt.setString(4, payment.getId());
                
                int linhas = stmt.executeUpdate();
                
                if (linhas == 0) {
                    // se não atualizou nada, é porque não existe -> INSERIR
                    try (PreparedStatement insertStmt = conc.prepareStatement(insertSQL)) {
                        insertStmt.setString(1, payment.getId());
                        insertStmt.setString(2, payment.getStatus());
                        insertStmt.setBigDecimal(3, payment.getAmount());
                        insertStmt.setString(4, payment.getMethod());
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
                PaymentResponse p = new PaymentResponse();
                p.setId(rs.getString("id"));
                p.setStatus(rs.getString("status"));
                p.setAmount(rs.getBigDecimal("amount"));
                p.setMethod(rs.getString("method"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pendentes: " + e.getMessage());
        }
        return list;
    }
}