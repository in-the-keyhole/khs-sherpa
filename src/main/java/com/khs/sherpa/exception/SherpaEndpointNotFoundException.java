package com.khs.sherpa.exception;

public class SherpaEndpointNotFoundException extends SherpaRuntimeException {

	private static final long serialVersionUID = -4432140915148653751L;

	public SherpaEndpointNotFoundException() {
		super();
	}

	public SherpaEndpointNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SherpaEndpointNotFoundException(String arg0) {
		super(arg0);
	}

	public SherpaEndpointNotFoundException(Throwable arg0) {
		super(arg0);
	}

}
