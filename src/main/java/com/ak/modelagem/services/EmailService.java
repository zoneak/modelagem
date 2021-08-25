package com.ak.modelagem.services;

import org.springframework.mail.SimpleMailMessage;

import com.ak.modelagem.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
}
