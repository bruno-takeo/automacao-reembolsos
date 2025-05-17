package com.meuprojeto.model;

import java.io.File;
import java.util.Date;

public class ReembolsoInfo {
    public String descricao;
    public String valor;
    public Date dataPagamento;
    public File arquivo;

    public ReembolsoInfo(String descricao, String valor, Date dataPagamento, File arquivo) {
        this.descricao = descricao;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
        this.arquivo = arquivo;
    }
}