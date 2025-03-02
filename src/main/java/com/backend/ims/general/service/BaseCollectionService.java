package com.backend.ims.general.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public abstract class BaseCollectionService<T> {
  private static final Logger logger = LoggerFactory.getLogger(BaseCollectionService.class);

  protected abstract String getCollectionName();

  protected abstract MongoTemplate getMongoTemplate();

  public List<T> getAllItems() {
    return getMongoTemplate().findAll(getEntityClass(), getCollectionName());
  }

  public T getItemById(String id) {
    return getMongoTemplate().findById(id, getEntityClass(), getCollectionName());
  }

  public T saveItem(T clazz) {
    return getMongoTemplate().save(clazz, getCollectionName());
  }

  public void deleteItem(String id) {
    Query query = new Query(Criteria.where("_id").is(id));
    getMongoTemplate().remove(query, getCollectionName());
  }

  public void deleteAllUsers() {
    getMongoTemplate().remove(new Query(), getCollectionName());
  }

  public T getByFilter(String key, String value) {
    Query query = new Query(Criteria.where(key).is(value));

    return getMongoTemplate().findOne(query, getEntityClass(), getCollectionName());
  }

  public boolean isExist(String key, String value) {
    Query query = new Query(Criteria.where(key).is(value));
    return getMongoTemplate().exists(query, getEntityClass());
  }

  protected abstract Class<T> getEntityClass();
}
