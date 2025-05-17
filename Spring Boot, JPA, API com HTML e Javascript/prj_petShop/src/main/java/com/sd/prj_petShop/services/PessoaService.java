package com.sd.prj_petShop.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sd.prj_petShop.models.Pessoa;
import com.sd.prj_petShop.repositories.PessoaRepository;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }   


    public List<Pessoa> getPessoas() {
        return pessoaRepository.findAll();
    }

    public Pessoa savPessoa(Pessoa pessoa) {
        return pessoaRepository.save(pessoa);
    }
}
