package com.dandanakka.datastore.model;

import java.util.List;

public class PaginatedResult<T> {

	private int noOfRecords;
	private List<T> results;

	public int getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(int noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

}
