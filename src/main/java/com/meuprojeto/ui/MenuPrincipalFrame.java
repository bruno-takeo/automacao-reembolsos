package com.meuprojeto.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class MenuPrincipalFrame extends JFrame {

    public MenuPrincipalFrame() {
        setTitle("Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton registrarFaturaBtn = new JButton("Registrar Fatura");
        JButton registrarComprovanteBtn = new JButton("Registrar Comprovante");
        JButton gerarReembolsoBtn = new JButton("Gerar Reembolso");

        registrarFaturaBtn.addActionListener(e -> {
            this.setVisible(false);
            RegistrarFaturaFrame faturaFrame = new RegistrarFaturaFrame(this);
            faturaFrame.setVisible(true);
        });

        registrarComprovanteBtn.addActionListener(e -> {
            this.setVisible(false);
            RegistrarComprovanteFrame comprovanteFrame = new RegistrarComprovanteFrame(this);
            comprovanteFrame.setVisible(true);
        });

        gerarReembolsoBtn.addActionListener(e -> {
            this.setVisible(false);
            GerarReembolsoFrame reembolsoFrame = new GerarReembolsoFrame(this);
            reembolsoFrame.setVisible(true);
        });

        contentPanel.add(registrarFaturaBtn);
        contentPanel.add(registrarComprovanteBtn);
        contentPanel.add(gerarReembolsoBtn);

        setContentPane(contentPanel);
    }
}
