package view;

import dao.PedidoDAO;
import model.Pedido;
import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class HistoricoPagosFrame extends JFrame {
    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel totalArrecadadoLabel;

    public HistoricoPagosFrame() {
        setTitle("Histórico de Pedidos Pagos");
        setSize(950, 500); // Define o tamanho da janela
        setLocationRelativeTo(null); // Centraliza a janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas essa janela
        setLayout(new BorderLayout()); // Layout da janela principal

        // Criação do modelo da tabela com colunas definidas
        modelo = new DefaultTableModel(new Object[]{
                "ID", "Mesa", "Garçom", "Produtos", "Total (R$)", "Data"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Deixa todas as células da tabela como não editáveis
            }
        };

        // Criação da tabela usando o modelo acima
        tabela = new JTable(modelo);
        tabela.setRowHeight(28);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));

        // Centraliza o conteúdo das colunas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Adiciona a tabela a um painel de rolagem
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Pedidos Pagos"));

        // Criação do rótulo que mostra o total arrecadado
        totalArrecadadoLabel = new JLabel("Total arrecadado: R$ 0,00");
        totalArrecadadoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalArrecadadoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalArrecadadoLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 30));

        // Monta os componentes na tela
        add(scroll, BorderLayout.CENTER);
        add(totalArrecadadoLabel, BorderLayout.SOUTH);

        // Carrega os dados reais da base
        carregarPedidosPagos();

        setVisible(true); // Exibe a janela
    }

    // Método responsável por buscar e mostrar os pedidos com status "Pago"
    private void carregarPedidosPagos() {
        PedidoDAO dao = new PedidoDAO();
        ArrayList<Pedido> pedidos = dao.listarPedidosCompletos(); // Busca todos os pedidos

        double totalGeral = 0.0;
        modelo.setRowCount(0); // Limpa qualquer linha anterior da tabela

        for (Pedido p : pedidos) {
            if ("Pago".equalsIgnoreCase(p.getStatus())) { // Só considera pedidos pagos
                double total = p.getProdutos().stream().mapToDouble(Produto::getPreco).sum(); // Soma preços
                totalGeral += total;

                // Junta os nomes dos produtos separados por vírgula
                String produtos = p.getProdutos().stream()
                        .map(Produto::getNome)
                        .collect(Collectors.joining(", "));

                // Adiciona uma linha na tabela
                modelo.addRow(new Object[]{
                        p.getId(),
                        p.getMesa().getNumero(),
                        p.getGarcom().getNome(),
                        produtos,
                        String.format("R$ %.2f", total),
                        p.getDataHora()
                });
            }
        }

        // Atualiza o rótulo com o total arrecadado
        totalArrecadadoLabel.setText(String.format("Total arrecadado: R$ %.2f", totalGeral));
    }

    public static void main(String[] args) {
        new HistoricoPagosFrame(); // Inicializa a janela
    }
}
