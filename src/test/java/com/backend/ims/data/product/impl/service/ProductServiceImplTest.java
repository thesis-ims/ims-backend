package com.backend.ims.data.product.impl.service;

import com.backend.ims.data.activitylog.api.service.ActivityLogService;
import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.data.product.api.model.request.ProductRequest;
import com.backend.ims.data.product.api.model.response.StockSummary;
import com.backend.ims.data.product.api.service.ProductService;
import com.backend.ims.data.product.impl.accessor.ProductAccessor;
import com.backend.ims.general.model.BaseResponse;
import com.backend.ims.general.model.request.PaginationRequest;
import com.backend.ims.general.model.request.SpecFilter;
import com.backend.ims.general.model.response.MapResponse;
import com.backend.ims.general.model.response.PaginatedResponse;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;

@Test
public class ProductServiceImplTest {

  @Mock
  private ProductAccessor productAccessor;
  @Mock
  private ActivityLogService activityLogService;

  private ProductService productService;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    productService = new ProductServiceImpl(productAccessor, activityLogService);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Authentication authentication = Mockito.mock(Authentication.class);

    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    Mockito.when(authentication.getName()).thenReturn("mockedUser");
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  public void testGetAllProduct_RequestNull() {
    ResponseEntity<?> response = productService.getAllProduct(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testGetAllProduct_Success() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .quantity(0)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getAllProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All Product Data");
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 2);
  }

  @Test
  public void testGetAllProduct_FilterDescending() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    request.setFilter(SpecFilter.NAME_DESCENDING);
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .quantity(0)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getAllProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All Product Data");
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 2);
  }

  @Test
  public void testGetAllProduct_FilterAscending() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    request.setFilter(SpecFilter.NAME_ASCENDING);
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .quantity(0)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getAllProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All Product Data");
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 2);
  }

  @Test
  public void testGetAllProduct_LowestStock() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    request.setFilter(SpecFilter.LOWEST_STOCK);
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .quantity(0)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getAllProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All Product Data");
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 2);
  }

  @Test
  public void testGetAllProduct_Highest_Stock() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    request.setFilter(SpecFilter.HIGHEST_STOCK);
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .quantity(0)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getAllProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All Product Data");
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 2);
  }

  @Test
  public void testGetAllProduct_Oldest() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    request.setFilter(SpecFilter.OLDEST);
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .createdDate(1)
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .quantity(0)
      .createdDate(2)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getAllProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All Product Data");
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 2);
  }

  @Test
  public void testGetAllProduct_Newest() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    request.setFilter(SpecFilter.NEWEST);
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .createdDate(1)
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .quantity(0)
      .createdDate(2)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getAllProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All Product Data");
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 2);
  }

  @Test
  public void testGetAllProduct_Low_Stock() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    request.setFilter(SpecFilter.LOW_STOCK);
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .quantity(0)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getAllProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All Product Data");
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 0);
  }

  @Test
  public void testGetAllProduct_Out_Of_Stock() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    request.setFilter(SpecFilter.OUT_OF_STOCK);
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .quantity(0)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getAllProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All Product Data");
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 1);
  }

  @Test
  public void testGetAllProduct_Exception() {
    Mockito.when(productAccessor.getAllItems()).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = productService.getAllProduct(new PaginationRequest());
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testGetProductDetail_RequestNull() {
    ResponseEntity<?> response = productService.getProductDetail(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testGetProductDetail_ProductNull() {
    ProductRequest request = new ProductRequest();
    request.setProductId("123");
    Mockito.when(productAccessor.getItemById(anyString())).thenReturn(null);
    ResponseEntity<?> response = productService.getProductDetail(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: There's no product with productId: 123!");
  }

  @Test
  public void testGetProductDetail_Success() {
    ProductRequest request = new ProductRequest();
    request.setProductId("123");
    Mockito.when(productAccessor.getItemById(anyString())).thenReturn(new Product());
    ResponseEntity<?> response = productService.getProductDetail(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting Product Data");
    Assert.assertEquals((((BaseResponse) response.getBody()).getData().getClass()), Product.class);
  }

  @Test
  public void testGetProductDetail_Exception() {
    ProductRequest request = new ProductRequest();
    request.setProductId("123");
    Mockito.when(productAccessor.getItemById(anyString())).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = productService.getProductDetail(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testInsertProduct_RequestNull() {
    ResponseEntity<?> response = productService.insertProduct(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testInsertProduct_Success() {
    Product request = new Product();
    request.setName("Product 1");
    request.setQuantity(10);
    ResponseEntity<?> response = productService.insertProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Product Inserted Successfully");
    Mockito.verify(activityLogService).logActivity(anyString(), anyString());
  }


  @Test
  public void testInsertProduct_Exception() {
    Product request = new Product();
    request.setName("Product 1");
    request.setQuantity(10);
    Mockito.when(productAccessor.saveItem(Mockito.any())).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = productService.insertProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testUpdateProduct_RequestNull() {
    ResponseEntity<?> response = productService.updateProduct(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testUpdateProduct_ProductNull() {
    Product request = new Product();
    request.setId("123");
    Mockito.when(productAccessor.getItemById(anyString())).thenReturn(null);
    ResponseEntity<?> response = productService.updateProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: There's no product with productId: 123!");
  }

  @Test
  public void testUpdateProduct_Success() {
    Product request = new Product();
    request.setId("123");
    request.setName("Updated Product");

    Product existingProduct = Product.builder()
      .id("123")
      .name("Old Product")
      .build();

    Mockito.when(productAccessor.getItemById(anyString())).thenReturn(existingProduct);
    ResponseEntity<?> response = productService.updateProduct(request);

    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Update Product Data");
    Mockito.verify(activityLogService).logActivity(anyString(), anyString());
  }

  @Test
  public void testUpdateProduct_Exception() {
    Product request = new Product();
    request.setId("123");
    Mockito.when(productAccessor.getItemById(anyString())).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = productService.updateProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testDeleteProduct_RequestNull() {
    ResponseEntity<?> response = productService.deleteProduct(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testDeleteProduct_Success() {
    ProductRequest request = new ProductRequest();
    request.setProductId("123");
    ResponseEntity<?> response = productService.deleteProduct(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Delete Product Data");
  }

  @Test
  public void testGetStockSummary_Success() {
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .quantity(0)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getStockSummary();
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting Stock Summary Data");
    Assert.assertEquals(((BaseResponse<StockSummary>) response.getBody()).getData().getEmptyStock(), 1);
    Assert.assertEquals(((BaseResponse<StockSummary>) response.getBody()).getData().getAvailable(), 1);
  }

  @Test
  public void testGetStockSummary_Exception() {
    Mockito.when(productAccessor.getAllItems()).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = productService.getStockSummary();
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testGetCategorySummary_Success() {
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .category("Electronics")
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .category("Furniture")
      .quantity(0)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getCategorySummary();
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting Category Summary Data");
    Assert.assertEquals(((BaseResponse<List<MapResponse>>) response.getBody()).getData().size(), 2);
  }

  @Test
  public void testGetCategorySummary_Exception() {
    Mockito.when(productAccessor.getAllItems()).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = productService.getCategorySummary();
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testGetNameSummary_Success() {
    Product product = Product.builder()
      .name("Product 1")
      .createdBy("mockedUser")
      .quantity(20)
      .category("Electronics")
      .build();
    Product product1 = Product.builder()
      .name("Product 2")
      .createdBy("mockedUser")
      .category("Furniture")
      .quantity(0)
      .build();
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product, product1));
    ResponseEntity<?> response = productService.getNameSummary();
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting Name Summary Data");
    Assert.assertEquals(((BaseResponse<List<MapResponse>>) response.getBody()).getData().size(), 2);
  }

  @Test
  public void testGetNameSummary_Exception() {
    Mockito.when(productAccessor.getAllItems()).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = productService.getNameSummary();
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
