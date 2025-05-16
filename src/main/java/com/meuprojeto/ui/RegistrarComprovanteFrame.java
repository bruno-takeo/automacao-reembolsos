package com.meuprojeto.ui;

import com.meuprojeto.util.ArquivoUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
//import java.util.Date;
import java.util.Objects;

public class RegistrarComprovanteFrame extends JFrame {

    private final JFrame menuPrincipal;
    private File comprovanteSelecionado;
    private File pastaDestino;
    private final JTextField dataPagamentoField = new JTextField();
    private final JTextField destinoField = new JTextField();
    private final JComboBox<String> faturaComboBox = new JComboBox<>();
    private File[] faturasDisponiveis;

    public RegistrarComprovanteFrame(JFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;

        setTitle("Registrar Comprovante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(9, 1, 5, 5)); // aumentei para 9 linhas

        JButton selecionarComprovanteBtn = new JButton("Selecionar Comprovante (PDF)");
        selecionarComprovanteBtn.addActionListener(e -> selecionarComprovante());

        JButton selecionarDestinoBtn = new JButton("Selecionar Pasta de Destino");
        selecionarDestinoBtn.addActionListener(e -> selecionarPastaDestino());

        destinoField.setEditable(false);

        JButton registrarBtn = new JButton("Registrar Comprovante");
        registrarBtn.addActionListener(e -> registrarComprovante());

        JButton voltarBtn = new JButton("Voltar");
        voltarBtn.addActionListener(e -> {
            this.dispose();
            menuPrincipal.setVisible(true);
        });

        add(selecionarComprovanteBtn);
        add(new JLabel("Data de Pagamento (dd-MM-yyyy):"));
        add(dataPagamentoField);
        add(selecionarDestinoBtn);
        add(destinoField);
        add(new JLabel("Selecione a Fatura correspondente:"));
        add(faturaComboBox);
        add(registrarBtn);
        add(voltarBtn);
    }

    private void selecionarComprovante() {
        JFileChooser fileChooser = new JFileChooser();
        int opcao = fileChooser.showOpenDialog(this);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            comprovanteSelecionado = fileChooser.getSelectedFile();
        }
    }

    private void selecionarPastaDestino() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int opcao = dirChooser.showOpenDialog(this);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            pastaDestino = dirChooser.getSelectedFile();
            destinoField.setText(pastaDestino.getAbsolutePath());
            carregarFaturasSemComprovante();
        }
    }

    private void carregarFaturasSemComprovante() {
        faturaComboBox.removeAllItems();
        faturasDisponiveis = pastaDestino.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".pdf") && !name.matches(".*_\\d{2}-\\d{2}-\\d{4}\\.pdf$"));

        if (faturasDisponiveis != null && faturasDisponiveis.length > 0) {
            Arrays.stream(faturasDisponiveis)
                    .map(File::getName)
                    .forEach(faturaComboBox::addItem);
        } else {
            JOptionPane.showMessageDialog(this, "Nenhuma fatura disponível para registrar comprovante nesta pasta.");
        }
    }

    private void registrarComprovante() {
        String data = dataPagamentoField.getText().trim();

        if (comprovanteSelecionado == null || pastaDestino == null || data.isEmpty() || faturaComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.");
            return;
        }

        if (!data.matches("\\d{2}-\\d{2}-\\d{4}")) {
            JOptionPane.showMessageDialog(this, "A data deve estar no formato dd-MM-yyyy.");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            sdf.parse(data); // valida rigorosamente
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data inválida.");
            return;
        }

        String nomeFatura = Objects.requireNonNull(faturaComboBox.getSelectedItem()).toString();
        File faturaSelecionada = new File(pastaDestino, nomeFatura);

        String novoNome = nomeFatura.replace(".pdf", "") + "_" + data + ".pdf";
        File arquivoFinal = new File(pastaDestino, novoNome);

        boolean sucesso = ArquivoUtil.consolidarPDFs(faturaSelecionada, comprovanteSelecionado, arquivoFinal);

        if (sucesso) {
            if (!faturaSelecionada.delete()) {
                JOptionPane.showMessageDialog(this, "Fatura registrada, mas não foi possível apagar o arquivo original.");
            }
            JOptionPane.showMessageDialog(this, "Comprovante registrado com sucesso!");
            this.dispose();
            menuPrincipal.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao consolidar os PDFs.");
        }
    }
}
