package view;

import dao.PedidoDAO;
import model.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class AcompanhamentoPedidosFrame extends JFrame {
    private JTable tabela;
    private DefaultTableModel modelo;
    private JButton botaoFinalizar, botaoCancelar;

    public AcompanhamentoPedidosFrame() {
        // Configurações básicas da janela
        setTitle("Acompanhamento de Pedidos");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Modelo da tabela, com colunas e células não editáveis
        modelo = new DefaultTableModel(new Object[]{
                "ID", "Mesa", "Garçom", "Produtos", "Status", "Data"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Cria a tabela configurando fonte, altura e seleção simples
        tabela = new JTable(modelo);
        tabela.setRowHeight(28);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderizador customizado para pintar as linhas conforme o status do pedido
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = table.getValueAt(row, 4).toString().toLowerCase();

                // Define cores de fundo conforme status do pedido
                if (status.contains("em preparo, pago")) {
                    c.setBackground(new Color(200, 255, 200)); // verde claro para pago em preparo
                } else if (status.contains("em preparo")) {
                    c.setBackground(new Color(255, 255, 180)); // amarelo claro para em preparo não pago
                } else {
                    c.setBackground(Color.WHITE); // branco padrão
                }

                // Destaque para linha selecionada
                if (isSelected) {
                    c.setBackground(new Color(100, 150, 255)); // azul claro
                    c.setForeground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                }

                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };

        // Aplica o renderizador customizado a todas as colunas
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // JScrollPane para permitir rolagem da tabela
        JScrollPane scroll = new JScrollPane(tabela);

        // Botão para finalizar pedido, com estilo e ação associada
        botaoFinalizar = new JButton("Finalizar Pedido");
        botaoFinalizar.setPreferredSize(new Dimension(160, 32));
        botaoFinalizar.setFont(new Font("SansSerif", Font.BOLD, 13));
        botaoFinalizar.setBackground(new Color(76, 175, 80));
        botaoFinalizar.setForeground(Color.WHITE);
        botaoFinalizar.setFocusPainted(false);
        botaoFinalizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoFinalizar.addActionListener(this::finalizarPedido);

        // Botão para cancelar pedido, com estilo e ação associada
        botaoCancelar = new JButton("Cancelar Pedido");
        botaoCancelar.setPreferredSize(new Dimension(160, 32));
        botaoCancelar.setFont(new Font("SansSerif", Font.BOLD, 13));
        botaoCancelar.setBackground(new Color(244, 67, 54));
        botaoCancelar.setForeground(Color.WHITE);
        botaoCancelar.setFocusPainted(false);
        botaoCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoCancelar.addActionListener(this::cancelarPedido);

        // Painel para os botões, alinhados centralmente com espaçamento
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        painelBotoes.setBackground(new Color(245, 245, 245));
        painelBotoes.add(botaoFinalizar);
        painelBotoes.add(botaoCancelar);

        // Adiciona componentes na janela: tabela no centro e botões na parte inferior
        add(scroll, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        // Carrega os pedidos para exibir na tabela ao iniciar a janela
        carregarPedidos();

        setVisible(true);
    }

    // Método que busca os pedidos no DAO e preenche a tabela
    private void carregarPedidos() {
        modelo.setRowCount(0); // limpa dados antigos da tabela
        PedidoDAO dao = new PedidoDAO();
        ArrayList<Pedido> pedidos = dao.listarPedidosCompletos();

        for (Pedido p : pedidos) {
            String status = p.getStatus().toLowerCase();

            // Exibe somente pedidos em preparo (não finalizados ou cancelados)
            if (status.contains("finalizado") || status.contains("cancelado")) {
                continue;
            }

            if (!status.contains("em preparo")) {
                continue;
            }

            // Junta nomes dos produtos separados por vírgula
            String nomesProdutos = p.getProdutos().stream()
                    .map(prod -> prod.getNome())
                    .collect(Collectors.joining(", "));

            // Adiciona uma linha com as informações do pedido
            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getMesa().getNumero(),
                    p.getGarcom().getNome(),
                    nomesProdutos,
                    p.getStatus(),
                    p.getDataHora()
            });
        }
    }

    // Ação para finalizar o pedido selecionado na tabela
    private void finalizarPedido(ActionEvent e) {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para finalizar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String statusOriginal = ((String) modelo.getValueAt(linhaSelecionada, 4)).trim().toLowerCase();

        // Verifica se o pedido já foi finalizado ou cancelado para impedir alteração
        if (statusOriginal.contains("finalizado") || statusOriginal.contains("cancelado")) {
            JOptionPane.showMessageDialog(this, "Este pedido já foi " + statusOriginal + " e não pode ser modificado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPedido = (int) modelo.getValueAt(linhaSelecionada, 0);
        PedidoDAO dao = new PedidoDAO();

        // Define novo status conforme pagamento
        String novoStatus;
        if (statusOriginal.equals("em preparo, pago")) {
            novoStatus = "Finalizado, pago";
        } else {
            novoStatus = "Finalizado, não pago";
        }

        // Atualiza status no banco
        boolean sucesso = dao.atualizarStatusPedido(idPedido, novoStatus);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Pedido finalizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarPedidos(); // atualiza tabela
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Ação para cancelar o pedido selecionado na tabela
    private void cancelarPedido(ActionEvent e) {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para cancelar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) modelo.getValueAt(linhaSelecionada, 4);
        // Verifica se o pedido não está finalizado, cancelado ou pago para permitir cancelamento
        if (status.equalsIgnoreCase("Finalizado") || status.equalsIgnoreCase("Cancelado") || status.equalsIgnoreCase("Pago")) {
            JOptionPane.showMessageDialog(this, "Este pedido já foi " + status.toLowerCase() + " e não pode ser modificado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPedido = (int) modelo.getValueAt(linhaSelecionada, 0);
        PedidoDAO dao = new PedidoDAO();
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente cancelar este pedido?", "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean sucesso = dao.atualizarStatusPedido(idPedido, "Cancelado");
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Pedido cancelado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPedidos();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new AcompanhamentoPedidosFrame();
    }
}
