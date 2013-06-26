package com.petrsmid.bitexchange.net;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;


public enum JsonCodec {
	INSTANCE;

	private ObjectMapper mapper = new ObjectMapper();

	private JsonCodec() {}
	
	public String toJson(Object o) throws JsonParsingException {
		try {
			return mapper.writeValueAsString(o);
		} catch (IOException e) {
			throw new JsonParsingException(e);
		}
	}
	
	public <T> T parseJson(String s, Class<T> clazz) throws JsonParsingException {
		try {
			return mapper.readValue(s, clazz);
		} catch (IOException e) {
			throw new JsonParsingException(e);
		}
	}

}
