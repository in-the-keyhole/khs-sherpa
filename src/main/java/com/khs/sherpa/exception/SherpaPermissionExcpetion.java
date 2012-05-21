package com.khs.sherpa.exception;

public class SherpaPermissionExcpetion extends SherpaRuntimeException {

	private static final long serialVersionUID = 7494012371178202501L;

	public SherpaPermissionExcpetion() {
		super();
	}

	public SherpaPermissionExcpetion(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SherpaPermissionExcpetion(String arg0) {
		super(arg0);
	}

	public SherpaPermissionExcpetion(Throwable arg0) {
		super(arg0);
	}

}
