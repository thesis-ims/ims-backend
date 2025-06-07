package com.backend.ims.data.product.impl.util;

import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.general.model.request.SpecFilter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ProductUtil {

  public static List<Product> filterProducts(List<Product> products, SpecFilter filter) {
    if (filter == null) {
      return products; // no filtering applied
    }

    // create a mutable copy of the list
    List<Product> mutableProducts = new ArrayList<>(products);

    switch (filter) {
      case LOWEST_STOCK:
        mutableProducts.sort(Comparator.comparingInt(Product::getQuantity));
        break;
      case HIGHEST_STOCK:
        mutableProducts.sort(Comparator.comparingInt(Product::getQuantity).reversed());
        break;
      case NAME_ASCENDING:
        mutableProducts.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        break;
      case NAME_DESCENDING:
        mutableProducts.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER).reversed());
        break;
      case OLDEST:
        mutableProducts.sort(Comparator.comparingLong(Product::getCreatedDate));
        break;
      case NEWEST:
        mutableProducts.sort(Comparator.comparingLong(Product::getCreatedDate).reversed());
        break;
      case LOW_STOCK:
        mutableProducts.removeIf(product -> product.getQuantity() == 0 || product.getQuantity() >= 10);
        break;
      case OUT_OF_STOCK:
        mutableProducts.removeIf(product -> product.getQuantity() > 0);
        break;
      default:
        break;
    }

    return mutableProducts;
  }

  public static List<Product> parseCsvToProducts(byte[] csvData, String username) throws IOException {
    List<Product> products = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(csvData)))) {
      CSVParser parser = CSVParser.parse(reader, CSVFormat.Builder.create()
        .setHeader("Name", "Description", "Category", "Buy Price", "Sell Price", "Quantity")
        .setSkipHeaderRecord(true)
        .build());
      for (CSVRecord record : parser) {
        Product product = Product.builder()
          .id(UUID.randomUUID().toString()) // Generate a new UUID for the product
          .name(record.get("Name"))
          .description(record.get("Description"))
          .category(record.get("Category"))
          .buyPrice(Long.parseLong(record.get("Buy Price")))
          .sellPrice(Long.parseLong(record.get("Sell Price")))
          .quantity(Integer.parseInt(record.get("Quantity")))
          .createdDate(System.currentTimeMillis()) // Set current time as creation date
          .createdBy(username)
          .build();
        products.add(product);
      }
    }
    return products;
  }
}