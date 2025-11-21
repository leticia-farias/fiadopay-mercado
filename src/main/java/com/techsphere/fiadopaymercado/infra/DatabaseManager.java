package com.techsphere.fiadopaymercado.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    
    private static final String URL = "jdbc:h2:./fiadopay_client_db"; 
    private static final String USER = "sa";
    private static final String PASSWORD = ""; 

    // BLOCO NOVO: Força o carregamento do Driver H2
    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("ERRO FATAL: Driver H2 não encontrado no classpath! Verifique o Maven.");
            e.printStackTrace();
        }
    }

    public static void initialize() {
        String sql = "CREATE TABLE IF NOT EXISTS payments (" +
                     "id VARCHAR(255) PRIMARY KEY, " +
                     "status VARCHAR(50), " +
                     "amount DECIMAL(10,2), " +
                     "method VARCHAR(50)" +
                     ")";
        
        try (Connection conc = getConnection();
             Statement stmt = conc.createStatement()) {
            stmt.execute(sql);
            System.out.println("Banco de dados inicializado/verificado.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}