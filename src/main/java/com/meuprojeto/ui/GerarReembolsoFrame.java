package com.meuprojeto.ui;

import com.meuprojeto.util.ArquivoUtil;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class GerarReembolsoFrame extends JFrame {

    private File pastaSelecionada;
    private final JTextField pastaField = new JTextField();
    private final JTextField nomePlanilhaPDFField = new JTextField("planilha-reembolso.pdf");
    private final JTextField nomePDFConsolidadoField = new JTextField("consolidado-reembolso.pdf");

    public GerarReembolsoFrame(JFrame menuPrincipal) {
        setTitle("Gerar Reembolso");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 1, 5, 5));

        JButton selecionarPastaBtn = new JButton("Selecionar Pasta");
        selecionarPastaBtn.addActionListener(e -> selecionarPasta());

        pastaField.setEditable(false);

        JButton gerarBtn = new JButton("Gerar Reembolso");
        gerarBtn.addActionListener(e -> gerarReembolso());

        add(selecionarPastaBtn);
        add(pastaField);
        add(new JLabel("Nome do PDF da planilha gerada:"));
        add(nomePlanilhaPDFField);
        add(new JLabel("Nome do PDF consolidado:"));
        add(nomePDFConsolidadoField);
        add(gerarBtn);
    }

    private void selecionarPasta() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pastaSelecionada = chooser.getSelectedFile();
            pastaField.setText(pastaSelecionada.getAbsolutePath());
        }
    }

    private void gerarReembolso() {
        if (pastaSelecionada == null || nomePlanilhaPDFField.getText().trim().isEmpty() || nomePDFConsolidadoField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos e selecione a pasta.");
            return;
        }

        File[] arquivos = pastaSelecionada.listFiles((dir, name) -> name.matches(".*_\\d+(\\.\\d{2})?_\\d{2}-\\d{2}-\\d{4}\\.pdf"));

        if (arquivos == null || arquivos.length == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum arquivo válido encontrado na pasta.");
            return;
        }

        List<ReembolsoInfo> infos = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        for (File arquivo : arquivos) {
            String nome = arquivo.getName().replace(".pdf", "");
            try {
                String[] partes = nome.split("_");
                String descricao = partes[0];
                String valor = partes[1];
                String data = partes[2];

                Date dataPagamento = sdf.parse(data);

                infos.add(new ReembolsoInfo(descricao, valor, dataPagamento, arquivo));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao processar o arquivo: " + nome);
                return;
            }
        }

        try {
            // 1. Criar planilha Excel com base no modelo
            File planilhaExcel = new File(pastaSelecionada, "planilha-temp.xlsx");
            gerarPlanilhaExcel(planilhaExcel, infos);

            // 2. Converter para PDF
            File planilhaPDF = new File(pastaSelecionada, nomePlanilhaPDFField.getText().trim());
            ArquivoUtil.converterExcelParaPDFComLibreOffice(planilhaExcel, planilhaPDF);

            // 3. Consolidar todos os PDFs
            infos.sort(Comparator.comparing(r -> r.dataPagamento));
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.setDestinationFileName(new File(pastaSelecionada, nomePDFConsolidadoField.getText().trim()).getAbsolutePath());

            for (ReembolsoInfo info : infos) {
                merger.addSource(info.arquivo);
            }

            merger.mergeDocuments(null);

            JOptionPane.showMessageDialog(this, "Reembolso gerado com sucesso!");
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao gerar reembolso.");
        }
    }

    private void gerarPlanilhaExcel(File arquivoSaida, List<ReembolsoInfo> infos) throws IOException {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    int linhaInicial = 6;

    try (InputStream modeloStream = getClass().getClassLoader().getResourceAsStream("com/meuprojeto/layout_preenchimento.xlsx");
         Workbook workbook = new XSSFWorkbook(modeloStream)) {

        if (modeloStream == null) {
            throw new FileNotFoundException("Modelo de planilha 'layout_preenchimento.xlsx' não encontrado no classpath.");
        }

        Sheet sheet = workbook.getSheetAt(0);
        infos.sort(Comparator.comparing(r -> r.dataPagamento));

        for (int i = 0; i < infos.size(); i++) {
            ReembolsoInfo info = infos.get(i);
            Row row = sheet.getRow(linhaInicial + i);
            if (row == null) row = sheet.createRow(linhaInicial + i);

            Cell cellDescricao = row.getCell(0);
            if (cellDescricao == null) cellDescricao = row.createCell(0);
            cellDescricao.setCellValue(info.descricao);

            Cell cellValor = row.getCell(1);
            if (cellValor == null) cellValor = row.createCell(1);
            try {
                cellValor.setCellValue(Double.parseDouble(info.valor.replace(",", ".")));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor inválido no arquivo: " + info.arquivo.getName(), e);
            }

            Cell cellData = row.getCell(2);
            if (cellData == null) cellData = row.createCell(2);
            cellData.setCellValue(sdf.format(info.dataPagamento));
        }

        try (FileOutputStream fos = new FileOutputStream(arquivoSaida)) {
            workbook.write(fos);
        }

    }
}


    private static class ReembolsoInfo {
        String descricao;
        String valor;
        Date dataPagamento;
        File arquivo;

        public ReembolsoInfo(String descricao, String valor, Date dataPagamento, File arquivo) {
            this.descricao = descricao;
            this.valor = valor;
            this.dataPagamento = dataPagamento;
            this.arquivo = arquivo;
        }
    }
}
