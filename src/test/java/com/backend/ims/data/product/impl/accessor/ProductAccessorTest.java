package com.backend.ims.data.product.impl.accessor;

import com.backend.ims.data.product.api.model.Product;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class ProductAccessorTest {

  @InjectMocks
  private ProductAccessor productAccessor;

  @Mock
  private MongoTemplate mongoTemplate;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetCollectionName() {
    String expectedCollectionName = "products";
    Mockito.when(mongoTemplate.getCollectionName(Product.class)).thenReturn(expectedCollectionName);

    String actualCollectionName = productAccessor.getCollectionName();
    Assert.assertEquals(actualCollectionName, expectedCollectionName);
  }

  @Test
  public void testGetMongoTemplate() {
    MongoTemplate actualMongoTemplate = productAccessor.getMongoTemplate();
    Assert.assertNotNull(actualMongoTemplate);
  }

  @Test
  public void testGetEntityClass() {
    Class<Product> entityClass = productAccessor.getEntityClass();
    Assert.assertEquals(entityClass, Product.class);
  }
}
