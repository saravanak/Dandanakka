package com.dandanakka.datastore.model;

import java.util.ArrayList;
import java.util.List;

public class Query {

	private List<Criteria> criterias;

	public List<Criteria> getCriterias() {
		return criterias;
	}
	
	public void clearCriterias() {
		criterias.clear() ;
	}

	
	
	public void addCriteria(String columnName, Operator operator, Object value) {
		if(criterias == null) {
			criterias = new ArrayList<>() ;			
		}
		criterias.add(new Criteria(columnName, operator, value)) ;
	}

}
