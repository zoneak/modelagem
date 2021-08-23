package com.ak.modelagem.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.ak.modelagem.domain.Cliente;
import com.ak.modelagem.dto.ClienteDTO;
import com.ak.modelagem.repositories.ClienteRepository;
import com.ak.modelagem.services.exceptions.DataIntegrityCustomException;
import com.ak.modelagem.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	public Cliente find(Long id) {
		Optional<Cliente> obj = clienteRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}
	
	public Cliente update(ClienteDTO objDTO, Long id) {
		Cliente objDoBanco = find(id);
		updateData(objDoBanco, objDTO);
		return clienteRepository.save(objDoBanco);
	}

	private void updateData(Cliente objDoBanco, ClienteDTO objDTO) {
		objDoBanco.setNome(objDTO.getNome());
		objDoBanco.setEmail(objDTO.getEmail());
	}

	public void delete(Long id) {
		find(id);
		try {
			clienteRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityCustomException("Não é possível excluir pois há entidades relacionadas");
		}
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String direction, String orderBy) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDTO) {
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null);
	}
}
