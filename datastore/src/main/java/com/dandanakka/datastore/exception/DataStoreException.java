package com.dandanakka.datastore.exception;

public class DataStoreException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataStoreException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataStoreException(String message) {
		super(message);
	}

}
