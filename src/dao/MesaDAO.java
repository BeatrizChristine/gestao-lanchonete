package dao;

import model.Mesa;
import java.sql.*;
import java.util.ArrayList;

public class MesaDAO extends BancoDeDados {

    // Adiciona uma nova mesa no banco de dados
    public boolean adicionarMesa(Mesa m) {
        String sql = "INSERT INTO mesas (numero, ocupada) VALUES (?, ?)";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, m.getNumero());      // Define o número da mesa
            stmt.setBoolean(2, m.isOcupada()); // Define se a mesa está ocupada
            stmt.executeUpdate();               // Executa o comando de inserção
            return true;                       // Retorna true se sucesso
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar mesa: " + e.getMessage());
            return false;                      // Retorna false em caso de erro
        }
    }

    // Lista todas as mesas cadastradas no banco
    public ArrayList<Mesa> listarMesas() {
        ArrayList<Mesa> lista = new ArrayList<>();
        String sql = "SELECT * FROM mesas";
        try {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                // Cria objeto Mesa com dados retornados da consulta
                Mesa m = new Mesa(rs.getInt("numero"), rs.getBoolean("ocupada"));
                m.setId(rs.getInt("id")); // Define o ID da mesa
                lista.add(m);             // Adiciona à lista que será retornada
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar mesas: " + e.getMessage());
        }
        return lista;
    }

    // Verifica se já existe uma mesa com determinado número
    public boolean existeMesaPorNumero(int numero) {
        String sql = "SELECT * FROM mesas WHERE numero = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, numero);   // Define o número para buscar
            ResultSet rs = stmt.executeQuery();
            return rs.next();         // Retorna true se encontrou algum registro
        } catch (SQLException e) {
            System.out.println("Erro ao verificar número da mesa: " + e.getMessage());
            return false;             // Em erro, retorna false (não existe ou falha)
        }
    }

    // Busca uma mesa pelo seu ID
    public Mesa buscarMesaPorId(int id) {
        String sql = "SELECT * FROM mesas WHERE id = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);       // Define o ID da mesa para busca
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Cria e retorna o objeto Mesa com os dados encontrados
                Mesa mesa = new Mesa(rs.getInt("numero"), rs.getBoolean("ocupada"));
                mesa.setId(rs.getInt("id"));
                return mesa;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar mesa por ID: " + e.getMessage());
        }
        return null;                   // Retorna null se não encontrar ou erro
    }

}
