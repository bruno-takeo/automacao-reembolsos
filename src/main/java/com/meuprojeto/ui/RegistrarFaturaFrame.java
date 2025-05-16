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
    private final JFrame menuPrincipal;

    public RegistrarFaturaFrame(JFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Registrar Fatura");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 1, 5, 5));

        JButton selecionarFaturaBtn = new JButton("Selecionar Fatura (PDF)");
        selecionarFaturaBtn.addActionListener(e -> selecionarFatura());

        JButton selecionarDestinoBtn = new JButton("Selecionar Pasta de Destino");
        selecionarDestinoBtn.addActionListener(e -> selecionarDestino());

        descricaoField = new JTextField();
        valorField = new JTextField();
        destinoField = new JTextField();
        destinoField.setEditable(false);

        JButton registrarBtn = new JButton("Registrar");
        registrarBtn.addActionListener(e -> registrarFatura());

        JButton voltarBtn = new JButton("Voltar");
        voltarBtn.addActionListener(e -> {
            this.dispose();
            menuPrincipal.setVisible(true);
        });

        add(selecionarFaturaBtn);
        add(new JLabel("Descrição:"));
        add(descricaoField);
        add(new JLabel("Valor (ex: 1234.56 ou 1234,56):"));
        add(valorField);
        add(selecionarDestinoBtn);
        add(destinoField);
        add(registrarBtn);
        add(voltarBtn);
    }

    private void selecionarFatura() {
        JFileChooser fileChooser = new JFileChooser();
        int opcao = fileChooser.showOpenDialog(this);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            faturaSelecionada = fileChooser.getSelectedFile();
        }
    }

    private void selecionarDestino() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int opcao = dirChooser.showOpenDialog(this);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            destinoField.setText(dirChooser.getSelectedFile().getAbsolutePath());
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
            this.dispose();
            menuPrincipal.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar a fatura.");
        }
    }
}
