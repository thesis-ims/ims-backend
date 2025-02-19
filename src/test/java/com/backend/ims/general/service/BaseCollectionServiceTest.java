package com.backend.ims.general.service;

import org.bson.Document;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class BaseCollectionServiceTest {

  public static final String COLLECTION_NAME = "testCollection";
  @Mock
  private MongoTemplate mongoTemplate;

  private BaseCollectionService<User> service;

  @BeforeMethod
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new BaseCollectionService<>() {
      @Override
      protected String getCollectionName() {
        return COLLECTION_NAME;
      }

      @Override
      protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
      }

      @Override
      protected Class<User> getEntityClass() {
        return User.class;
      }
    };
  }

  @Test
  public void getAllItems_CallsFindAllWithCorrectParameters() {
    List<User> expectedList = List.of(new User());
    Mockito.when(mongoTemplate.findAll(User.class, COLLECTION_NAME)).thenReturn(expectedList);

    List<User> result = service.getAllItems();

    Assert.assertSame(result, expectedList);
    Mockito.verify(mongoTemplate).findAll(User.class, COLLECTION_NAME);
  }

  @Test
  public void getItemById_CallsFindByIdWithCorrectParameters() {
    String id = "123";
    User expectedUser = new User();
    Mockito.when(mongoTemplate.findById(id, User.class, COLLECTION_NAME)).thenReturn(expectedUser);

    User result = service.getItemById(id);

    Assert.assertSame(result, expectedUser);
    Mockito.verify(mongoTemplate).findById(id, User.class, COLLECTION_NAME);
  }

  @Test
  public void saveItem_CallsSaveWithCorrectParameters() {
    User user = new User();
    Mockito.when(mongoTemplate.save(user, COLLECTION_NAME)).thenReturn(user);

    User result = service.saveItem(user);

    Assert.assertSame(result, user);
    Mockito.verify(mongoTemplate).save(user, COLLECTION_NAME);
  }

  @Test
  public void deleteItem_CallsRemoveWithCorrectQueryAndCollection() {
    String id = "123";
    service.deleteItem(id);

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    Mockito.verify(mongoTemplate).remove(queryCaptor.capture(), Mockito.eq(COLLECTION_NAME));

    Document actualCriteria = queryCaptor.getValue().getQueryObject();

    Document expectedCriteria = new Document("_id", id);

    Assert.assertEquals(actualCriteria, expectedCriteria);
  }

  @Test
  public void deleteAllUsers_CallsRemoveWithEmptyQuery() {
    service.deleteAllUsers();

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    Mockito.verify(mongoTemplate).remove(queryCaptor.capture(), Mockito.eq(COLLECTION_NAME));

    Query actualQuery = queryCaptor.getValue();
    Assert.assertTrue(actualQuery.getQueryObject().isEmpty());
  }

  @Test
  public void getByFilter_CallsFindOneWithCorrectQuery() {
    String key = "name";
    String value = "John";
    User expectedUser = new User();
    Mockito.when(mongoTemplate.findOne(Mockito.any(Query.class), Mockito.eq(User.class), Mockito.eq(COLLECTION_NAME))).thenReturn(expectedUser);

    User result = service.getByFilter(key, value);

    Assert.assertSame(result, expectedUser);

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    Mockito.verify(mongoTemplate).findOne(queryCaptor.capture(), Mockito.eq(User.class), Mockito.eq(COLLECTION_NAME));
    Document actualCriteria = queryCaptor.getValue().getQueryObject();
    Document expectedCriteria = new Document(key, value);

    Assert.assertEquals(actualCriteria, expectedCriteria);
  }
  private static class User {
  }
}
