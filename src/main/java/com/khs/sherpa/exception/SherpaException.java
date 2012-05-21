package com.khs.sherpa.exception;

public class SherpaException extends Exception {

	private static final long serialVersionUID = -9073262409489839045L;

	public SherpaException() {
		super();
	}

	public SherpaException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SherpaException(String arg0) {
		super(arg0);
	}

	public SherpaException(Throwable arg0) {
		super(arg0);
	}

}
