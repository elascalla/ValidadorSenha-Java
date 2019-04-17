package com.db1.validacaosenha.model;

public enum Complexidade {

    MUITO_FRACA("Muito Fraca"),
    FRACA("Fraca"),
    NORMAL("Normal"),
    FORTE("Forte"),
    MUITO_FORTE("Muito Forte");

    private final String descricao;

    Complexidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
