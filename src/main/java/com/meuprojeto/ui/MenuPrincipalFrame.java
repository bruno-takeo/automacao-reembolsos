package com.meuprojeto.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class MenuPrincipalFrame extends JFrame {

    public MenuPrincipalFrame() {
        setTitle("Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Painel com padding
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton registrarFaturaBtn = new JButton("Registrar Fatura");
        JButton registrarComprovanteBtn = new JButton("Registrar Comprovante");

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

        contentPanel.add(registrarFaturaBtn);
        contentPanel.add(registrarComprovanteBtn);

        setContentPane(contentPanel);
    }

}
