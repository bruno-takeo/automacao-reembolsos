package com.meuprojeto.ui;

import javax.swing.*;
import com.meuprojeto.model.ClienteReembolso;
import com.meuprojeto.util.ArquivoUtil;
import com.meuprojeto.util.ClienteUtil;

import java.awt.*;
import java.io.File;

public class RegistrarFaturaFrame extends JFrame {

    private JTextField descricaoField;
    private JTextField valorField;
    private JTextField destinoField;
    private File faturaSelecionada;
    private File pastaBase;
    private ClienteReembolso clienteSelecionado;
    private final JFrame menuPrincipal;

    public RegistrarFaturaFrame(JFrame menuPrincipal) {
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
        setTitle("Registrar Fatura");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(10, 1, 5, 5));

        JButton selecionarFaturaBtn = new JButton("Selecionar Fatura (PDF)");
        selecionarFaturaBtn.addActionListener(e -> selecionarFatura());

        JButton selecionarDestinoBtn = new JButton("Selecionar Pasta de Destino");
        selecionarDestinoBtn.addActionListener(e -> selecionarDestino());

        descricaoField = new JTextField();
        valorField = new JTextField();

        destinoField = new JTextField(pastaBase.getAbsolutePath());
        destinoField.setEditable(false);

        JButton registrarBtn = new JButton("Registrar");
        registrarBtn.addActionListener(e -> registrarFatura());

        JButton trocarClienteBtn = new JButton("Selecionar Outro Cliente/Diretório");
        trocarClienteBtn.addActionListener(e -> {
            this.dispose();
            new RegistrarFaturaFrame(menuPrincipal).setVisible(true);
        });

        JButton voltarMenuBtn = new JButton("Voltar ao Menu");
        voltarMenuBtn.addActionListener(e -> {
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
        add(trocarClienteBtn);
        add(voltarMenuBtn);

        setVisible(true);
    }

    private void selecionarFatura() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione o arquivo da fatura (PDF)");
        int opcao = fileChooser.showOpenDialog(this);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            faturaSelecionada = fileChooser.getSelectedFile();
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
            // Limpa os campos para novo registro, mantendo cliente atual
            faturaSelecionada = null;
            descricaoField.setText("");
            valorField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar a fatura.");
        }
    }
}
