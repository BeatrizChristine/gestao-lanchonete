import view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenuFrame extends JFrame {

    // Construtor da janela
    public MainMenuFrame() {
        setTitle("Sistema de Lanchonete - Menu Principal");
        setSize(500, 600); // Define tamanho da janela
        setLocationRelativeTo(null); // Centraliza a janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Encerra o app ao fechar a janela

        // Painel que organiza todos os botões e o título
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS)); // Layout vertical
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 50, 10, 50)); // Margens
        painelPrincipal.setBackground(new Color(245, 245, 245)); // Cor de fundo clara

        // Título do menu
        JLabel titulo = new JLabel("Menu Principal");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Espaçamento abaixo do título
        painelPrincipal.add(titulo);

        // Adiciona os botões de navegação
        painelPrincipal.add(criarBotao("Cadastrar Garçom", e -> new CadastroGarcomFrame()));
        painelPrincipal.add(criarBotao("Cadastrar Mesa", e -> new CadastroMesaFrame()));
        painelPrincipal.add(criarBotao("Cadastrar Produto", e -> new CadastroProdutoFrame()));
        painelPrincipal.add(criarBotao("Realizar Pedido", e -> new RealizarPedidoFrame()));
        painelPrincipal.add(criarBotao("Acompanhar Pedidos", e -> new AcompanhamentoPedidosFrame()));
        painelPrincipal.add(criarBotao("Fechamento de Conta", e -> new FechamentoContaFrame()));
        painelPrincipal.add(criarBotao("Histórico de Pedidos Pagos", e -> new HistoricoPagosFrame()));

        painelPrincipal.add(Box.createVerticalStrut(5)); // Espaço antes do botão sair

        // Botão para sair do sistema
        painelPrincipal.add(criarBotao("Sair", e -> System.exit(0)));

        // Adiciona tudo na janela
        add(painelPrincipal);
        setVisible(true); // Exibe a janela
    }

    // Método auxiliar para criar botões com estilo e ação
    private JButton criarBotao(String texto, ActionListener action) {
        JButton botao = new JButton(texto);
        botao.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza
        botao.setMaximumSize(new Dimension(250, 50)); // Define tamanho máximo
        botao.setFont(new Font("SansSerif", Font.PLAIN, 16));
        botao.addActionListener(action); // Define a ação quando o botão é clicado
        botao.setFocusPainted(false); // Remove o contorno ao focar

        // Define cores diferentes para o botão "Sair"
        if (texto.equalsIgnoreCase("Sair")) {
            botao.setBackground(new Color(220, 53, 69)); // vermelho (estilo Bootstrap)
        } else {
            botao.setBackground(new Color(100, 149, 237)); // azul padrão
        }

        botao.setForeground(Color.WHITE); // Cor da fonte
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Muda o cursor ao passar o mouse
        return botao;
    }

    // Método principal: inicializa o programa exibindo o menu
    public static void main(String[] args) {
        new MainMenuFrame();
    }
}
