package com.meuprojeto.ui;

import javax.swing.*;
import com.meuprojeto.util.ArquivoUtil;
import com.meuprojeto.model.ClienteReembolso;
import com.meuprojeto.util.ClienteUtil;

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
    private File pastaBase;
    private ClienteReembolso clienteSelecionado;
    private final JFrame menuPrincipal;
    private List<File> faturasDisponiveis;

    public RegistrarComprovanteFrame(JFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
        selecionarClienteOuDiretorio();
    }

    private void selecionarClienteOuDiretorio() {
        int escolha = JOptionPane.showOptionDialog(
                menuPrincipal,
                "Deseja selecionar um cliente cadastrado ou informar um diretório manualmente?",
                "Escolha a origem",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Selecionar Cliente", "Informar Diretório"},
                "Selecionar Cliente"
        );

        if (escolha == JOptionPane.YES_OPTION) {
            clienteSelecionado = ClienteUtil.selecionarCliente(menuPrincipal);
            if (clienteSelecionado == null) {
                cancelarTela();
                return;
            }
            pastaBase = new File(clienteSelecionado.getCaminhoBase());
        } else if (escolha == JOptionPane.NO_OPTION) {
            JFileChooser dirChooser = new JFileChooser();
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirChooser.setDialogTitle("Selecione o diretório base");
            int opcao = dirChooser.showOpenDialog(menuPrincipal);
            if (opcao != JFileChooser.APPROVE_OPTION) {
                cancelarTela();
                return;
            }
            pastaBase = dirChooser.getSelectedFile();
        } else {
            cancelarTela();
            return;
        }

        inicializarUI();
    }

    private void cancelarTela() {
        JOptionPane.showMessageDialog(menuPrincipal, "Operação cancelada.");
        menuPrincipal.setVisible(true);
        dispose();
    }

    private void inicializarUI() {
        setTitle("Registrar Comprovante");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(10, 1, 5, 5));

        JButton selecionarComprovanteBtn = new JButton("Selecionar Comprovante (PDF)");
        selecionarComprovanteBtn.addActionListener(e -> selecionarComprovante());

        destinoField = new JTextField(pastaBase.getAbsolutePath());
        destinoField.setEditable(false);

        JButton selecionarDestinoBtn = new JButton("Selecionar Pasta de Destino");
        selecionarDestinoBtn.addActionListener(e -> selecionarDestino());

        dataPagamentoField = new JTextField();

        faturasDisponiveis = listarFaturasSemData(pastaBase);
        faturasComboBox = new JComboBox<>(faturasDisponiveis.stream()
                .map(File::getName)
                .toArray(String[]::new));

        JButton registrarBtn = new JButton("Registrar Comprovante");
        registrarBtn.addActionListener(e -> registrarComprovante());

        JButton trocarClienteBtn = new JButton("Selecionar Outro Cliente/Diretório");
        trocarClienteBtn.addActionListener(e -> {
            this.dispose();
            new RegistrarComprovanteFrame(menuPrincipal).setVisible(true);
        });

        JButton voltarMenuBtn = new JButton("Voltar ao Menu");
        voltarMenuBtn.addActionListener(e -> {
            this.dispose();
            menuPrincipal.setVisible(true);
        });

        add(selecionarComprovanteBtn);
        add(new JLabel("Data de Pagamento (dd-MM-yyyy):"));
        add(dataPagamentoField);
        add(new JLabel("Fatura a vincular:"));
        add(faturasComboBox);
        add(selecionarDestinoBtn);
        add(destinoField);
        add(registrarBtn);
        add(trocarClienteBtn);
        add(voltarMenuBtn);

        setVisible(true);
    }

    private void selecionarComprovante() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione o comprovante (PDF)");
        int opcao = fileChooser.showOpenDialog(this);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            comprovanteSelecionado = fileChooser.getSelectedFile();
        }
    }

    private void selecionarDestino() {
        JFileChooser dirChooser = new JFileChooser(pastaBase);
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setDialogTitle("Selecione uma subpasta de destino");
        int opcao = dirChooser.showOpenDialog(this);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            File selecionado = dirChooser.getSelectedFile();
            if (clienteSelecionado != null &&
                    !selecionado.getAbsolutePath().startsWith(pastaBase.getAbsolutePath())) {
                JOptionPane.showMessageDialog(this, "A pasta deve estar dentro da pasta do cliente selecionado.");
                return;
            }
            destinoField.setText(selecionado.getAbsolutePath());
        }
    }

    private void registrarComprovante() {
        if (comprovanteSelecionado == null ||
                dataPagamentoField.getText().trim().isEmpty() ||
                faturasComboBox.getSelectedIndex() == -1 ||
                destinoField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.");
            return;
        }

        String dataPagamentoStr = dataPagamentoField.getText().trim();
        try {
            LocalDate.parse(dataPagamentoStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd-MM-yyyy.");
            return;
        }

        File faturaSelecionada = faturasDisponiveis.get(faturasComboBox.getSelectedIndex());
        String nomeBase = faturaSelecionada.getName().replace(".pdf", "");
        String novoNome = nomeBase + "_" + dataPagamentoStr + ".pdf";

        File destino = new File(destinoField.getText(), novoNome);

        boolean sucesso = ArquivoUtil.consolidarPDFs(faturaSelecionada, comprovanteSelecionado, destino);

        if (sucesso && faturaSelecionada.delete()) {
            JOptionPane.showMessageDialog(this, "Comprovante registrado e fatura consolidada com sucesso!");
            comprovanteSelecionado = null;
            dataPagamentoField.setText("");
            faturasDisponiveis.remove(faturaSelecionada);
            faturasComboBox.removeItem(faturaSelecionada.getName());
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao consolidar os documentos.");
        }
    }

    private List<File> listarFaturasSemData(File pasta) {
        File[] arquivos = pasta.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf") && !name.matches(".*_\\d{2}-\\d{2}-\\d{4}\\.pdf"));
        return arquivos != null ? List.of(arquivos) : List.of();
    }
} 
