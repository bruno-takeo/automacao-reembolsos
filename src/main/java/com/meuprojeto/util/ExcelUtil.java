package com.meuprojeto.util;

import com.meuprojeto.model.ReembolsoInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExcelUtil {

    public static void gerarPlanilhaExcel(File arquivoSaida, List<ReembolsoInfo> infos, String nomeEmpresa, String periodo) throws IOException {
        int linhaInicial = 10;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try (InputStream modeloStream = ExcelUtil.class.getClassLoader()
                .getResourceAsStream("com/meuprojeto/layout_preenchimento.xlsx")) {

            if (modeloStream == null) {
                throw new FileNotFoundException("Modelo de planilha 'layout_preenchimento.xlsx' não encontrado no classpath.");
            }

            try (Workbook workbook = new XSSFWorkbook(modeloStream)) {
                Sheet sheet = workbook.getSheetAt(0);

                // B6 - Empresa
                Row rowEmpresa = sheet.getRow(6); // Linha 6
                if (rowEmpresa == null) rowEmpresa = sheet.createRow(6);
                Cell cellEmpresa = rowEmpresa.getCell(1); // Coluna B (índice 1)
                if (cellEmpresa == null) cellEmpresa = rowEmpresa.createCell(1);
                cellEmpresa.setCellValue(nomeEmpresa != null ? nomeEmpresa : "");

                // B7 - Período
                Row rowPeriodo = sheet.getRow(7); // Linha 7
                if (rowPeriodo == null) rowPeriodo = sheet.createRow(7);
                Cell cellPeriodo = rowPeriodo.getCell(1); // Coluna B (índice 1)
                if (cellPeriodo == null) cellPeriodo = rowPeriodo.createCell(1);
                cellPeriodo.setCellValue(periodo != null ? periodo : "");

                preencherPlanilhaComReembolsos(sheet, infos, linhaInicial, sdf);

                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                evaluator.evaluateAll();
                workbook.setForceFormulaRecalculation(true);

                try (FileOutputStream fos = new FileOutputStream(arquivoSaida)) {
                    workbook.write(fos);
                }
            }
        }
    }


    private static void preencherPlanilhaComReembolsos(Sheet sheet, List<ReembolsoInfo> infos, int linhaModeloIndex, SimpleDateFormat sdf) {
        infos.sort(Comparator.comparing(r -> r.dataPagamento));
        int quantidadeInsercoes = infos.size();

        Row linhaModeloOriginal = sheet.getRow(linhaModeloIndex);
        if (linhaModeloOriginal == null) {
            throw new IllegalStateException("Linha modelo não encontrada na linha " + (linhaModeloIndex + 1));
        }

        // Cópias seguras do estilo e tipo das células da linha modelo
        List<CellStyle> estilosModelo = new ArrayList<>();
        List<CellType> tiposModelo = new ArrayList<>();
        for (Cell cell : linhaModeloOriginal) {
            estilosModelo.add(cell.getCellStyle());
            tiposModelo.add(cell.getCellType());
        }

        int linhaRodapeIndex = linhaModeloIndex + 1;
        if (quantidadeInsercoes > 1) {
            sheet.shiftRows(linhaRodapeIndex, sheet.getLastRowNum(), quantidadeInsercoes - 1, true, false);
        }

        for (int i = 0; i < quantidadeInsercoes; i++) {
            int novaLinhaIndex = linhaModeloIndex + i;
            Row novaLinha = sheet.createRow(novaLinhaIndex);

            for (int col = 0; col < estilosModelo.size(); col++) {
                Cell cell = novaLinha.createCell(col, tiposModelo.get(col)); // agora válido
                cell.setCellStyle(estilosModelo.get(col));
            }

            ReembolsoInfo info = infos.get(i);

            Cell cellData = novaLinha.getCell(0);
            if (cellData == null) cellData = novaLinha.createCell(0);
            cellData.setCellValue(sdf.format(info.dataPagamento));

            Cell cellDescricao = novaLinha.getCell(1);
            if (cellDescricao == null) cellDescricao = novaLinha.createCell(1);
            cellDescricao.setCellValue(info.descricao);

            Cell cellValor = novaLinha.getCell(2);
            if (cellValor == null) cellValor = novaLinha.createCell(2);
            try {
                cellValor.setCellValue(Double.parseDouble(info.valor.replace(",", ".")));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor inválido no arquivo: " + info.arquivo.getName(), e);
            }
        }

        // Atualiza a fórmula do rodapé para refletir o novo intervalo da coluna de valores (C)
        int linhaInicial = linhaModeloIndex;
        int linhaFinal = linhaModeloIndex + infos.size() - 1;

        Row linhaRodape = sheet.getRow(linhaFinal + 1);
        if (linhaRodape != null) {
            Cell celulaFormula = linhaRodape.getCell(2); // coluna C = índice 2
            if (celulaFormula == null) {
                celulaFormula = linhaRodape.createCell(2);
            }

            // Construção da fórmula no formato Excel
            String formula = String.format("SUM(C%d:C%d)", linhaInicial + 1, linhaFinal + 1); // +1 pois Excel é 1-based
            celulaFormula.setCellFormula(formula);
        }

        sheet.autoSizeColumn(1); // Coluna B (Descrição)
    }




    private static void copiarEstiloLinha(Row origem, Row destino) {
        Workbook workbook = origem.getSheet().getWorkbook();

        for (int i = 0; i < origem.getLastCellNum(); i++) {
            // Garante que a célula de origem exista
            Cell cellOrigem = origem.getCell(i);
            if (cellOrigem == null) {
                cellOrigem = origem.createCell(i);
            }

            CellStyle estiloOrigem = cellOrigem.getCellStyle();
            CellStyle estiloNovo = workbook.createCellStyle();
            estiloNovo.cloneStyleFrom(estiloOrigem);

            Cell cellDestino = destino.createCell(i);
            cellDestino.setCellStyle(estiloNovo);
        }
    }

    private static void criarLinhaClonadaComEstilo(Sheet sheet, Row linhaModelo, int novaLinhaIndex) {
        Row novaLinha = sheet.createRow(novaLinhaIndex);
        novaLinha.setHeight(linhaModelo.getHeight());

        Workbook workbook = sheet.getWorkbook();

        for (int i = 0; i < linhaModelo.getLastCellNum(); i++) {
            Cell cellModelo = linhaModelo.getCell(i);
            Cell cellNova = novaLinha.createCell(i);

            if (cellModelo != null) {
                // Clona o estilo da célula modelo
                CellStyle estilo = cellModelo.getCellStyle();
                if (estilo != null) {
                    cellNova.setCellStyle(estilo); // ⚠️ usa o mesmo estilo (evita excesso de estilos no workbook)
                }

                // Copia o conteúdo, se houver
                switch (cellModelo.getCellType()) {
                    case STRING:
                        cellNova.setCellValue(cellModelo.getStringCellValue());
                        break;
                    case NUMERIC:
                        cellNova.setCellValue(cellModelo.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        cellNova.setCellValue(cellModelo.getBooleanCellValue());
                        break;
                    case FORMULA:
                        cellNova.setCellFormula(cellModelo.getCellFormula());
                        break;
                    default:
                        break;
                }
            } else {
                // Caso não exista célula na linha modelo, criamos célula em branco com estilo da coluna
                Row rowTemp = sheet.getRow(novaLinhaIndex);
                Cell cellTemp = rowTemp.getCell(i);
                if (cellTemp == null) cellTemp = rowTemp.createCell(i);

                // Aplica estilo da coluna (caso tenha sido configurado via template)
                Cell modeloEstilo = linhaModelo.createCell(i);
                if (modeloEstilo.getCellStyle() != null) {
                    cellTemp.setCellStyle(modeloEstilo.getCellStyle());
                }
            }
        }
    }



    
}