package com.meuprojeto.util;

import java.io.IOException;

public class ExcelToPdfConverter {
    
    public static void convertExcelToPdf(String excelFilePath, String outputDir) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
            "soffice",
            "--headless",
            "--convert-to",
            "pdf",
            excelFilePath,
            "--outdir",
            outputDir
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Erro ao converter o arquivo Excel para PDF. Código de saída: " + exitCode);
        }
    }
}

