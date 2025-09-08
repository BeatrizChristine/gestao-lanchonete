package view;

import dao.*;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// Janela para realizar pedidos no sistema
public class RealizarPedidoFrame extends JFrame {

    // Componentes da interface gráfica
    private JComboBox<Mesa> comboMesa;
    private JComboBox<Garcom> comboGarcom;
    private JPanel painelProdutos;
    private JButton botaoFazerPedido;
    private ArrayList<JCheckBox> checkboxesProdutos;
    ImageIcon icon = new ImageIcon("src/icons/check.png"); // Ícone para mensagem de sucesso

    // Construtor da janela
    public RealizarPedidoFrame() {
        setTitle("Realizar Pedido");
        setSize(550, 500);
        setLocationRelativeTo(null); // Centraliza a janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha só essa janela

        // Painel principal da interface
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Painel para seleção da mesa
        JPanel painelMesa = new JPanel();
        painelMesa.setLayout(new BoxLayout(painelMesa, BoxLayout.Y_AXIS));
        painelMesa.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelMesa = new JLabel("Mesa:");
        comboMesa = new JComboBox<>();
        comboMesa.setMaximumSize(new Dimension(300, 30));

        // Adiciona apenas mesas disponíveis no combo
        for (Mesa m : new MesaDAO().listarMesas()) {
            if (!m.isOcupada()) comboMesa.addItem(m);
        }

        painelMesa.add(labelMesa);
        painelMesa.add(Box.createVerticalStrut(5));
        painelMesa.add(comboMesa);

        // Painel para seleção do garçom
        JPanel painelGarcom = new JPanel();
        painelGarcom.setLayout(new BoxLayout(painelGarcom, BoxLayout.Y_AXIS));
        painelGarcom.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelGarcom = new JLabel("Garçom:");
        comboGarcom = new JComboBox<>();
        comboGarcom.setMaximumSize(new Dimension(300, 30));

        // Adiciona apenas garçons ativos no combo
        for (Garcom g : new GarcomDAO().listarGarcons()) {
            if (g.isAtivo()) {
                comboGarcom.addItem(g);
            }
        }

        painelGarcom.add(labelGarcom);
        painelGarcom.add(Box.createVerticalStrut(5));
        painelGarcom.add(comboGarcom);

        // Painel com lista de produtos disponíveis (checkboxes)
        painelProdutos = new JPanel();
        painelProdutos.setLayout(new BoxLayout(painelProdutos, BoxLayout.Y_AXIS));
        painelProdutos.setBorder(BorderFactory.createTitledBorder("Produtos Disponíveis"));
        checkboxesProdutos = new ArrayList<>();

        for (Produto p : new ProdutoDAO().listarProdutos()) {
            JCheckBox check = new JCheckBox(p.toString());
            check.putClientProperty("produto", p); // Associa o produto ao checkbox
            checkboxesProdutos.add(check);
            painelProdutos.add(check);
        }

        // Botão para finalizar o pedido
        botaoFazerPedido = new JButton("Fazer Pedido");
        botaoFazerPedido.addActionListener(e -> fazerPedido()); // Chama o método fazerPedido()

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.add(botaoFazerPedido);

        // Montagem final do painel principal
        painelPrincipal.add(painelMesa);
        painelPrincipal.add(Box.createVerticalStrut(10));
        painelPrincipal.add(painelGarcom);
        painelPrincipal.add(Box.createVerticalStrut(10));
        painelPrincipal.add(painelProdutos);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        painelPrincipal.add(painelBotao);

        setContentPane(painelPrincipal);
        setVisible(true); // Exibe a janela
    }

    // Lógica ao clicar em "Fazer Pedido"
    private void fazerPedido() {
        Mesa mesa = (Mesa) comboMesa.getSelectedItem();
        Garcom garcom = (Garcom) comboGarcom.getSelectedItem();
        ArrayList<Produto> produtosSelecionados = new ArrayList<>();

        // Pega todos os produtos marcados
        for (JCheckBox check : checkboxesProdutos) {
            if (check.isSelected()) {
                Produto p = (Produto) check.getClientProperty("produto");
                produtosSelecionados.add(p);
            }
        }

        // Verifica se todos os campos foram preenchidos
        if (mesa == null || garcom == null || produtosSelecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione todos os campos e ao menos um produto.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PedidoDAO pedidoDAO = new PedidoDAO();

        // Verifica se já há um pedido em andamento nessa mesa
        Pedido pedidoExistente = pedidoDAO.buscarPedidoAbertoPorMesa(mesa.getId());

        if (pedidoExistente != null) {
            // Pergunta se o usuário quer adicionar os produtos ao pedido existente
            int resposta = JOptionPane.showOptionDialog(
                    this,
                    "A mesa selecionada já possui um pedido em andamento.\nDeseja adicionar os novos itens ao pedido atual ou criar um novo?",
                    "Pedido em andamento",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Adicionar ao Existente", "Criar Novo Pedido"},
                    "Adicionar ao Existente"
            );

            if (resposta == -1) return; // Cancelado

            if (resposta == 0) { // Adicionar ao pedido existente
                boolean sucesso = pedidoDAO.adicionarProdutosAoPedido(pedidoExistente.getId(), produtosSelecionados);
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Produtos adicionados com sucesso ao pedido existente!","Sucesso", JOptionPane.PLAIN_MESSAGE, icon);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao adicionar produtos ao pedido.");
                }
                return;
            }
        }

        // Criação de novo pedido
        Pedido novoPedido = new Pedido(mesa, garcom, produtosSelecionados, "Em preparo");
        if (pedidoDAO.adicionarPedido(novoPedido)) {
            JOptionPane.showMessageDialog(this, "Novo pedido registrado com sucesso!","Sucesso", JOptionPane.PLAIN_MESSAGE, icon);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar novo pedido.");
        }
    }

    // Método principal para abrir a janela
    public static void main(String[] args) {
        new RealizarPedidoFrame();
    }
}
