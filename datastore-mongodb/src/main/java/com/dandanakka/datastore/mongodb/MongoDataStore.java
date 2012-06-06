package com.dandanakka.datastore.mongodb;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.omg.CORBA.SystemException;

import com.dandanakka.core.util.ConfigUtil;
import com.dandanakka.datastore.DataStore;
import com.dandanakka.datastore.exception.DataStoreException;
import com.dandanakka.datastore.model.Criteria;
import com.dandanakka.datastore.model.PaginatedResult;
import com.dandanakka.datastore.model.Query;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteResult;

public class MongoDataStore extends DataStore {

	private DB getMongoDB() throws DataStoreException {
		DB db = null;
		try {
			Mongo mongo = new Mongo(
					ConfigUtil.getCongiguration("mongodb.host"),
					ConfigUtil.getCongigurationAsInt("mongodb.port"));
			db = mongo.getDB((getApplication() == null) ? "root" : "site"
					+ getApplication());
		} catch (UnknownHostException e) {
			throw new DataStoreException("please check MongoDB instance", e);
		} catch (MongoException e) {
			throw new DataStoreException("please check MongoDB instance", e);
		} catch (SystemException e) {
			throw new DataStoreException("please check MongoDB instance", e);
		} catch (IOException e) {
			throw new DataStoreException("please check config file instance", e);
		}
		return db;
	}

	@Override
	public String createData(String schemaName, Map<String, Object> data)
			throws DataStoreException {
		DB db = getMongoDB();
		BasicDBObject dbObject = new BasicDBObject(data);
		WriteResult result = db.getCollection(schemaName).insert(dbObject);
		return dbObject.getString("_id");
	}

	@Override
	public int updateData(String schemaName, Map<String, Object> data)
			throws DataStoreException {

		DB db = getMongoDB();
		BasicDBObject dbObject = new BasicDBObject(data);
		dbObject.put(getIdColumnName(),
				getIdValue(dbObject.get(getIdColumnName()).toString()));
		BasicDBObject queryObject = new BasicDBObject("_id",
				getIdValue(dbObject.get(getIdColumnName()).toString()));
		WriteResult result = db.getCollection(schemaName).update(queryObject,
				dbObject);

		return result.getN();
	}

	@Override
	public Map<String, Object> getDataMap(String schemaName, String id)
			throws DataStoreException {
		if (id == null) {
			return null;
		}
		DB db = getMongoDB();
		BasicDBObject queryObject = new BasicDBObject("_id", getIdValue(id));
		return (Map<String, Object>) db.getCollection(schemaName).findOne(
				queryObject);
	}

	@Override
	public PaginatedResult getDataList(String schemaName,
			Map<String, Object> data, Integer pageNumber, Integer pageSize)
			throws DataStoreException {
		PaginatedResult paginatedResult = null;
		List<Map<String, Object>> list = null;
		DB db = getMongoDB();
		BasicDBObject queryObject = new BasicDBObject(data);
		DBCursor dbCursor = db.getCollection(schemaName).find(queryObject);

		if (pageNumber != null) {
			dbCursor = dbCursor.batchSize(pageSize)
					.skip((pageNumber - 1) * pageSize).limit(pageSize);
		}

		list = new ArrayList<Map<String, Object>>();
		while (dbCursor.hasNext()) {
			list.add((Map<String, Object>) dbCursor.next());
		}
		if (list != null) {
			paginatedResult = new PaginatedResult();
			paginatedResult.setResults(list);
			paginatedResult.setNoOfRecords(dbCursor.count());
		}

		return paginatedResult;
	}

	@Override
	public PaginatedResult getDataList(String schemaName, Query query,
			Integer pageNumber, Integer pageSize) throws DataStoreException {
		PaginatedResult paginatedResult = null;
		List<Map<String, Object>> list = null;
		DB db = getMongoDB();

		BasicDBObject queryObject = new BasicDBObject();

		QueryBuilder qb = new QueryBuilder();

		List<Criteria> criterias = query.getCriterias();
		for (Criteria criteria : criterias) {

			switch (criteria.getOperator()) {
			case IS_NULL:
				qb.put(criteria.getColumnName()).is(null);
				break;
			case EQUALS:
				queryObject.put(criteria.getColumnName(), criteria.getValue());
				break;
			}
		}

		queryObject.putAll(qb.get());

		DBCursor dbCursor = db.getCollection(schemaName).find(queryObject);

		if (pageNumber != null) {
			dbCursor = dbCursor.batchSize(pageSize)
					.skip((pageNumber - 1) * pageSize).limit(pageSize);
		}

		list = new ArrayList<Map<String, Object>>();
		while (dbCursor.hasNext()) {
			list.add((Map<String, Object>) dbCursor.next());
		}
		if (list != null) {
			paginatedResult = new PaginatedResult();
			paginatedResult.setResults(list);
			paginatedResult.setNoOfRecords(dbCursor.count());
		}

		return paginatedResult;
	}

	@Override
	public String getIdColumnName() {

		return "_id";
	}

	@Override
	public boolean deleteData(String schemaName, String id)
			throws DataStoreException {
		if (id == null) {
			return false;
		}
		DB db = getMongoDB();
		return db.getCollection(schemaName)
				.remove(new BasicDBObject("_id", getIdValue(id))).getError() == null;

	}

	private Object getIdValue(String id) {
		Object idValue = id;
		try {
			idValue = new ObjectId(id);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return idValue;
	}

}
