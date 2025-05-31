package com.backend.ims.data.activitylog.impl.accessor;

import com.backend.ims.data.activitylog.api.model.ActivityLog;
import com.backend.ims.general.service.BaseCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogAccessor extends BaseCollectionService<ActivityLog> {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  protected String getCollectionName() {
    return mongoTemplate.getCollectionName(ActivityLog.class);
  }

  @Override
  protected MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Override
  protected Class<ActivityLog> getEntityClass() {
    return ActivityLog.class;
  }
}
