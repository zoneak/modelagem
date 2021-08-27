package com.ak.modelagem.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ak.modelagem.domain.Cliente;
import com.ak.modelagem.repositories.ClienteRepository;
import com.ak.modelagem.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private EmailService emailService;
	
	private Random random = new Random();
	
	public void sendNewPassword(String email) {
		
		Cliente cliente = clienteRepository.findByEmail(email);
		
		if (cliente == null) {
			throw new ObjectNotFoundException("Email não encontrado");
		}
		
		String newPass = newPassword();
		cliente.setSenha(encoder.encode(newPass));
		clienteRepository.save(cliente);
		
		emailService.sendNewPasswordEmail(cliente, newPass);
	}

	private String newPassword() {
		char[] vet = new char[10];
		for (int i = 0; i < 10; i++) {
			vet[i] = randomChar();
		}
		return new String(vet);
	}

	// https://unicode-table.com
	private char randomChar() {
		int opt = random.nextInt(3);
		if (opt == 0) { // gera um digito
			return (char) (48 + random.nextInt(10)); // 48 = unicode do dígito 0. Ex.: 48 + 3 = 51 = unicode do dígito 3 
		} else if (opt == 1) { // gera letra maiuscula
			return (char) (65 + random.nextInt(26)); // 65 = unidade da letra A. Ex.: 65 + 4 = 69 = unicode da letra E
		} else { // gera letra minuscula
			return (char) (97 + random.nextInt(26)); // 97 = unicode da letra a. Ex.: 97 + 4 = 101 = unicode da letra e 
		}
	}
}
