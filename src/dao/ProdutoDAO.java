package dao;

import model.Produto;
import java.sql.*;
import java.util.ArrayList;

//Data Access Object (Objeto de Acesso a Dados)

public class ProdutoDAO extends BancoDeDados {

    // Método para adicionar um novo produto no banco
    public boolean adicionarProduto(Produto p) {
        // Comando SQL para inserir nome e preço no banco
        String sql = "INSERT INTO produtos (nome, preco) VALUES (?, ?)";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, p.getNome());      // Define o parâmetro nome
            stmt.setDouble(2, p.getPreco());     // Define o parâmetro preço
            stmt.executeUpdate();                 // Executa o comando
            return true;                         // Sucesso
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar produto: " + e.getMessage());
            return false;                        // Falha
        }
    }

    // Método que retorna a lista de todos os produtos cadastrados
    public ArrayList<Produto> listarProdutos() {
        ArrayList<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos";
        try {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery(sql); // Executa consulta
            while (rs.next()) {
                // Cria objeto Produto com dados do resultado
                Produto p = new Produto(rs.getString("nome"), rs.getDouble("preco"));
                p.setId(rs.getInt("id"));          // Seta o id do produto
                lista.add(p);                      // Adiciona à lista
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }
        return lista;                             // Retorna lista (vazia se erro)
    }

    // Método para verificar se já existe produto com determinado nome
    public boolean existeProdutoPorNome(String nome) {
        String sql = "SELECT * FROM produtos WHERE nome = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, nome);              // Define parâmetro nome
            ResultSet rs = stmt.executeQuery();
            return rs.next();                     // Retorna true se encontrou registro
        } catch (SQLException e) {
            System.out.println("Erro ao verificar produto existente: " + e.getMessage());
            return false;                        // Em caso de erro, assume que não existe
        }
    }

}
