package view;

import dao.MesaDAO;
import dao.PedidoDAO;
import model.Mesa;
import model.Pedido;
import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FechamentoContaFrame extends JFrame {
    private JComboBox<Mesa> comboMesas; // ComboBox com as mesas disponíveis
    private JTable tabela;               // Tabela com os pedidos
    private DefaultTableModel modelo;    // Modelo da tabela
    private JLabel labelTotal;          // Label que mostra o total da mesa
    private JButton botaoPagarSelecionado; // Botão para marcar o pedido como pago

    // Ícone usado na mensagem de sucesso
    ImageIcon icon = new ImageIcon("src/icons/check.png");

    public FechamentoContaFrame() {
        setTitle("Fechamento de Conta por Mesa");
        setSize(850, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Painel principal da tela
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(new Color(245, 245, 245));

        // ComboBox de mesas (carrega todas as mesas do banco)
        comboMesas = new JComboBox<>();
        for (Mesa m : new MesaDAO().listarMesas()) {
            comboMesas.addItem(m);
        }
        comboMesas.addActionListener(this::carregarPedidos);

        // Painel superior com a seleção da mesa
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        topo.setBackground(new Color(245, 245, 245));
        topo.add(new JLabel("Mesa:"));
        topo.add(comboMesas);

        // Configuração do modelo da tabela
        modelo = new DefaultTableModel(new Object[]{"Pedido ID", "Produtos", "Status", "Valor Total (R$)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Criação da tabela com o modelo
        tabela = new JTable(modelo);
        tabela.setRowHeight(28);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        JScrollPane scroll = new JScrollPane(tabela);

        // Label que mostra o total geral da mesa
        labelTotal = new JLabel("Total: R$ 0.00");
        labelTotal.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Botão de pagar pedido selecionado
        botaoPagarSelecionado = new JButton("Marcar como Pago");
        botaoPagarSelecionado.setPreferredSize(new Dimension(160, 32));
        botaoPagarSelecionado.setFont(new Font("SansSerif", Font.BOLD, 13));
        botaoPagarSelecionado.setBackground(new Color(76, 175, 80)); // verde
        botaoPagarSelecionado.setForeground(Color.WHITE);
        botaoPagarSelecionado.setFocusPainted(false);
        botaoPagarSelecionado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoPagarSelecionado.addActionListener(this::pagarPedidoSelecionado);

        // Rodapé com o total e botão de pagar
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(new Color(245, 245, 245));
        rodape.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        rodape.add(labelTotal, BorderLayout.WEST);
        rodape.add(botaoPagarSelecionado, BorderLayout.EAST);

        // Adiciona os painéis ao painel principal
        painelPrincipal.add(topo, BorderLayout.NORTH);
        painelPrincipal.add(scroll, BorderLayout.CENTER);
        painelPrincipal.add(rodape, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);

        // Carrega os pedidos da primeira mesa por padrão
        carregarPedidos(null);

        setVisible(true);
    }

    // Carrega os pedidos da mesa selecionada e atualiza a tabela e o total
    private void carregarPedidos(ActionEvent e) {
        modelo.setRowCount(0); // limpa a tabela
        Mesa mesa = (Mesa) comboMesas.getSelectedItem();
        double total = 0.0;

        if (mesa != null) {
            ArrayList<Pedido> pedidos = new PedidoDAO().listarPedidosCompletos();
            pedidos.sort((p1, p2) -> Integer.compare(p1.getId(), p2.getId()));

            for (Pedido p : pedidos) {
                String status = p.getStatus().trim().toLowerCase();

                // Ignora pedidos cancelados ou já pagos
                boolean isCancelado = status.contains("cancelado");
                boolean isRealmentePago = status.equals("finalizado, pago") ||
                        status.equals("em preparo, pago") ||
                        status.equals("pago");

                if (isCancelado || isRealmentePago) {
                    continue;
                }

                // Só mostra pedidos da mesa selecionada
                if (p.getMesa().getId() == mesa.getId()) {
                    double totalPedido = p.getProdutos().stream().mapToDouble(Produto::getPreco).sum();
                    String nomesProdutos = p.getProdutos().stream()
                            .map(Produto::getNome)
                            .collect(Collectors.joining(", "));

                    modelo.addRow(new Object[]{
                            p.getId(),
                            nomesProdutos,
                            p.getStatus(),
                            String.format("%.2f", totalPedido)
                    });

                    total += totalPedido;
                }
            }
        }

        // Atualiza o rótulo com o total da mesa
        labelTotal.setText("Total: R$ " + String.format("%.2f", total));
    }

    // Marca o pedido selecionado como "pago", se possível
    private void pagarPedidoSelecionado(ActionEvent e) {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para marcar como pago.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPedido = (int) modelo.getValueAt(linhaSelecionada, 0);
        String status = ((String) modelo.getValueAt(linhaSelecionada, 2)).toLowerCase();

        // Bloqueia tentativa de pagamento de pedidos já pagos ou cancelados
        if (status.contains("cancelado") || status.contains("finalizado, pago")) {
            JOptionPane.showMessageDialog(this, "Este pedido já está " + status + ".", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmação antes de marcar como pago
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja marcar o pedido como pago?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String novoStatus;

            if (status.contains("finalizado") && status.contains("não pago")) {
                novoStatus = "Finalizado, pago";
            } else if (status.contains("em preparo") || status.contains("aberto") || status.contains("não pago")) {
                novoStatus = "Em preparo, pago";
            } else {
                JOptionPane.showMessageDialog(this, "Status atual não permite pagamento automático.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Atualiza o status no banco de dados
            boolean sucesso = new PedidoDAO().atualizarStatusPedido(idPedido, novoStatus);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Pedido marcado como pago.", "Sucesso", JOptionPane.PLAIN_MESSAGE, icon);
                carregarPedidos(null); // Atualiza a tabela após a mudança
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new FechamentoContaFrame();
    }
}
