package com.backend.ims.data.activitylog.impl.accessor;

import com.backend.ims.data.activitylog.api.model.ActivityLog;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class ActivityLogAccessorTest {

  @InjectMocks
  private ActivityLogAccessor activityLogAccessor;

  @Mock
  private MongoTemplate mongoTemplate;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetCollectionName() {
    String expectedCollectionName = "activity_logs";
    Mockito.when(mongoTemplate.getCollectionName(ActivityLog.class)).thenReturn(expectedCollectionName);

    String actualCollectionName = activityLogAccessor.getCollectionName();
    Assert.assertEquals(actualCollectionName, expectedCollectionName);
  }

  @Test
  public void testGetMongoTemplate() {
    MongoTemplate actualMongoTemplate = activityLogAccessor.getMongoTemplate();
    Assert.assertNotNull(actualMongoTemplate);
  }

  @Test
  public void testGetEntityClass() {
    Class<ActivityLog> entityClass = activityLogAccessor.getEntityClass();
    Assert.assertEquals(entityClass, ActivityLog.class);
  }
}
