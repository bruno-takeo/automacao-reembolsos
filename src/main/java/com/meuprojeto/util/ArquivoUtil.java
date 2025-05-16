package com.meuprojeto.util;

import java.util.List;
import java.io.*;
import java.util.Arrays;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.pdfbox.pdmodel.*;
//import org.apache.pdfbox.pdmodel.common.PDRectangle;
//import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ArquivoUtil {

    /**
     * Copia um arquivo para o destino especificado.
     * 
     * @param origem  Arquivo original (deve existir).
     * @param destino Novo arquivo (não pode ser diretório).
     * @return true se a cópia foi bem-sucedida, false caso contrário.
     */
    public static boolean copiarArquivo(File origem, File destino) {
        if (origem == null || !origem.exists() || !origem.isFile()) {
            System.err.println("Arquivo de origem inválido.");
            return false;
        }

        if (destino == null) {
            System.err.println("Destino inválido.");
            return false;
        }

        // Verifica se o destino já existe para evitar sobrescrita acidental
        if (destino.exists()) {
            System.err.println("Arquivo de destino já existe: " + destino.getAbsolutePath());
            return false;
        }

        try (InputStream in = new FileInputStream(origem);
             OutputStream out = new FileOutputStream(destino)) {

            byte[] buffer = new byte[8192];
            int bytesLidos;

            while ((bytesLidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesLidos);
            }

            return true;

        } catch (IOException e) {
            System.err.println("Erro ao copiar arquivo:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Consolida dois arquivos PDF (fatura e comprovante) em um único PDF.
     *
     * @param fatura        Arquivo PDF da fatura.
     * @param comprovante   Arquivo PDF do comprovante.
     * @param destinoFinal  Arquivo PDF consolidado.
     * @return true se a fusão foi bem-sucedida, false caso contrário.
     */
    public static boolean consolidarPDFs(File fatura, File comprovante, File destinoFinal) {
        if (fatura == null || !fatura.exists() || !fatura.isFile()) {
            System.err.println("Fatura inválida.");
            return false;
        }

        if (comprovante == null || !comprovante.exists() || !comprovante.isFile()) {
            System.err.println("Comprovante inválido.");
            return false;
        }

        if (destinoFinal == null) {
            System.err.println("Destino inválido.");
            return false;
        }

        try {
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.setDestinationFileName(destinoFinal.getAbsolutePath());

            merger.addSource(fatura);
            merger.addSource(comprovante);

            merger.mergeDocuments(null);

            return true;

        } catch (IOException e) {
            System.err.println("Erro ao consolidar PDFs:");
            e.printStackTrace();
            return false;
        }
    }

    public static void converterExcelParaPDFComLibreOffice(File arquivoExcel, File destinoPDF) throws IOException, InterruptedException {
        File diretorioSaida = destinoPDF.getParentFile();

        // Caminho do LibreOffice (ajustar conforme o sistema operacional)
        String caminhoLibreOffice = detectarCaminhoLibreOffice();

        if (caminhoLibreOffice == null) {
            throw new IOException("LibreOffice não encontrado. Verifique se está instalado.");
        }

        List<String> comando = Arrays.asList(
            caminhoLibreOffice,
            "--headless",
            "--convert-to", "pdf",
            "--outdir", diretorioSaida.getAbsolutePath(),
            arquivoExcel.getAbsolutePath()
        );

        ProcessBuilder builder = new ProcessBuilder(comando);
        builder.redirectErrorStream(true);
        Process processo = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getInputStream()))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                System.out.println(linha);
            }
        }

        int exitCode = processo.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Erro ao converter planilha com LibreOffice. Código de saída: " + exitCode);
        }

        File pdfGerado = new File(diretorioSaida, arquivoExcel.getName().replace(".xlsx", ".pdf"));
        if (!pdfGerado.renameTo(destinoPDF)) {
            throw new IOException("Falha ao renomear o PDF gerado para: " + destinoPDF.getName());
        }
    }

    private static String detectarCaminhoLibreOffice() {
        // Caminhos padrão para sistemas operacionais
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "C:\\Program Files\\LibreOffice\\program\\soffice.exe";
        } else if (os.contains("mac")) {
            return "/Applications/LibreOffice.app/Contents/MacOS/soffice";
        } else {
            return "/usr/bin/libreoffice"; // Linux padrão
        }
    }

}
