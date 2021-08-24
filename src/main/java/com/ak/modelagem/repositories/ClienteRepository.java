package com.ak.modelagem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ak.modelagem.domain.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	@Transactional(readOnly = true) // não necessita ser envolvida como transação, torna mais rápida e diminui 'locking'
	Cliente findByEmail(String email);
}
