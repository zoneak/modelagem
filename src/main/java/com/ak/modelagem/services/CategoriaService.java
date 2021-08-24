package com.ak.modelagem.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.ak.modelagem.domain.Categoria;
import com.ak.modelagem.domain.Categoria;
import com.ak.modelagem.dto.CategoriaDTO;
import com.ak.modelagem.dto.CategoriaDTO;
import com.ak.modelagem.repositories.CategoriaRepository;
import com.ak.modelagem.services.exceptions.DataIntegrityCustomException;
import com.ak.modelagem.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public List<Categoria> findAll() {
		return categoriaRepository.findAll();
	}
	
	public Categoria find(Long id) {
		Optional<Categoria> obj = categoriaRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));
	}

	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return categoriaRepository.save(obj); 
	}

	public Categoria update(CategoriaDTO objDTO, Long id) {
		Categoria objDoBanco = find(id);
		updateData(objDoBanco, objDTO);
		return categoriaRepository.save(objDoBanco);
	}

	private void updateData(Categoria objDoBanco, CategoriaDTO objDTO) {
		objDoBanco.setNome(objDTO.getNome());
	}

	public void delete(Long id) {
		find(id);
		try {
			categoriaRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityCustomException("Não é possível excluir uma categoria que possui produtos associados");
		}
	}
	
	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String direction, String orderBy) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return categoriaRepository.findAll(pageRequest);
	}
	
	public Categoria fromDTO(CategoriaDTO objDTO) {
		return new Categoria(objDTO.getId(), objDTO.getNome());
	}

}
