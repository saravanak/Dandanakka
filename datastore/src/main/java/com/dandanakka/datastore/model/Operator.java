package com.dandanakka.datastore.model;

public enum Operator {
	EQUALS("="), NOT_EQUALS("!="), NOT_NULL("NOT_NULL"), IS_NULL("IS_NULL");

	private final String operator; // in kilograms

	Operator(String operator) {
		this.operator = operator;
	}

}
