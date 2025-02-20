package com.backend.ims.data.user.impl.accessor;

import com.backend.ims.data.user.api.model.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class UserAccessorTest {

  @InjectMocks
  private UserAccessor userAccessor;

  @Mock
  private MongoTemplate mongoTemplate;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetCollectionName() {
    String expectedCollectionName = "users";
    Mockito.when(mongoTemplate.getCollectionName(User.class)).thenReturn(expectedCollectionName);

    String actualCollectionName = userAccessor.getCollectionName();
    Assert.assertEquals(actualCollectionName, expectedCollectionName);
  }

  @Test
  public void testGetMongoTemplate() {
    MongoTemplate actualMongoTemplate = userAccessor.getMongoTemplate();
    Assert.assertNotNull(actualMongoTemplate);
  }

  @Test
  public void testGetEntityClass() {
    Class<User> entityClass = userAccessor.getEntityClass();
    Assert.assertEquals(entityClass, User.class);
  }
}
