package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;

public class PedidoDAO extends BancoDeDados {

    // Método para adicionar um novo pedido no banco, incluindo os produtos do pedido
    public boolean adicionarPedido(Pedido pedido) {
        String sqlPedido = "INSERT INTO pedidos (mesa_id, garcom_id, status, data_hora) VALUES (?, ?, ?, ?)";
        String sqlProdutos = "INSERT INTO pedido_produtos (pedido_id, produto_id) VALUES (?, ?)";

        try {
            conexao.setAutoCommit(false);  // Desabilita autocommit para começar transação

            // Prepara e executa inserção do pedido na tabela 'pedidos'
            PreparedStatement stmtPedido = conexao.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            stmtPedido.setInt(1, pedido.getMesa().getId());
            stmtPedido.setInt(2, pedido.getGarcom().getId());
            stmtPedido.setString(3, pedido.getStatus());
            stmtPedido.setTimestamp(4, Timestamp.valueOf(pedido.getDataHora()));
            stmtPedido.executeUpdate();

            // Obtém o ID gerado do pedido para usar na tabela intermediária
            ResultSet rs = stmtPedido.getGeneratedKeys();
            int idPedido = 0;
            if (rs.next()) {
                idPedido = rs.getInt(1);
                pedido.setId(idPedido);
            }

            // Prepara inserção em lote dos produtos relacionados ao pedido
            PreparedStatement stmtProdutos = conexao.prepareStatement(sqlProdutos);
            for (Produto p : pedido.getProdutos()) {
                stmtProdutos.setInt(1, idPedido);
                stmtProdutos.setInt(2, p.getId());
                stmtProdutos.addBatch();
            }
            stmtProdutos.executeBatch(); // Executa todas as inserções de produtos

            conexao.commit(); // Confirma transação
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao adicionar pedido: " + e.getMessage());
            try {
                conexao.rollback(); // Reverte alterações se der erro
            } catch (SQLException ex) {
                System.out.println("Erro ao desfazer transação: " + ex.getMessage());
            }
            return false;

        } finally {
            try {
                conexao.setAutoCommit(true); // Restaura autocommit para o padrão
            } catch (SQLException e) {
                System.out.println("Erro ao restaurar autoCommit: " + e.getMessage());
            }
        }
    }

    // Método para adicionar produtos a um pedido já existente
    public boolean adicionarProdutosAoPedido(int idPedido, ArrayList<Produto> produtos) {
        String sql = "INSERT INTO pedido_produtos (pedido_id, produto_id) VALUES (?, ?)";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            for (Produto p : produtos) {
                stmt.setInt(1, idPedido);
                stmt.setInt(2, p.getId());
                stmt.addBatch(); // Adiciona cada inserção ao batch
            }
            stmt.executeBatch(); // Executa as inserções em lote
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar produtos ao pedido existente: " + e.getMessage());
            return false;
        }
    }

    // Busca pedido em aberto (status 'Em preparo') para a mesa indicada
    public Pedido buscarPedidoAbertoPorMesa(int idMesa) {
        String sql = "SELECT * FROM pedidos WHERE mesa_id = ? AND status = 'Em preparo'";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idMesa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Cria objeto Pedido preenchendo mesa e garçom com DAOs correspondentes
                Pedido pedido = new Pedido(
                        new MesaDAO().buscarMesaPorId(idMesa),
                        new GarcomDAO().buscarGarcomPorId(rs.getInt("garcom_id")),
                        new ArrayList<>(), // lista vazia de produtos neste momento
                        rs.getString("status")
                );
                pedido.setId(rs.getInt("id"));
                pedido.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                return pedido;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pedido em aberto: " + e.getMessage());
        }
        return null; // Retorna null se não encontrar pedido aberto
    }

    // Lista todos os pedidos com dados completos: mesa, garçom, produtos, status e data
    public ArrayList<Pedido> listarPedidosCompletos() {
        ArrayList<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.id AS pedido_id, p.status, p.data_hora, " +
                "m.id AS mesa_id, m.numero, m.ocupada, " +
                "g.id AS garcom_id, g.nome, g.matricula " +
                "FROM pedidos p " +
                "JOIN mesas m ON p.mesa_id = m.id " +
                "JOIN garcons g ON p.garcom_id = g.id";

        try {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Monta mesa com dados do resultado
                Mesa mesa = new Mesa(rs.getInt("numero"), rs.getBoolean("ocupada"));
                mesa.setId(rs.getInt("mesa_id"));

                // Monta garçom com dados do resultado
                Garcom garcom = new Garcom(rs.getString("nome"), rs.getString("matricula"));
                garcom.setId(rs.getInt("garcom_id"));

                // Busca os produtos do pedido via método privado
                ArrayList<Produto> produtos = buscarProdutosDoPedido(rs.getInt("pedido_id"));

                // Cria o pedido completo
                Pedido pedido = new Pedido(mesa, garcom, produtos, rs.getString("status"));
                pedido.setId(rs.getInt("pedido_id"));
                pedido.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());

                lista.add(pedido);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar pedidos: " + e.getMessage());
        }
        return lista;
    }

    // Método privado para buscar os produtos associados a um pedido
    private ArrayList<Produto> buscarProdutosDoPedido(int pedidoId) {
        ArrayList<Produto> produtos = new ArrayList<>();
        String sql = "SELECT pr.id, pr.nome, pr.preco " +
                "FROM produtos pr " +
                "JOIN pedido_produtos pp ON pr.id = pp.produto_id " +
                "WHERE pp.pedido_id = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Produto produto = new Produto(rs.getString("nome"), rs.getDouble("preco"));
                produto.setId(rs.getInt("id"));
                produtos.add(produto);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produtos do pedido: " + e.getMessage());
        }
        return produtos;
    }

    // Atualiza o status de um pedido
    public boolean atualizarStatusPedido(int idPedido, String novoStatus) {
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, novoStatus);
            stmt.setInt(2, idPedido);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se alguma linha foi atualizada
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status do pedido: " + e.getMessage());
            return false;
        }
    }

}
