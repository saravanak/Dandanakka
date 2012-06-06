package com.dandanakka.datastore.model;

public class Criteria {

	private String columnName;
	private Operator operator;
	private Object value;

	public Criteria(String columnName, Operator operator, Object value) {
		setColumnName(columnName) ;
		setOperator(operator) ;
		setValue(value) ;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
