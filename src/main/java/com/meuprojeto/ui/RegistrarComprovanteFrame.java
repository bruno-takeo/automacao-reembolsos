package com.meuprojeto.ui;

import javax.swing.*;
import com.meuprojeto.util.ArquivoUtil;
import com.meuprojeto.util.VisualizadorPDF;
import com.meuprojeto.model.ClienteReembolso;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class RegistrarComprovanteFrame extends JFrame {

    private JTextField dataPagamentoField;
    private JTextField destinoField;
    private JComboBox<String> faturasComboBox;
    private File comprovanteSelecionado;
    private JLabel comprovanteSelecionadoLabel;
    private File pastaBase;
    private ClienteReembolso clienteSelecionado;
    private final JFrame menuPrincipal;
    private List<File> faturasDisponiveis;

    private SelecionarClienteOuDiretorioPanel selecionarPanel;
    private JLabel nomeEmpresaLabel;

    public RegistrarComprovanteFrame(JFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Registrar Comprovante");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
                menuPrincipal.setVisible(true);
            }
        });

        setSize(600, 470);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(12, 1, 5, 5)); // +1 linha para o nome do cliente

        selecionarPanel = new SelecionarClienteOuDiretorioPanel(this);

        nomeEmpresaLabel = new JLabel("");
        nomeEmpresaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nomeEmpresaLabel.setForeground(new Color(0, 70, 140)); // azul escuro elegante

        selecionarPanel.setSelecaoListener(pasta -> {
            pastaBase = pasta;
            clienteSelecionado = selecionarPanel.getClienteSelecionado();
            destinoField.setText(pasta != null ? pasta.getAbsolutePath() : "");

            if (clienteSelecionado != null) {
                nomeEmpresaLabel.setText("Cliente selecionado: " + clienteSelecionado.getNomeEmpresa());
            } else {
                nomeEmpresaLabel.setText("");
            }

            atualizarFaturasComboBox();
        });

        add(selecionarPanel);
        add(nomeEmpresaLabel); // logo abaixo da seleção

        JButton selecionarComprovanteBtn = new JButton("Selecionar Comprovante (PDF)");
        selecionarComprovanteBtn.addActionListener(e -> selecionarComprovante());

        JButton visualizarComprovanteBtn = new JButton("Visualizar Comprovante");
        visualizarComprovanteBtn.addActionListener(e -> VisualizadorPDF.visualizarPDF(this, comprovanteSelecionado));


        dataPagamentoField = new JTextField();

        destinoField = new JTextField();
        destinoField.setEditable(false);

        faturasComboBox = new JComboBox<>();

        JButton registrarBtn = new JButton("Registrar Comprovante");
        registrarBtn.addActionListener(e -> registrarComprovante());

        JButton voltarMenuBtn = new JButton("Voltar ao Menu");
        voltarMenuBtn.addActionListener(e -> {
            this.dispose();
            menuPrincipal.setVisible(true);
        });

        add(selecionarComprovanteBtn);
        add(visualizarComprovanteBtn);
        comprovanteSelecionadoLabel = new JLabel("Nenhum comprovante selecionado.");
        comprovanteSelecionadoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        add(comprovanteSelecionadoLabel);
        add(new JLabel("Data de Pagamento (dd-MM-yyyy):"));
        add(dataPagamentoField);
        add(new JLabel("Fatura a ser vinculada:"));
        add(faturasComboBox);
        add(destinoField);
        add(registrarBtn);
        add(voltarMenuBtn);

        setVisible(true);
    }

    private void selecionarComprovante() {
        JFileChooser fileChooser;

        File pastaInicial = new File(destinoField.getText());
        if (pastaInicial.exists() && pastaInicial.isDirectory()) {
            fileChooser = new JFileChooser(pastaInicial);
        } else {
            fileChooser = new JFileChooser(); // fallback se não houver pasta válida
        }

        fileChooser.setDialogTitle("Selecione o comprovante (PDF)");
        int opcao = fileChooser.showOpenDialog(this);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            comprovanteSelecionado = fileChooser.getSelectedFile();
            comprovanteSelecionadoLabel.setText(comprovanteSelecionado.getAbsolutePath());
        }
    }

    private void atualizarFaturasComboBox() {
        faturasComboBox.removeAllItems();
        if (pastaBase != null) {
            faturasDisponiveis = listarFaturasSemData(pastaBase);
            for (File fatura : faturasDisponiveis) {
                faturasComboBox.addItem(fatura.getName());
            }
        }
    }

    private List<File> listarFaturasSemData(File pasta) {
        File[] arquivos = pasta.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf") && !name.matches(".*_\\d{2}-\\d{2}-\\d{4}\\.pdf"));
        return arquivos != null ? List.of(arquivos) : List.of();
    }

    private void registrarComprovante() {
        if (comprovanteSelecionado == null || dataPagamentoField.getText().trim().isEmpty()
                || pastaBase == null || faturasComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.");
            return;
        }

        String dataTexto = dataPagamentoField.getText().trim();
        LocalDate dataPagamento;

        try {
            dataPagamento = LocalDate.parse(dataTexto, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd-MM-yyyy.");
            return;
        }

        File faturaOriginal = faturasDisponiveis.get(faturasComboBox.getSelectedIndex());
        String nomeBase = faturaOriginal.getName().replace(".pdf", "") + "_" + dataTexto + ".pdf";
        File destino = new File(pastaBase, nomeBase);

        boolean sucesso = ArquivoUtil.consolidarPDFs(faturaOriginal, comprovanteSelecionado, destino);

        if (sucesso) {
            faturaOriginal.delete(); // Remove a fatura antiga
            JOptionPane.showMessageDialog(this, "Comprovante registrado com sucesso!");

            comprovanteSelecionado = null;
            dataPagamentoField.setText("");
            atualizarFaturasComboBox(); // atualiza para refletir a fatura removida
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar o comprovante.");
        }
    }

    public JComboBox<String> getFaturasComboBox() {
        return faturasComboBox;
    }

    public SelecionarClienteOuDiretorioPanel getSelecionarPanel() {
        return selecionarPanel;
    }

}
