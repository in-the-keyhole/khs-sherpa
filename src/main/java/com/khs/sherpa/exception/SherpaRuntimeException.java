package com.khs.sherpa.exception;

public class SherpaRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 3997371687805128093L;

	public SherpaRuntimeException() {
		super();
	}

	public SherpaRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SherpaRuntimeException(String arg0) {
		super(arg0);
	}

	public SherpaRuntimeException(Throwable arg0) {
		super(arg0);
	}

}
