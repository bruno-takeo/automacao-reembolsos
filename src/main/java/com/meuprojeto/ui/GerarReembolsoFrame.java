package com.meuprojeto.ui;

import com.meuprojeto.util.ArquivoUtil;
import com.meuprojeto.util.ExcelUtil;
import com.meuprojeto.model.ReembolsoInfo;
import com.meuprojeto.service.ReembolsoService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.ParseException;
import java.util.List;


public class GerarReembolsoFrame extends JFrame {

    private final JButton selecionarPastaButton;
    private final JTextField pastaTextField;
    private final JButton gerarButton;
    private final MenuPrincipalFrame menuPrincipal;

    public GerarReembolsoFrame(MenuPrincipalFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;

        setTitle("Gerador de Reembolso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 150);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Faz o menu principal reaparecer quando esta janela for fechada
    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            menuPrincipal.setVisible(true);
        }
    });
    
        JPanel panel = new JPanel(new FlowLayout());

        pastaTextField = new JTextField(30);
        pastaTextField.setEditable(false);

        selecionarPastaButton = new JButton("Selecionar Pasta");
        selecionarPastaButton.addActionListener(this::selecionarPasta);

        gerarButton = new JButton("Gerar Reembolso");
        gerarButton.addActionListener(this::gerarReembolso);

        panel.add(new JLabel("Pasta:"));
        panel.add(pastaTextField);
        panel.add(selecionarPastaButton);
        panel.add(gerarButton);

        add(panel, BorderLayout.CENTER);
    }

    private void selecionarPasta(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File pastaSelecionada = fileChooser.getSelectedFile();
            pastaTextField.setText(pastaSelecionada.getAbsolutePath());
        }
    }

    private void gerarReembolso(ActionEvent e) {
        try {
            File pastaSelecionada = new File(pastaTextField.getText());
            if (!pastaSelecionada.exists() || !pastaSelecionada.isDirectory()) {
                JOptionPane.showMessageDialog(this, "Selecione uma pasta válida.");
                return;
            }

            List<ReembolsoInfo> infos = ReembolsoService.lerArquivosValidos(pastaSelecionada);
            if (infos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum arquivo válido encontrado.");
                return;
            }

            File excelTemp = new File(pastaSelecionada, "planilha-temp.xlsx");
            File planilhaPDF = new File(pastaSelecionada, "planilha.pdf");
            File novoPDF = new File(pastaSelecionada, "reembolso_final.pdf");

            ExcelUtil.gerarPlanilhaExcel(excelTemp, infos);
            ArquivoUtil.converterExcelParaPDFComLibreOffice(excelTemp, planilhaPDF);
            ReembolsoService.consolidarPDFsOrdenados(infos, novoPDF);

            JOptionPane.showMessageDialog(this, "Reembolso gerado com sucesso!");

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao ler os nomes dos arquivos. Verifique o padrão.");
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar reembolso: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}