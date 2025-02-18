package com.backend.ims.data.user.impl.accessor;

import com.backend.ims.data.user.api.model.User;
import com.backend.ims.general.service.BaseCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserAccessor extends BaseCollectionService<User> {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  protected String getCollectionName() {
    return mongoTemplate.getCollectionName(User.class);
  }

  @Override
  protected MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Override
  protected Class<User> getEntityClass() {
    return User.class;
  }
}
