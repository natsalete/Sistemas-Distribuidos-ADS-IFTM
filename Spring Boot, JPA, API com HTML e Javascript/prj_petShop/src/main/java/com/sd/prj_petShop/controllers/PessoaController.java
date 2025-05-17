package com.sd.prj_petShop.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sd.prj_petShop.models.Pessoa;
import com.sd.prj_petShop.services.PessoaService;

@RestController
@RequestMapping("/api/pessoas")
@CrossOrigin(origins = "*") // Apply CORS globally to all methods in this controller
public class PessoaController {
    
    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public List<Pessoa> getAllPessoas() { 
        return pessoaService.getPessoas();
    }

    @PostMapping
    public ResponseEntity<Pessoa> savePessoa(@RequestBody Pessoa pessoa) {
        try {
            // Ensure we don't try to set an ID - let the database generate it
            pessoa.setIdPessoa(null);
            Pessoa savedPessoa = pessoaService.savPessoa(pessoa);
            return new ResponseEntity<>(savedPessoa, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}