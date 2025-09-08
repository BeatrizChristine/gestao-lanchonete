package view;

import dao.MesaDAO;
import model.Mesa;

import javax.swing.*;
import java.awt.*;

public class CadastroMesaFrame extends JFrame {

    private JSpinner campoNumero;
    private JButton botaoSalvar;

    // Ícone para ser exibido em mensagens de sucesso
    ImageIcon icon = new ImageIcon("src/icons/check.png");

    public CadastroMesaFrame() {
        setTitle("Cadastro de Mesa");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Painel principal com layout vertical e margem interna
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setBackground(new Color(245, 245, 245));
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Título centralizado
        JLabel titulo = new JLabel("Cadastrar Nova Mesa");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelPrincipal.add(titulo);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));

        // Linha de entrada: número da mesa (JSpinner)
        JPanel linhaNumero = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        linhaNumero.setBackground(new Color(245, 245, 245));
        JLabel labelNumero = new JLabel("Número da Mesa:");
        labelNumero.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Campo numérico com valores entre 1 e 100
        campoNumero = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        campoNumero.setPreferredSize(new Dimension(60, 28));

        // Alinhamento e estilo do texto do spinner
        JComponent editor = campoNumero.getEditor();
        JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));

        linhaNumero.add(labelNumero);
        linhaNumero.add(campoNumero);
        painelPrincipal.add(linhaNumero);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botão de salvar com estilo e ação
        botaoSalvar = new JButton("Salvar");
        botaoSalvar.setPreferredSize(new Dimension(100, 32));
        botaoSalvar.setFont(new Font("SansSerif", Font.BOLD, 13));
        botaoSalvar.setBackground(new Color(51, 153, 255));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setFocusPainted(false);
        botaoSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBackground(new Color(245, 245, 245));
        painelBotao.add(botaoSalvar);

        painelPrincipal.add(painelBotao);

        // Ação do botão "Salvar"
        botaoSalvar.addActionListener(e -> salvarMesa());

        setContentPane(painelPrincipal);
        setVisible(true);
    }

    // Método que realiza o cadastro da mesa
    private void salvarMesa() {
        int numero = (int) campoNumero.getValue(); // Obtém valor do spinner
        MesaDAO mesaDAO = new MesaDAO();

        // Verifica se já existe uma mesa com o mesmo número
        if (mesaDAO.existeMesaPorNumero(numero)) {
            JOptionPane.showMessageDialog(this, "Já existe uma mesa com esse número.",
                    "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Cria objeto Mesa com o número informado e disponibilidade 'false' (livre)
        Mesa mesa = new Mesa(numero, false);

        // Tenta adicionar a mesa no banco de dados
        if (mesaDAO.adicionarMesa(mesa)) {
            JOptionPane.showMessageDialog(this,
                    "Mesa cadastrada com sucesso!",
                    "Sucesso",
                    JOptionPane.PLAIN_MESSAGE,
                    icon);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar mesa.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new CadastroMesaFrame();
    }
}
