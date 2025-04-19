package com.backend.ims.data.product.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "products")
public class Product {
  @Id
  private String id;
  private String name;
  private int quantity;
  private long createdDate; // creation time
  private long lut; // last update time
  private String createdBy; // Reference to the user who created the product
  private List<byte[]> images; // List of images stored as binary data
}
