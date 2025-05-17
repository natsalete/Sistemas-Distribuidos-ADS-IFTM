package com.sd.prj_petShop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sd.prj_petShop.models.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

}
