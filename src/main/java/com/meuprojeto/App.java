package com.meuprojeto;

import javax.swing.SwingUtilities;
import com.meuprojeto.ui.MenuPrincipalFrame;
import com.meuprojeto.util.InicializadorApp;

public class App {
    public static void main(String[] args) {
        InicializadorApp.inicializar(); // Cria a pasta e arquivo json para salvar dados dos clientes se não existir

        SwingUtilities.invokeLater(() -> {
            new MenuPrincipalFrame().setVisible(true);
        });
    }
}