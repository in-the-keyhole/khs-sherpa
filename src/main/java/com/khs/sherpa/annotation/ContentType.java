package com.khs.sherpa.annotation;

public enum ContentType {
	JSON("application/json"),
	XML("application/xml"),
	HTML("text/html"),
	PDF("application/pdf"),
	TEXT("text/plain"),
	GIF("image/gif"),
	JPEG("image/jpeg"),
	
	;
	
	public String type = null;
	ContentType(String type) {
		this.type = type;
	}
}
