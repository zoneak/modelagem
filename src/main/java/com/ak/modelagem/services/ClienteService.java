package com.ak.modelagem.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ak.modelagem.domain.Cidade;
import com.ak.modelagem.domain.Cliente;
import com.ak.modelagem.domain.Endereco;
import com.ak.modelagem.domain.enums.Perfil;
import com.ak.modelagem.domain.enums.TipoCliente;
import com.ak.modelagem.dto.ClienteDTO;
import com.ak.modelagem.dto.ClienteNewDTO;
import com.ak.modelagem.repositories.ClienteRepository;
import com.ak.modelagem.repositories.EnderecoRepository;
import com.ak.modelagem.security.UserSpringSecurity;
import com.ak.modelagem.services.exceptions.AuthorizationException;
import com.ak.modelagem.services.exceptions.DataIntegrityCustomException;
import com.ak.modelagem.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private ImageService imageService;

	@Value("${img.prefix.client.profile}")
	private String prefix; 
	
	@Value("${img.profile.size}")
	private Integer size;
	
	public Cliente find(Long id) {
		UserSpringSecurity user = UserService.authenticated();
		
		if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = clienteRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto n??o encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	public Cliente findByEmail(String email) {
		UserSpringSecurity user = UserService.authenticated();
		if (user == null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
			throw new AuthorizationException("Acesso negado");
		}

		Cliente obj = clienteRepository.findByEmail(email);
		if (obj == null) {
			throw new ObjectNotFoundException(
					"Objeto n??o encontrado! Id: " + user.getId() + ", Tipo: " + Cliente.class.getName());
		}
		return obj;
	}
	
	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = clienteRepository.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
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
			throw new DataIntegrityCustomException("N??o ?? poss??vel excluir pois h?? pedidos relacionados");
		}
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String direction, String orderBy) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDTO) {
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO objDTO) {
		Cliente cli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj(), TipoCliente.toEnum(objDTO.getTipo()), bCryptPasswordEncoder.encode(objDTO.getSenha()));
		Cidade cid = new Cidade(objDTO.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDTO.getLogradouro(), objDTO.getNumero(), objDTO.getComplemento(), objDTO.getBairro(), objDTO.getCep(), cli, cid);
		
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDTO.getTelefone1());
		
		if (objDTO.getTelefone2()!=null) {
			cli.getTelefones().add(objDTO.getTelefone2());
		}
		
		if (objDTO.getTelefone3()!=null) {
			cli.getTelefones().add(objDTO.getTelefone3());
		}
		
		return cli;
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSpringSecurity user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		
		BufferedImage jpgImageFromFile = imageService.getJpgImageFromFile(multipartFile);
		jpgImageFromFile = imageService.cropSquare(jpgImageFromFile);
		jpgImageFromFile = imageService.resize(jpgImageFromFile, size);
		
		String filename = prefix + user.getId() + ".jpg";
		
		return s3Service.uploadFile(imageService.getInputStream(jpgImageFromFile, "jpg"), filename, "image");
	}

}
