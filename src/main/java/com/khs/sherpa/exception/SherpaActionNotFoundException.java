package com.khs.sherpa.exception;

public class SherpaActionNotFoundException extends SherpaRuntimeException {

	private static final long serialVersionUID = 9003606273103576947L;

	public SherpaActionNotFoundException() {
		super();
	}

	public SherpaActionNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SherpaActionNotFoundException(String arg0) {
		super(arg0);
	}

	public SherpaActionNotFoundException(Throwable arg0) {
		super(arg0);
	}

}
