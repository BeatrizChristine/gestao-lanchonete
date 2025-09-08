package view;

import dao.GarcomDAO;
import model.Garcom;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import util.ValidadorCampos;

public class CadastroGarcomFrame extends JFrame {
    private JTextField nomeField;
    private JTextField matriculaField;
    private JTable tabela;
    private DefaultTableModel modelo;
    private GarcomDAO dao;
    private JButton btnDesativar;

    // Ícone para mensagens de sucesso
    ImageIcon icon = new ImageIcon("src/icons/check.png");

    public CadastroGarcomFrame() {
        setTitle("Cadastro de Garçom");
        setSize(750, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        dao = new GarcomDAO();

        // Painel principal com layout vertical e borda
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBackground(new Color(245, 245, 245));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Título da janela
        JLabel titulo = new JLabel("Cadastro de Garçom");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelPrincipal.add(titulo);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));

        // Painel com formulário: dois campos (nome e matrícula) organizados em grid 2x2
        JPanel painelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        painelForm.setBackground(new Color(245, 245, 245));
        painelForm.add(new JLabel("Nome:"));
        nomeField = new JTextField();
        painelForm.add(nomeField);
        painelForm.add(new JLabel("Matrícula (4 dígitos):"));
        matriculaField = new JTextField();
        painelForm.add(matriculaField);

        // Painel de botões Salvar e Desativar/Ativar
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        painelBotoes.setBackground(new Color(245, 245, 245));
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setPreferredSize(new Dimension(100, 32));
        btnSalvar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnSalvar.setBackground(new Color(76, 175, 80));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDesativar = new JButton("Desativar");
        btnDesativar.setPreferredSize(new Dimension(100, 32));
        btnDesativar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnDesativar.setBackground(new Color(33, 150, 243));
        btnDesativar.setForeground(Color.WHITE);
        btnDesativar.setFocusPainted(false);
        btnDesativar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnDesativar);

        // Modelo da tabela com colunas e células não editáveis
        modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Matrícula", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Configuração da tabela: fonte, altura linha e cabeçalho
        tabela = new JTable(modelo);
        tabela.setRowHeight(26);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        // Listener para seleção da linha na tabela: atualiza texto e habilita botão de ativar/desativar
        tabela.getSelectionModel().addListSelectionListener(event -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                String status = (String) modelo.getValueAt(linha, 3);
                btnDesativar.setText(status.equals("Ativo") ? "Desativar" : "Ativar");
                btnDesativar.setEnabled(true);
            } else {
                btnDesativar.setEnabled(false);
            }
        });

        // Renderizador personalizado para colorir o status (verde para ativo, vermelho para inativo)
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String status = value.toString();
                    if (status.equals("Ativo")) {
                        c.setForeground(Color.GREEN.darker());
                    } else {
                        c.setForeground(Color.RED);
                    }
                }
                return c;
            }
        });

        // Scroll pane para a tabela com tamanho fixo
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new Dimension(660, 250));

        // Adiciona componentes ao painel principal
        painelPrincipal.add(painelForm);
        painelPrincipal.add(Box.createVerticalStrut(10));
        painelPrincipal.add(painelBotoes);
        painelPrincipal.add(Box.createVerticalStrut(10));
        painelPrincipal.add(scroll);

        add(painelPrincipal);

        // Carrega dados dos garçons na tabela
        carregarGarcons();

        // Adiciona ações aos botões
        btnSalvar.addActionListener(this::salvarGarcom);
        btnDesativar.addActionListener(this::desativarOuAtivarGarcom);

        setVisible(true);
    }

    // Método para salvar um novo garçom após validação dos campos
    private void salvarGarcom(ActionEvent e) {
        String nome = nomeField.getText().trim();
        String matricula = matriculaField.getText().trim();

        // Validação do nome (somente letras e espaços)
        if (!ValidadorCampos.nomeValido(nome)) {
            JOptionPane.showMessageDialog(this, "O nome deve conter apenas letras e espaços, sem números ou símbolos.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validação da matrícula (exatamente 4 dígitos numéricos)
        if (!ValidadorCampos.matriculaValida(matricula)) {
            JOptionPane.showMessageDialog(this, "A matrícula deve conter exatamente 4 números.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verifica se os campos não estão vazios e se matrícula tem 4 dígitos
        if (nome.isEmpty() || !matricula.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Preencha os campos corretamente.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verifica duplicidade de matrícula
        if (dao.matriculaJaExiste(matricula)) {
            JOptionPane.showMessageDialog(this, "Matrícula já cadastrada para outro garçom.");
            return;
        }

        // Verifica duplicidade de nome
        if (dao.nomeJaExiste(nome)) {
            JOptionPane.showMessageDialog(this, "Nome já cadastrado para outro garçom.");
            return;
        }

        // Cria objeto Garcom e tenta salvar no banco
        Garcom g = new Garcom(nome, matricula);
        if (dao.adicionarGarcom(g)) {
            JOptionPane.showMessageDialog(this, "Garçom cadastrado com sucesso!", "Sucesso", JOptionPane.PLAIN_MESSAGE, icon);
            nomeField.setText("");
            matriculaField.setText("");
            carregarGarcons();  // Atualiza tabela
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar garçom.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para ativar ou desativar o garçom selecionado na tabela
    private void desativarOuAtivarGarcom(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um garçom na tabela.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modelo.getValueAt(linha, 0);
        String statusAtual = (String) modelo.getValueAt(linha, 3);
        String acao = statusAtual.equals("Ativo") ? "desativar" : "ativar";

        // Confirmação do usuário antes da alteração
        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja " + acao + " este garçom?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) return;

        // Chama DAO para alterar status do garçom
        boolean sucesso = statusAtual.equals("Ativo") ? dao.desativarGarcom(id) : dao.ativarGarcom(id);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Garçom " + (acao.equals("desativar") ? "desativado" : "ativado") + " com sucesso!", "Sucesso", JOptionPane.PLAIN_MESSAGE, icon);
            carregarGarcons();  // Atualiza tabela para refletir mudança
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao tentar alterar o status do garçom.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Carrega todos os garçons da base de dados para a tabela
    private void carregarGarcons() {
        modelo.setRowCount(0); // limpa linhas existentes
        for (Garcom g : dao.listarGarcons()) {
            modelo.addRow(new Object[]{g.getId(), g.getNome(), g.getMatricula(), g.isAtivo() ? "Ativo" : "Inativo"});
        }
        btnDesativar.setEnabled(false); // desativa botão até seleção
    }

    // Método main para iniciar a aplicação em thread segura do Swing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CadastroGarcomFrame::new);
    }
}
