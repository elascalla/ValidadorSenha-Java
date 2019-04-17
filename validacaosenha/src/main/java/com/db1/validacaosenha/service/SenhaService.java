package com.db1.validacaosenha.service;

import com.db1.validacaosenha.model.Complexidade;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SenhaService {

    public static int calculaForcaSenha(String senha){

        //total score of password
        int pontuacao = 0;
        int tam = senha.length();

        if(senha.length() < 0)
            return 0;

        pontuacao += somaPontuacao(senha, tam);

        pontuacao -= subtraiPontuacao(senha, tam);

        return pontuacao > 100 ? 100 : pontuacao < 0 ? 0 : pontuacao;
    }

    private static int somaPontuacao(String senha, int tam) {

        int pontuacao =0;
        int qtdUpperCase = 0;
        int qtdLowerCase = 0;
        int qtdNumbers = 0;

        if(senha != null && tam > 0
                && (senha.matches("(?=.*[a-z]).*")
                || senha.matches("(?=.*[A-Z]).*")
                || senha.matches("(?=.*[0-9]).*"))) {

            for (int i = 0; i < tam; i++) {
                if (Character.isUpperCase(senha.charAt(i))) qtdUpperCase++;
                if (Character.isLowerCase(senha.charAt(i))) qtdLowerCase++;
                if (Character.isDigit(senha.charAt(i))) qtdNumbers++;
            }
        }

        int qtdSymbols = retornaQuantidadeSimbolos(senha);

        pontuacao += tam * 4;

        pontuacao += retornaPontuacao(senha, qtdNumbers, "(?=.*[0-9]).*", 4);

        pontuacao += retornaPontuacao(senha, tam - qtdLowerCase, "(?=.*[a-z]).*", 2);

        pontuacao += retornaPontuacao(senha, tam - qtdUpperCase, "(?=.*[A-Z]).*", 2);

        pontuacao += retornaPontuacao(senha, qtdSymbols, "(?=.*[~!@#$%^&*()_-]).*", 6);

        pontuacao += numerosMeioOuSimbolos(senha, tam);

        pontuacao += minimoRequerido(qtdUpperCase, qtdLowerCase, qtdNumbers, tam, qtdSymbols);

        return pontuacao;
    }

    private static int subtraiPontuacao(String senha, int tam) {

        int pontuacao = 0;

        pontuacao += somenteNumeros(senha, tam);

        pontuacao += somenteLetras(senha, tam);

        pontuacao += caracteresRepitidos(senha, tam);

        pontuacao += caracteresConsecutivas(senha, "[A-Z]");

        pontuacao += caracteresConsecutivas(senha, "[a-z]");

        pontuacao += caracteresConsecutivas(senha, "[0-9]");

        pontuacao += caracteresSequenciais(senha, "[a-z]");

        pontuacao += caracteresSequenciais(senha, "[0-9]");

        pontuacao += simbolosSequenciais(senha);

        return pontuacao;
    }

    private static int minimoRequerido(int upperCase, int lowerCase, int numbers, int tam, int symbols) {

        if(upperCase > 0
                && lowerCase > 0
                && numbers > 0
                && symbols > 0
                && tam >= 8) {
            return 10;
        }

        return 0;
    }

    private static int retornaPontuacao(String senha, int numbers, String regex, int value) {
        if (senha.matches(regex)) {
            return numbers * value;
        }
        return 0;
    }

    private static int retornaQuantidadeSimbolos(String senha) {

        int symbols = 0;

        if(senha != null && senha.length() > 0
                && senha.matches("(?=.*[~!@#$%^&*()_-]).*")) {
            Pattern pattern = Pattern.compile("(?=.*[~!@#$%^&*()_-]).*");
            Matcher matcher = pattern.matcher(senha);

            while (matcher.find()) {
                symbols++;
            }
        }
        return symbols;
    }

    private static int somenteLetras(String senha, int tam) {
        if(senha.chars().allMatch(Character::isLetter)) {
            return tam;
        }
        return 0;
    }

    private static int somenteNumeros(String senha, int tam) {
        if(senha.chars().allMatch(Character::isDigit)) {
            return tam;
        }

        return 0;
    }

    public static int numerosMeioOuSimbolos(String senha, int tam) {
        int qtd = 0;
        if(tam < 2)
            return 0;

        for (char caracter : senha.substring(1, tam-1).toCharArray()) {
            if (String.valueOf(caracter).matches("[^0-9^A-Z^a-z]"))
                qtd += 1;
            if (String.valueOf(caracter).matches("[0-9]"))
                qtd += 1;
        }

        return qtd * 2;
    }

    private static int caracteresRepitidos(String senha, int tam) {

        Double repitidos = 0.0;
        int unicos;
        int qtdRepitido = 0;
        boolean existeCaracter = false;

        for (int i=0; i < tam; i++) {
            existeCaracter = false;
            for (int k=0; k < tam; k++) {
                if (senha.charAt(i) == senha.charAt(k) && i != k) {
                    existeCaracter = true;
                    repitidos += Math.abs(tam/(k-i));
                }
            }
            if (existeCaracter) {
                qtdRepitido ++;
                unicos = tam-qtdRepitido;
                repitidos = (unicos > 0) ? Math.ceil(repitidos/unicos) : Math.ceil(repitidos);
            }
        }

        return repitidos.intValue();
    }

    private static int caracteresConsecutivas(String senha, String regex) {

        int qtd = 0;
        char anterior = 0;

        for (char caracter : senha.toCharArray()) {
            if (String.valueOf(caracter).matches(regex) && String.valueOf(anterior).matches(regex))
                qtd ++;
            anterior = caracter;
        }

        return qtd*2;
    }

    private static int caracteresSequenciais(String senha, String regex) {

        int qtdSequencia = 0;
        char anterior = 0;
        char anterior2 = 0;
        for (char caracter : (regex.equals("[a-z]") ? senha.toLowerCase().toCharArray() : senha.toCharArray())) {
            if (Integer.valueOf(caracter-1).equals(Integer.valueOf(anterior))
                    && Integer.valueOf(caracter-2).equals(Integer.valueOf(anterior2))
                    && String.valueOf(caracter).matches(regex))
                qtdSequencia ++;
            anterior2 = anterior;
            anterior = caracter;
        }

        return qtdSequencia*3;
    }

    private static int simbolosSequenciais(String senha) {

        int qtdSequencial = 0;
        String simbolos = "!@#$%^&*()";

        String aux = "";
        for (char caracter : senha.toCharArray()) {
            aux += caracter;
            if (aux.length() > 3)
                aux = aux.substring(1, 4);
            if (simbolos.indexOf(aux) >= 0 && aux.length() == 3)
                qtdSequencial ++;
        }

        return qtdSequencial*3;
    }

    public static String calculaComplexidade(int pontuacao){

        String complexidade = Complexidade.MUITO_FRACA.getDescricao();

        if(new BigDecimal(pontuacao).divide(new BigDecimal("100")).compareTo(new BigDecimal("0.23")) > 0
                && new BigDecimal(pontuacao).divide(new BigDecimal("100")).compareTo(new BigDecimal("0.25")) <= 0) {
            complexidade = Complexidade.FRACA.getDescricao();
        } else if(new BigDecimal(pontuacao).divide(new BigDecimal("100")).compareTo(new BigDecimal("0.25")) > 0
                && new BigDecimal(pontuacao).divide(new BigDecimal("100")).compareTo(new BigDecimal("0.5")) <= 0) {
            complexidade = Complexidade.NORMAL.getDescricao();
        } else if(new BigDecimal(pontuacao).divide(new BigDecimal("100")).compareTo(new BigDecimal("0.5")) > 0
                && new BigDecimal(pontuacao).divide(new BigDecimal("100")).compareTo(new BigDecimal("0.75")) <= 0) {
            complexidade = Complexidade.FORTE.getDescricao();
        } else if(new BigDecimal(pontuacao).divide(new BigDecimal("100")).compareTo(new BigDecimal("0.75")) > 0) {
            complexidade = Complexidade.MUITO_FORTE.getDescricao();
        }

        return complexidade;
    }
}
