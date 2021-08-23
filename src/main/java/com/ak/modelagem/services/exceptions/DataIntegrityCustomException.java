package com.ak.modelagem.services.exceptions;

public class DataIntegrityCustomException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DataIntegrityCustomException(String msg) {
		super(msg);
	}

	public DataIntegrityCustomException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
