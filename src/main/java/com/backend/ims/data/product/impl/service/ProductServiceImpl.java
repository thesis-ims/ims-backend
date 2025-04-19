package com.backend.ims.data.product.impl.service;

import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.data.product.api.model.request.ProductRequest;
import com.backend.ims.data.product.api.service.ProductService;
import com.backend.ims.data.product.impl.accessor.ProductAccessor;
import com.backend.ims.general.model.BaseResponse;
import com.backend.ims.general.model.request.PaginationRequest;
import com.backend.ims.general.model.response.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

  private final ProductAccessor productAccessor;

  @Autowired
  public ProductServiceImpl(ProductAccessor productAccessor) {
    this.productAccessor = productAccessor;
  }

  @Override
  public ResponseEntity<?> getAllProduct(PaginationRequest request) {
    try {
      if (request == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }

      // Get the authenticated user's ID
      String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();

      // Fetch all products created by the authenticated user
      List<Product> allProducts = productAccessor.getAllItems().stream()
        .filter(product -> authenticatedUserId.equals(product.getCreatedBy()))
        .toList();

      int size = request.getSize();
      int page = request.getPage();
      int start = Math.max(0, (page - 1) * size);
      int end = Math.min(start + size, allProducts.size());

      List<Product> paginatedProducts = allProducts.subList(start, end);

      PaginatedResponse<Product> response = PaginatedResponse.<Product>builder()
        .object(paginatedProducts)
        .total(allProducts.size())
        .page(page)
        .size(size)
        .totalPages((int) Math.ceil((double) allProducts.size() / size))
        .build();

      return ResponseEntity.ok(new BaseResponse<>("Successfully Getting All Product Data", response));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> getProductDetail(ProductRequest request) {
    try {
      if (request == null || request.getProductId() == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }
      Product product = productAccessor.getItemById(request.getProductId());
      if (product == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>(String.format("Error: There's no product with productId: %s!", request.getProductId())));
      }
      return ResponseEntity.ok(new BaseResponse<>("Successfully Getting Product Data", product));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> insertProduct(Product request) {
    try {
      if (request == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }
      Product product = Product.builder()
        .id(UUID.randomUUID().toString())
        .name(request.getName())
        .quantity(request.getQuantity())
        .createdBy(SecurityContextHolder.getContext().getAuthentication().getName())
        .images(request.getImages())
        .createdDate(System.currentTimeMillis())
        .lut(System.currentTimeMillis())
        .build();
      productAccessor.saveItem(product);
      return ResponseEntity.ok(new BaseResponse<>("Product Inserted Successfully", request));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> updateProduct(Product request) {
    try {
      if (request == null || request.getId() == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }
      Product product = productAccessor.getItemById(request.getId());
      if (product == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>(String.format("Error: There's no product with productId: %s!", request.getId())));
      }
      product.setLut(System.currentTimeMillis());
      productAccessor.saveItem(product);
      return ResponseEntity.ok(new BaseResponse<>("Successfully Update Product Data"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> deleteProduct(ProductRequest request) {
    try {
      if (request == null || request.getProductId() == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }
      productAccessor.deleteItem(request.getProductId());
      return ResponseEntity.ok(new BaseResponse<>("Successfully Delete Product Data"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }
}
