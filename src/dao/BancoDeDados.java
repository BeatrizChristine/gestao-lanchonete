package dao;

import java.sql.*;

public class BancoDeDados {
    // URL de conexão com o banco MySQL local na porta 3306, usando o schema "LanchoneteBD"
    private static final String URL = "jdbc:mysql://localhost:3306/LanchoneteBD";
    private static final String USUARIO = "root";           // Usuário do banco
    private static final String SENHA = "Beatriz12$";       // Senha do banco

    // Conexão compartilhada por todas as classes que estendem BancoDeDados
    protected static Connection conexao = null;

    // Construtor: conecta ao banco se a conexão ainda não existir
    public BancoDeDados() {
        if (conexao == null) conecta();
    }

    // Método que realiza a conexão com o banco de dados
    private static boolean conecta() {
        try {
            // Registra o driver JDBC do MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Estabelece conexão com o banco usando URL, usuário e senha
            conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            return true;    // Conexão bem-sucedida
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erro de conexão: " + e.getMessage());
            return false;   // Retorna false em caso de falha
        }
    }

    // Método para fechar a conexão com o banco de dados
    public static boolean desconecta() {
        try {
            conexao.close();
            return true;    // Desconectou com sucesso
        } catch (SQLException e) {
            return false;   // Falha ao desconectar
        }
    }
}
