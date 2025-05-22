package com.meuprojeto.ui;

import javax.swing.*;

import com.meuprojeto.util.ArquivoUtil;

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
        setSize(600, 450);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(10, 1, 5, 5));

        selecionarPanel = new SelecionarClienteOuDiretorioPanel(this);
        selecionarPanel.setSelecaoListener(pasta -> {
            if (pasta != null) {
                destinoField.setText(pasta.getAbsolutePath());
            } else {
                destinoField.setText("");
            }
        });
        add(selecionarPanel);

        JButton selecionarFaturaBtn = new JButton("Selecionar Fatura (PDF)");
        selecionarFaturaBtn.addActionListener(e -> selecionarFatura());

        descricaoField = new JTextField();
        valorField = new JTextField();

        destinoField = new JTextField();
        destinoField.setEditable(false);

        JButton registrarBtn = new JButton("Registrar");
        registrarBtn.addActionListener(e -> registrarFatura());

        JButton voltarMenuBtn = new JButton("Voltar ao Menu");
        voltarMenuBtn.addActionListener(e -> {
            this.dispose();
            menuPrincipal.setVisible(true);
        });

        add(selecionarFaturaBtn);
        faturaSelecionadaLabel = new JLabel("Nenhuma fatura selecionada.");
        faturaSelecionadaLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        add(faturaSelecionadaLabel);
        add(new JLabel("Descrição:"));
        add(descricaoField);
        add(new JLabel("Valor (ex: 1234.56 ou 1234,56):"));
        add(valorField);
        add(destinoField);
        add(registrarBtn);
        add(voltarMenuBtn);

        setVisible(true);
    }

    private void selecionarFatura() {
        JFileChooser fileChooser;

        // Verifica se o campo destino tem um caminho válido
        File pastaInicial = new File(destinoField.getText());
        if (pastaInicial.exists() && pastaInicial.isDirectory()) {
            fileChooser = new JFileChooser(pastaInicial);
        } else {
            fileChooser = new JFileChooser(); // fallback para diretório padrão
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

        String novoNome = descricao + "_" + valor + ".pdf";
        File destino = new File(destinoField.getText(), novoNome);

        boolean sucesso = ArquivoUtil.copiarArquivo(faturaSelecionada, destino);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Fatura registrada com sucesso!");
            faturaSelecionada = null;
            descricaoField.setText("");
            valorField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar a fatura.");
        }
    }
}
