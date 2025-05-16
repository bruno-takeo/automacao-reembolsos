package com.meuprojeto.util;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import java.io.File;
import java.io.IOException;

public class PdfMerger {
    public static void mergePdfs(File[] pdfFiles, String outputFilePath) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(outputFilePath);
        for (File pdf : pdfFiles) {
            merger.addSource(pdf);
        }
        merger.mergeDocuments(null);
    }      
}
