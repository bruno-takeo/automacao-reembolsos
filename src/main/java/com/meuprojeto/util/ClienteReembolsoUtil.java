package com.meuprojeto.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuprojeto.model.ClienteReembolso;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ClienteReembolsoUtil {
    private static final Path arquivoClientes = Paths.get(System.getProperty("user.home"), ".automacao-reembolsos", "clientes.json");
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<ClienteReembolso> carregarClientes() {
        if (Files.exists(arquivoClientes)) {
            try {
                return mapper.readValue(arquivoClientes.toFile(), new TypeReference<List<ClienteReembolso>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public static void salvarClientes(List<ClienteReembolso> clientes) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivoClientes.toFile(), clientes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void adicionarOuAtualizarCliente(ClienteReembolso novo) {
        List<ClienteReembolso> clientes = carregarClientes();
        Optional<ClienteReembolso> existente = clientes.stream()
                .filter(c -> c.getNomeEmpresa().equalsIgnoreCase(novo.getNomeEmpresa()))
                .findFirst();

        if (existente.isPresent()) {
            clientes.remove(existente.get());
        }
        clientes.add(novo);
        salvarClientes(clientes);
    }

    public static void removerCliente(String nome) {
        List<ClienteReembolso> clientes = carregarClientes();
        clientes = clientes.stream()
                .filter(c -> !c.getNomeEmpresa().equalsIgnoreCase(nome))
                .collect(Collectors.toList());
        salvarClientes(clientes);
    }

    public static Optional<ClienteReembolso> buscarPorNome(String nome) {
        return carregarClientes().stream()
                .filter(c -> c.getNomeEmpresa().equalsIgnoreCase(nome))
                .findFirst();
    }
}
