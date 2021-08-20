package com.ak.modelagem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ak.modelagem.domain.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

}
