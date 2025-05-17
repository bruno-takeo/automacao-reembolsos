package com.meuprojeto.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.meuprojeto.model.ReembolsoInfo;

public class ReembolsoService {

    private static final String PADRAO_ARQUIVO = ".*_\\d+(\\.\\d{2})?_\\d{2}-\\d{2}-\\d{4}\\.pdf";

    public static List<ReembolsoInfo> lerArquivosValidos(File pasta) throws ParseException {
        File[] arquivos = pasta.listFiles((dir, name) -> name.matches(PADRAO_ARQUIVO));
        List<ReembolsoInfo> infos = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        if (arquivos != null) {
            for (File arquivo : arquivos) {
                try {
                    String nome = arquivo.getName().replace(".pdf", "");
                    String[] partes = nome.split("_");
                    if (partes.length != 3) {
                        System.err.println("Arquivo ignorado por nome inválido: " + nome);
                        continue;
                    }
                    ReembolsoInfo info = new ReembolsoInfo(partes[0], partes[1], sdf.parse(partes[2]), arquivo);
                    infos.add(info);
                } catch (Exception e) {
                    System.err.println("Erro ao processar arquivo: " + arquivo.getName() + " - " + e.getMessage());
                }
            }
        }

        return infos;
    }

    public static void consolidarPDFsOrdenados(List<ReembolsoInfo> infos, File destinoPDF) throws IOException {
        infos.sort(Comparator.comparing(r -> r.dataPagamento));

        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(destinoPDF.getAbsolutePath());

        for (ReembolsoInfo info : infos) {
            merger.addSource(info.arquivo);
        }

        merger.mergeDocuments(null);
    }
}
