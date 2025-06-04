package com.meuprojeto.util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class VisualizadorPDF {

    public static void visualizarPDF(Component parentComponent, File arquivo) {
        if (arquivo == null) {
            JOptionPane.showMessageDialog(parentComponent, "Selecione um documento antes de visualizar.");
            return;
        }

        if (!arquivo.exists() || !arquivo.getName().toLowerCase().endsWith(".pdf")) {
            JOptionPane.showMessageDialog(parentComponent, "Arquivo inválido ou inexistente.");
            return;
        }

        try {
            Desktop.getDesktop().open(arquivo);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentComponent, "Erro ao abrir o arquivo: " + e.getMessage());
        }
    }
}
