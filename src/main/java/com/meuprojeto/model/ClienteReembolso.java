package com.meuprojeto.model;

public class ClienteReembolso {
    
    private String nomeEmpresa;
    private String caminhoBase;

    public ClienteReembolso() {
    }

    public ClienteReembolso(String nomeEmpresa, String caminhoBase) {
        this.nomeEmpresa = nomeEmpresa;
        this.caminhoBase = caminhoBase;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getCaminhoBase() {
        return caminhoBase;
    }

    public void setCaminhoBase(String caminhoBase) {
        this.caminhoBase = caminhoBase;
    }

    @Override
    public String toString() {
        return nomeEmpresa; // Usado para exibir no JComboBox, por exemplo
    }
}

