package com.backend.ims.data.product.api.service;

import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.data.product.api.model.request.ProductRequest;
import com.backend.ims.general.model.request.ImportCsvRequest;
import com.backend.ims.general.model.request.PaginationRequest;
import org.springframework.http.ResponseEntity;

public interface ProductService {
  /**
   * This method is used to get all product data
   *
   * @param request request will contains page and size per page to shown
   * @return all product data
   */
  ResponseEntity<?> getAllProduct(PaginationRequest request);


  /**
   * This method is used to get product by product id
   *
   * @param request product id to get product detail
   * @return detailed product information
   */
  ResponseEntity<?> getProductDetail(ProductRequest request);

  /**
   * This method is used to import product data from csv file
   *
   * @param request import csv request
   * @return base response
   */
  ResponseEntity<?> importCsv(ImportCsvRequest request);

  /**
   * This method is used to insert new product data
   *
   * @param request product data to insert
   * @return product data response
   */
  ResponseEntity<?> insertProduct(Product request);

  /**
   * This method is used to update product data
   *
   * @param request product data to update
   * @return product data response
   */
  ResponseEntity<?> updateProduct(Product request);


  /**
   * This method will delete product data by product id
   *
   * @param request product id to delete product
   * @return base response
   */
  ResponseEntity<?> deleteProduct(ProductRequest request);

  /**
   * This method will get stock summary of all products
   *
   * @return stock summary of all products
   */
  ResponseEntity<?> getStockSummary();

  /**
   * This method will get all category summary
   *
   * @return category summary of all products
   */
  ResponseEntity<?> getCategorySummary();

  /**
   * This method will get all name summary
   *
   * @return name summary of all products
   */
  ResponseEntity<?> getNameSummary();
}
