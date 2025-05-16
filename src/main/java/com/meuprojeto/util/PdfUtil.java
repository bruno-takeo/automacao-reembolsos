package com.meuprojeto.util;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;

public class PdfUtil {
    public static void consolidarPdfs(File fatura, File comprovante, File destino) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.addSource(fatura);
        merger.addSource(comprovante);
        merger.setDestinationFileName(destino.getAbsolutePath());
        merger.mergeDocuments(null);
    }
}
