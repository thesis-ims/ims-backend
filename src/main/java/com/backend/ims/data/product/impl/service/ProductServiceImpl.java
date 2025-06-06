package com.backend.ims.data.product.impl.service;

import com.backend.ims.data.activitylog.api.service.ActivityLogService;
import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.data.product.api.model.request.ProductRequest;
import com.backend.ims.data.product.api.model.response.StockSummary;
import com.backend.ims.data.product.api.service.ProductService;
import com.backend.ims.data.product.impl.accessor.ProductAccessor;
import com.backend.ims.data.product.impl.util.ProductUtil;
import com.backend.ims.general.model.BaseResponse;
import com.backend.ims.general.model.request.PaginationRequest;
import com.backend.ims.general.model.response.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

  private final ProductAccessor productAccessor;
  private final ActivityLogService activityLogService;

  @Autowired
  public ProductServiceImpl(ProductAccessor productAccessor,
                            ActivityLogService activityLogService) {
    this.productAccessor = productAccessor;
    this.activityLogService = activityLogService;
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

      List<Product> filteredProducts = ProductUtil.filterProducts(allProducts, request.getFilter());

      int size = request.getSize();
      int page = request.getPage();
      int start = Math.max(0, (page - 1) * size);
      int end = Math.min(start + size, filteredProducts.size());

      List<Product> paginatedProducts = filteredProducts.subList(start, end);

      PaginatedResponse<Product> response = PaginatedResponse.<Product>builder()
        .object(paginatedProducts)
        .total(filteredProducts.size())
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
        .description(request.getDescription())
        .category(request.getCategory())
        .buyPrice(request.getBuyPrice())
        .sellPrice(request.getSellPrice())
        .quantity(request.getQuantity())
        .createdBy(SecurityContextHolder.getContext().getAuthentication().getName())
        .images(request.getImages())
        .createdDate(System.currentTimeMillis())
        .lut(System.currentTimeMillis())
        .build();
      productAccessor.saveItem(product);

      // Log changes
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      activityLogService.logActivity(username, "Product inserted: " + request.getName());
      return ResponseEntity.ok(new BaseResponse<>("Product Inserted Successfully"));
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
      // Compare original and updated product
      StringBuilder changes = new StringBuilder();
      if (!product.getName().equals(request.getName())) {
        changes.append("Name changed from '").append(product.getName())
          .append("' to '").append(request.getName()).append("'. ");
      }
      if (!product.getDescription().equals(request.getDescription())) {
        changes.append("Description changed from '").append(product.getDescription())
          .append("' to '").append(request.getDescription()).append("'. ");
      }
      if (!product.getCategory().equals(request.getCategory())) {
        changes.append("Category changed from '").append(product.getCategory())
          .append("' to '").append(request.getCategory()).append("'. ");
      }
      if (product.getBuyPrice() != request.getBuyPrice()) {
        changes.append("Buy price changed from ").append(product.getBuyPrice())
          .append(" to ").append(request.getBuyPrice()).append(". ");
      }
      if (product.getSellPrice() != request.getSellPrice()) {
        changes.append("Sell price changed from ").append(product.getSellPrice())
          .append(" to ").append(request.getSellPrice()).append(". ");
      }
      if (product.getQuantity() != request.getQuantity()) {
        changes.append("Quantity changed from ").append(product.getQuantity())
          .append(" to ").append(request.getQuantity()).append(". ");
      }

      product.setName(request.getName());
      product.setDescription(request.getDescription());
      product.setCategory(request.getCategory());
      product.setBuyPrice(request.getBuyPrice());
      product.setSellPrice(request.getSellPrice());
      product.setQuantity(request.getQuantity());
      product.setImages(request.getImages());
      product.setLut(System.currentTimeMillis());
      productAccessor.saveItem(product);

      // Log changes
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      activityLogService.logActivity(username, "Product updated: " + changes);
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

  @Override
  public ResponseEntity<?> getStockSummary() {
    try {
      // Get the authenticated user's ID
      String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();

      // Fetch all products created by the authenticated user
      List<Product> allProducts = productAccessor.getAllItems().stream()
        .filter(product -> authenticatedUserId.equals(product.getCreatedBy()))
        .toList();

      StockSummary stockSummary = new StockSummary();

      allProducts.forEach(product -> {
        if (product.getQuantity() == 0) {
          stockSummary.setEmptyStock(stockSummary.getEmptyStock() + 1);
        } else if (product.getQuantity() < 10) {
          stockSummary.setLowStock(stockSummary.getLowStock() + 1);
        } else {
          stockSummary.setAvailable(stockSummary.getAvailable() + 1);
        }
      });

      return ResponseEntity.ok(new BaseResponse<>("Successfully Getting Stock Summary Data", stockSummary));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> getCategorySummary() {
    try {
      // Get the authenticated user's ID
      String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();

      // Fetch all products created by the authenticated user
      List<Product> allProducts = productAccessor.getAllItems().stream()
        .filter(product -> authenticatedUserId.equals(product.getCreatedBy()))
        .toList();

      Map<String, Integer> categorySummary = new HashMap<>();

      allProducts.forEach(product -> {
        String category = product.getCategory();
        if (category != null && !category.isEmpty()) {
          categorySummary.put(category, categorySummary.getOrDefault(category, 0) + 1);
        }
      });

      return ResponseEntity.ok(new BaseResponse<>("Successfully Getting Category Summary Data", categorySummary));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> getNameSummary() {
    try {
      // Get the authenticated user's ID
      String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();

      // Fetch all products created by the authenticated user
      List<Product> allProducts = productAccessor.getAllItems().stream()
        .filter(product -> authenticatedUserId.equals(product.getCreatedBy()))
        .toList();

      Map<String, Integer> nameSummary = new HashMap<>();

      allProducts.forEach(product -> {
        String name = product.getName();
        if (name != null && !name.isEmpty()) {
          nameSummary.put(name, nameSummary.getOrDefault(name, 0) + 1);
        }
      });

      return ResponseEntity.ok(new BaseResponse<>("Successfully Getting Name Summary Data", nameSummary));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }
}
