package com.db1.validacaosenha.model;

public class Senha {

    private String senha;
    private int pontuacao = 0;
    private String complexidade;

    public Senha() {
    }

    public Senha(String senha, int pontuacao, String complexidade) {
        this.senha = senha;
        this.pontuacao = pontuacao;
        this.complexidade = complexidade;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getComplexidade() {
        return complexidade;
    }

    public void setComplexidade(String complexidade) {
        this.complexidade = complexidade;
    }
}
