package view;

import dao.ProdutoDAO;
import model.Produto;

import javax.swing.*;
import java.awt.*;

public class CadastroProdutoFrame extends JFrame {
    private JTextField campoNome;
    private JTextField campoPreco;
    private JButton botaoSalvar;

    ImageIcon icon = new ImageIcon("src/icons/check.png"); // Ícone para feedback visual no JOptionPane

    public CadastroProdutoFrame() {
        setTitle("Cadastro de Produto");
        setSize(420, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(new Color(245, 245, 245));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Cadastrar Novo Produto");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelPrincipal.add(titulo);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel linhaNome = new JPanel(new BorderLayout(10, 10));
        linhaNome.setBackground(new Color(245, 245, 245));
        JLabel labelNome = new JLabel("Nome:");
        labelNome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campoNome = new JTextField();
        campoNome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        linhaNome.add(labelNome, BorderLayout.WEST);
        linhaNome.add(campoNome, BorderLayout.CENTER);

        JPanel linhaPreco = new JPanel(new BorderLayout(10, 10));
        linhaPreco.setBackground(new Color(245, 245, 245));
        JLabel labelPreco = new JLabel("Preço:");
        labelPreco.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campoPreco = new JTextField();
        campoPreco.setFont(new Font("SansSerif", Font.PLAIN, 14));
        linhaPreco.add(labelPreco, BorderLayout.WEST);
        linhaPreco.add(campoPreco, BorderLayout.CENTER);

        botaoSalvar = new JButton("Salvar");
        botaoSalvar.setPreferredSize(new Dimension(100, 32));
        botaoSalvar.setFont(new Font("SansSerif", Font.BOLD, 13));
        botaoSalvar.setBackground(new Color(76, 175, 80));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setFocusPainted(false);
        botaoSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoSalvar.addActionListener(e -> salvarProduto());

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBackground(new Color(245, 245, 245));
        painelBotao.add(botaoSalvar);

        painelPrincipal.add(linhaNome);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        painelPrincipal.add(linhaPreco);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        painelPrincipal.add(painelBotao);

        setContentPane(painelPrincipal);
        setVisible(true);
    }

    private void salvarProduto() {
        String nome = campoNome.getText().trim();
        String precoTexto = campoPreco.getText().trim();

        if (nome.isEmpty() || precoTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double preco = Double.parseDouble(precoTexto);

            if (preco <= 0) {
                JOptionPane.showMessageDialog(this, "O preço deve ser um valor positivo.", "Valor Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ProdutoDAO produtoDAO = new ProdutoDAO();

            // Verifica se já existe um produto com o mesmo nome para evitar duplicidade
            if (produtoDAO.existeProdutoPorNome(nome)) {
                JOptionPane.showMessageDialog(this, "Já existe um produto com esse nome.", "Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Produto produto = new Produto(nome, preco);

            // Insere o produto no banco de dados
            if (produtoDAO.adicionarProduto(produto)) {
                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!", "Sucesso", JOptionPane.PLAIN_MESSAGE, icon);
                campoNome.setText("");
                campoPreco.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar produto.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            // Captura erro caso o usuário digite letras no campo de preço
            JOptionPane.showMessageDialog(this, "Preço inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new CadastroProdutoFrame();
    }
}
