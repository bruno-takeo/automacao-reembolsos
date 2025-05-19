package com.meuprojeto.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class InicializadorApp {

    public static void inicializar() {
        Path pasta = Paths.get(System.getProperty("user.home"), ".automacao-reembolsos");
        Path arquivoClientes = pasta.resolve("clientes.json");

        try {
            if (Files.notExists(pasta)) {
                Files.createDirectories(pasta);
                System.out.println("Pasta de dados criada: " + pasta.toString());
            }

            if (Files.notExists(arquivoClientes)) {
                Files.write(arquivoClientes, "[]".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                System.out.println("Arquivo clientes.json criado: " + arquivoClientes.toString());
            }

        } catch (IOException e) {
            System.err.println("Erro ao inicializar diretórios ou arquivos: " + e.getMessage());
        }
    }
}
