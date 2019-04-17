package com.db1.validacaosenha.controller;

import com.db1.validacaosenha.model.Complexidade;
import com.db1.validacaosenha.model.Senha;
import com.db1.validacaosenha.service.SenhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping("/")
public class ValidacaoController {

    @Autowired
    private SenhaService senhaService;

    @GetMapping
    public String teste() {
        return "TESTE";
    }

    @PostMapping
    public Senha validarSenha(@RequestBody Senha senha) {

        if(senha.getSenha() == null || senha.getSenha().length() == 0) {
            senha.setComplexidade(null);
            senha.setPontuacao(0);
        } else {

            senha.setPontuacao(senhaService.calculaForcaSenha(senha.getSenha()));
            senha.setComplexidade(senhaService.calculaComplexidade(senha.getPontuacao()));
        }

        return senha;
    }
}
