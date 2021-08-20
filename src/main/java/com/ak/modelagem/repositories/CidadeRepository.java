package com.ak.modelagem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ak.modelagem.domain.Cidade;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {

}
