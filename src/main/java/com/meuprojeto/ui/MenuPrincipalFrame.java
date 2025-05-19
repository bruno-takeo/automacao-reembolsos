package com.meuprojeto.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class MenuPrincipalFrame extends JFrame {

    public MenuPrincipalFrame() {
        setTitle("Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Painel principal com espaçamento
        JPanel contentPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Botões
        JButton registrarFaturaBtn = new JButton("Registrar Fatura");
        JButton registrarComprovanteBtn = new JButton("Registrar Comprovante");
        JButton gerarReembolsoBtn = new JButton("Gerar Reembolso");
        JButton cadastroClienteBtn = new JButton("Cadastro de Cliente");

        // Ações dos botões
        registrarFaturaBtn.addActionListener(e -> {
            this.setVisible(false);
            new RegistrarFaturaFrame(this).setVisible(true);
        });

        registrarComprovanteBtn.addActionListener(e -> {
            this.setVisible(false);
            new RegistrarComprovanteFrame(this).setVisible(true);
        });

        gerarReembolsoBtn.addActionListener(e -> {
            this.setVisible(false);
            new GerarReembolsoFrame(this).setVisible(true);
        });

        cadastroClienteBtn.addActionListener(e -> {
            this.setVisible(false);
            new CadastroClienteFrame(this).setVisible(true);
        });

        // Adiciona os botões ao painel
        contentPanel.add(registrarFaturaBtn);
        contentPanel.add(registrarComprovanteBtn);
        contentPanel.add(gerarReembolsoBtn);
        contentPanel.add(cadastroClienteBtn);

        // Define o painel como conteúdo da janela
        setContentPane(contentPanel);
    }
}
