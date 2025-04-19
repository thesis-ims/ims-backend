package com.backend.ims.data.product.impl.controller;

import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.data.product.api.model.request.ProductRequest;
import com.backend.ims.data.product.api.service.ProductService;
import com.backend.ims.general.model.request.PaginationRequest;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class ProductControllerTest {

  @Mock
  private ProductService productService;

  private ProductController productController;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    productController = new ProductController(productService);
  }

  @Test
  public void testGetAllProduct() {
    Mockito.when(productService.getAllProduct(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = productController.getAllProduct(new PaginationRequest());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }

  @Test
  public void testGetProductDetail() {
    Mockito.when(productService.getProductDetail(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = productController.getProductDetail(new ProductRequest());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }


  @Test
  public void testInsertProduct() {
    Mockito.when(productService.insertProduct(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = productController.insertProduct(new Product());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }

  @Test
  public void testUpdateProduct() {
    Mockito.when(productService.updateProduct(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = productController.updateProduct(new Product());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }

  @Test
  public void testDeleteProduct() {
    Mockito.when(productService.deleteProduct(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = productController.deleteProduct(new ProductRequest());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }
}
