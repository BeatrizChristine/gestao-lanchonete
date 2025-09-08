package dao;

import model.Garcom;
import java.sql.*;
import java.util.ArrayList;

public class GarcomDAO extends BancoDeDados {

    // Adiciona um novo garçom na tabela "garcons"
    // Sempre cadastra como ativo (ativo = true)
    public boolean adicionarGarcom(Garcom g) {
        String sql = "INSERT INTO garcons (nome, matricula, ativo) VALUES (?, ?, ?)";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, g.getNome());         // Define o nome do garçom
            stmt.setString(2, g.getMatricula());    // Define a matrícula do garçom
            stmt.setBoolean(3, true);                // Define ativo como true no cadastro
            stmt.executeUpdate();                    // Executa inserção no banco
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar garçom: " + e.getMessage());
            return false;                           // Retorna false se houver erro
        }
    }

    // Retorna uma lista com todos os garçons cadastrados no banco
    public ArrayList<Garcom> listarGarcons() {
        ArrayList<Garcom> lista = new ArrayList<>();
        String sql = "SELECT * FROM garcons";
        try {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Garcom g = new Garcom(rs.getString("nome"), rs.getString("matricula"));
                g.setId(rs.getInt("id"));           // Define o ID do garçom
                g.setAtivo(rs.getBoolean("ativo")); // Define se está ativo
                lista.add(g);                       // Adiciona na lista para retorno
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar garçons: " + e.getMessage());
        }
        return lista;
    }

    // Método comentado: verifica se já existe garçom por nome ou matrícula
    // public boolean existeGarcom(String nome, String matricula) { ... }

    // Busca um garçom pelo seu ID
    public Garcom buscarGarcomPorId(int id) {
        String sql = "SELECT * FROM garcons WHERE id = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);                   // Define o ID para busca
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Garcom garcom = new Garcom(rs.getString("nome"), rs.getString("matricula"));
                garcom.setId(rs.getInt("id"));       // Define ID
                garcom.setAtivo(rs.getBoolean("ativo")); // Define ativo/inativo
                return garcom;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar garçom por ID: " + e.getMessage());
        }
        return null;                               // Retorna null se não encontrar
    }

    // Método comentado: atualiza nome e matrícula do garçom
    // public boolean atualizarGarcom(Garcom g) { ... }

    // Desativa um garçom (ativo = false) pelo ID
    public boolean desativarGarcom(int id) {
        String sql = "UPDATE garcons SET ativo = false WHERE id = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);                  // Define o ID do garçom a desativar
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao desativar garçom: " + e.getMessage());
            return false;
        }
    }

    // Ativa um garçom (ativo = true) pelo ID
    public boolean ativarGarcom(int id) {
        String sql = "UPDATE garcons SET ativo = true WHERE id = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);                  // Define o ID do garçom a ativar
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao ativar garçom: " + e.getMessage());
            return false;
        }
    }

    // Verifica se já existe um garçom com o nome informado
    public boolean nomeJaExiste(String nome) {
        String sql = "SELECT 1 FROM garcons WHERE nome = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, nome);             // Define o nome para busca
            ResultSet rs = stmt.executeQuery();
            return rs.next();                    // Retorna true se existir
        } catch (SQLException e) {
            System.out.println("Erro ao verificar nome de garçom: " + e.getMessage());
            return false;
        }
    }

    // Verifica se já existe um garçom com a matrícula informada
    public boolean matriculaJaExiste(String matricula) {
        String sql = "SELECT 1 FROM garcons WHERE matricula = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, matricula);        // Define matrícula para busca
            ResultSet rs = stmt.executeQuery();
            return rs.next();                    // Retorna true se existir
        } catch (SQLException e) {
            System.out.println("Erro ao verificar matrícula de garçom: " + e.getMessage());
            return false;
        }
    }

}
