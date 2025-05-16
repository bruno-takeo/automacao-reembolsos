package com.meuprojeto;

import javax.swing.SwingUtilities;
import com.meuprojeto.ui.MenuPrincipalFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipalFrame().setVisible(true);
        });
    }
}