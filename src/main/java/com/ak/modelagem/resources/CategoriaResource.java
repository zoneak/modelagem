package com.ak.modelagem.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ak.modelagem.domain.Categoria;
import com.ak.modelagem.dto.CategoriaDTO;
import com.ak.modelagem.services.CategoriaService;

@RestController
@RequestMapping(value="/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaService categoriaService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<CategoriaDTO>> findAll() {
		List<Categoria> lista = categoriaService.findAll();
		List<CategoriaDTO> listaDTO = lista.stream().map(obj -> new CategoriaDTO(obj)).collect(Collectors.toList()); // Transforma em lista de DTO
		return ResponseEntity.ok().body(listaDTO);	
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public ResponseEntity<Page<CategoriaDTO>> findPage(
			@RequestParam(value = "page", defaultValue = "0") Integer page, 
			@RequestParam(value = "linesPerPage", defaultValue = "24") Integer linesPerPage, 
			@RequestParam(value = "direction", defaultValue = "ASC") String direction, 
			@RequestParam(value = "orderBy", defaultValue = "nome") String orderBy) {
		Page<Categoria> lista = categoriaService.findPage(page, linesPerPage, direction, orderBy);
		Page<CategoriaDTO> listaDTO = lista.map(obj -> new CategoriaDTO(obj));
		return ResponseEntity.ok().body(listaDTO);	
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ResponseEntity<Categoria> find(@PathVariable Long id) {
		Categoria categoria = categoriaService.find(id);
		return ResponseEntity.ok().body(categoria);	
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> insert(@Valid @RequestBody CategoriaDTO objDTO) {
		Categoria obj = categoriaService.fromDTO(objDTO);
		obj = categoriaService.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri(); // pega a URI do novo recurso inserido
		return ResponseEntity.created(uri).build();
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> update(@Valid @RequestBody CategoriaDTO objDTO, @PathVariable Long id) {
		categoriaService.update(objDTO, id);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		categoriaService.delete(id);
		return ResponseEntity.noContent().build();	
	}

}
