package com.backend.ims.data.product.impl.accessor;

import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.general.service.BaseCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductAccessor extends BaseCollectionService<Product> {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  protected String getCollectionName() {
    return mongoTemplate.getCollectionName(Product.class);
  }

  @Override
  protected MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Override
  protected Class<Product> getEntityClass() {
    return Product.class;
  }
}
