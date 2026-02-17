package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repositório de itens do pedido (order_items).
 */
public class RepositoryOrderItems {

    private static final String SQL_TOTAL_PRODUCTS =
            "SELECT COALESCE(SUM(quantity), 0) AS total FROM order_items";

    /**
     * Soma total de produtos comprados (quantidade de order_items).
     * @return total de itens comprados
     * @throws SQLException em erro de acesso ao banco
     */
    public long getTotalProductsPurchased() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_TOTAL_PRODUCTS);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0L;
        }
    }
}
