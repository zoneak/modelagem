package com.ak.modelagem.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ak.modelagem.domain.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {

	@Transactional(readOnly=true)
	public List<Estado> findAllByOrderByNome();
}
