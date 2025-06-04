package com.meuprojeto.ui;

import javax.swing.*;
import com.meuprojeto.util.ArquivoUtil;
import com.meuprojeto.util.VisualizadorPDF;

import java.awt.*;
import java.io.File;

public class RegistrarFaturaFrame extends JFrame {

    private JTextField descricaoField;
    private JTextField valorField;
    private JTextField destinoField;
    private File faturaSelecionada;
    private JLabel faturaSelecionadaLabel;
    private final JFrame menuPrincipal;

    private SelecionarClienteOuDiretorioPanel selecionarPanel;
    private JButton registrarComprovanteBtn;
    private File pastaDestino;
    private String nomeFaturaRegistrada;

    public RegistrarFaturaFrame(JFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Registrar Fatura");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
                menuPrincipal.setVisible(true);
            }
        });

        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(12, 1, 5, 5)); // +1 linha para novo botão

        selecionarPanel = new SelecionarClienteOuDiretorioPanel(this);
        selecionarPanel.setSelecaoListener(pasta -> {
            if (pasta != null) {
                destinoField.setText(pasta.getAbsolutePath());
                pastaDestino = pasta;
            } else {
                destinoField.setText("");
                pastaDestino = null;
            }
        });
        add(selecionarPanel);

        JButton selecionarFaturaBtn = new JButton("Selecionar Fatura (PDF)");
        selecionarFaturaBtn.addActionListener(e -> selecionarFatura());

        JButton visualizarFaturaBtn = new JButton("Visualizar Fatura");
        visualizarFaturaBtn.addActionListener(e -> VisualizadorPDF.visualizarPDF(this, faturaSelecionada));

        descricaoField = new JTextField();
        valorField = new JTextField();

        destinoField = new JTextField();
        destinoField.setEditable(false);

        JButton registrarBtn = new JButton("Registrar");
        registrarBtn.addActionListener(e -> registrarFatura());

        registrarComprovanteBtn = new JButton("Registrar Comprovante para esta Fatura");
        registrarComprovanteBtn.setVisible(false); // só aparece após registro
        registrarComprovanteBtn.addActionListener(e -> abrirRegistrarComprovante());

        JButton voltarMenuBtn = new JButton("Voltar ao Menu");
        voltarMenuBtn.addActionListener(e -> {
            this.dispose();
            menuPrincipal.setVisible(true);
        });

        add(selecionarFaturaBtn);
        add(visualizarFaturaBtn);
        faturaSelecionadaLabel = new JLabel("Nenhuma fatura selecionada.");
        faturaSelecionadaLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        add(faturaSelecionadaLabel);
        add(new JLabel("Descrição:"));
        add(descricaoField);
        add(new JLabel("Valor (ex: 1234.56 ou 1234,56):"));
        add(valorField);
        add(destinoField);
        add(registrarBtn);
        add(registrarComprovanteBtn); // novo botão
        add(voltarMenuBtn);

        setVisible(true);
    }

    private void selecionarFatura() {
        JFileChooser fileChooser;

        File pastaInicial = new File(destinoField.getText());
        if (pastaInicial.exists() && pastaInicial.isDirectory()) {
            fileChooser = new JFileChooser(pastaInicial);
        } else {
            fileChooser = new JFileChooser();
        }

        fileChooser.setDialogTitle("Selecione o arquivo da fatura (PDF)");
        int opcao = fileChooser.showOpenDialog(this);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            faturaSelecionada = fileChooser.getSelectedFile();
            faturaSelecionadaLabel.setText(faturaSelecionada.getAbsolutePath());
        }
    }

    private void registrarFatura() {
        if (faturaSelecionada == null || descricaoField.getText().trim().isEmpty()
                || valorField.getText().trim().isEmpty() || destinoField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.");
            return;
        }

        String descricao = descricaoField.getText().trim().replaceAll("[\\\\/:*?\"<>|]", "-");
        String valor = valorField.getText().replace(",", ".").trim();

        if (!valor.matches("\\d+(\\.\\d{1,2})?")) {
            JOptionPane.showMessageDialog(this, "Valor inválido. Use formato numérico (ex: 1234.56).");
            return;
        }

        nomeFaturaRegistrada = descricao + "_" + valor + ".pdf";
        File destino = new File(destinoField.getText(), nomeFaturaRegistrada);

        boolean sucesso = ArquivoUtil.copiarArquivo(faturaSelecionada, destino);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Fatura registrada com sucesso!");
            // Limpa os campos após registro
            faturaSelecionada = null;
            faturaSelecionadaLabel.setText("Nenhuma fatura selecionada.");
            descricaoField.setText("");
            valorField.setText("");
            registrarComprovanteBtn.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar a fatura.");
        }
    }

    private void abrirRegistrarComprovante() {
        registrarComprovanteBtn.setVisible(false); // Esconde o botão para evitar uso duplicado
        
        this.setVisible(false);
        RegistrarComprovanteFrame comprovanteFrame = new RegistrarComprovanteFrame(this) {
            @Override
            public void dispose() {
                super.dispose();
                RegistrarFaturaFrame.this.setVisible(true); // volta para a tela de fatura
            }
        };

        // Preenchimento automático
        if (pastaDestino != null) {
            comprovanteFrame.getSelecionarPanel().setDiretorioInicial(pastaDestino);
            comprovanteFrame.getSelecionarPanel().forcarSelecaoDiretorio(pastaDestino);
        }

        // Pré-selecionar a fatura recém-criada no comboBox
        SwingUtilities.invokeLater(() -> {
            comprovanteFrame.getFaturasComboBox().setSelectedItem(nomeFaturaRegistrada);
        });
    }
}
