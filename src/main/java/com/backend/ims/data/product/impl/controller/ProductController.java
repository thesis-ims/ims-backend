package com.backend.ims.data.product.impl.controller;

import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.data.product.api.model.request.ProductRequest;
import com.backend.ims.data.product.api.service.ProductService;
import com.backend.ims.general.model.request.PaginationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductService userService;

  @Autowired
  public ProductController(ProductService userService) {
    this.userService = userService;
  }

  @PostMapping("/get-all-product")
  public ResponseEntity<?> getAllProduct(@RequestBody PaginationRequest request) {
    return userService.getAllProduct(request);
  }

  @PostMapping("/get-product-detail")
  public ResponseEntity<?> getProductDetail(@RequestBody ProductRequest request) {
    return userService.getProductDetail(request);
  }

  @PostMapping("/update")
  public ResponseEntity<?> updateProduct(@RequestBody Product request) {
    return userService.updateProduct(request);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> deleteProduct(@RequestBody ProductRequest request) {
    return userService.deleteProduct(request);
  }

  @PostMapping("/insert")
  public ResponseEntity<?> insertProduct(@RequestBody Product request) {
    return userService.insertProduct(request);
  }

}
