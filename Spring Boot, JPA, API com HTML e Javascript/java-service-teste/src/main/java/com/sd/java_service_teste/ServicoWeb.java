package com.sd.java_service_teste;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class ServicoWeb {
    private static final String conteudo = "Olá, %s!";

    private final AtomicLong contador = new AtomicLong();

    @GetMapping("/pessoa")
    public Pessoa getPessoa(@RequestParam(required = false, defaultValue = "Mundo") String nome) {
        return new Pessoa(contador.incrementAndGet(), String.format(conteudo, nome));
    }
}
