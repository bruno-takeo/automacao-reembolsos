package com.meuprojeto.ui;

import com.meuprojeto.model.ClienteReembolso;
import com.meuprojeto.util.ClienteUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SelecionarClienteOuDiretorioPanel extends JPanel {

    private final JTextField pastaTextField;
    private final JButton selecionarClienteButton;
    private final JButton selecionarDiretorioButton;

    private File pastaSelecionada;
    private ClienteReembolso clienteSelecionado;

    private SelecaoListener selecaoListener;

    public SelecionarClienteOuDiretorioPanel(JFrame parentFrame) {
        this.setLayout(new FlowLayout());

        pastaTextField = new JTextField(35);
        pastaTextField.setEditable(false);

        selecionarClienteButton = new JButton("Selecionar Cliente");
        selecionarClienteButton.addActionListener(e -> selecionarCliente(parentFrame));

        selecionarDiretorioButton = new JButton("Selecionar Pasta Manualmente");
        selecionarDiretorioButton.addActionListener(e -> selecionarDiretorio(parentFrame));

        this.add(new JLabel("Pasta:"));
        this.add(pastaTextField);
        this.add(selecionarClienteButton);
        this.add(selecionarDiretorioButton);
    }

    public void setSelecaoListener(SelecaoListener listener) {
        this.selecaoListener = listener;
    }

    private void notificarSelecao(File pasta) {
        if (selecaoListener != null) {
            selecaoListener.aoSelecionarPasta(pasta);
        }
    }

    private void selecionarCliente(JFrame parentFrame) {
        ClienteReembolso selecionado = ClienteUtil.selecionarCliente(parentFrame);
        if (selecionado != null) {
            this.clienteSelecionado = selecionado;

            JFileChooser chooser = new JFileChooser(new File(clienteSelecionado.getCaminhoBase()));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int resultado = chooser.showOpenDialog(parentFrame);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File escolhido = chooser.getSelectedFile();
                if (!escolhido.getAbsolutePath().startsWith(clienteSelecionado.getCaminhoBase())) {
                    JOptionPane.showMessageDialog(parentFrame, "A pasta selecionada deve estar dentro do diretório do cliente.");
                    return;
                }

                this.pastaSelecionada = escolhido;
                pastaTextField.setText(escolhido.getAbsolutePath());
                notificarSelecao(escolhido);
            }
        }
    }

    private void selecionarDiretorio(JFrame parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int resultado = fileChooser.showOpenDialog(parentFrame);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            this.pastaSelecionada = fileChooser.getSelectedFile();
            this.clienteSelecionado = null;
            pastaTextField.setText(pastaSelecionada.getAbsolutePath());
            notificarSelecao(pastaSelecionada);
        }
    }

    public File getPastaSelecionada() {
        return pastaSelecionada;
    }

    public ClienteReembolso getClienteSelecionado() {
        return clienteSelecionado;
    }

    public void resetarSelecao() {
        this.pastaSelecionada = null;
        this.clienteSelecionado = null;
        pastaTextField.setText("");
        notificarSelecao(null);
    }
}