package com.backend.ims.data.product.impl.service;

import com.backend.ims.data.activitylog.api.service.ActivityLogService;
import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.data.product.api.model.request.ProductRequest;
import com.backend.ims.data.product.api.model.response.StockSummary;
import com.backend.ims.data.product.api.service.ProductService;
import com.backend.ims.data.product.impl.accessor.ProductAccessor;
import com.backend.ims.data.product.impl.util.ProductUtil;
import com.backend.ims.general.model.BaseResponse;
import com.backend.ims.general.model.request.ImportCsvRequest;
import com.backend.ims.general.model.request.ImportType;
import com.backend.ims.general.model.request.PaginationRequest;
import com.backend.ims.general.model.response.MapResponse;
import com.backend.ims.general.model.response.PaginatedResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
      String userName = SecurityContextHolder.getContext().getAuthentication().getName();

      // Fetch all products created by the authenticated user
      List<Product> allProducts = productAccessor.getAllItems().stream()
        .filter(product -> userName.equals(product.getCreatedBy()))
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
        .totalPages((int) Math.ceil((double) filteredProducts.size() / size))
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
  public ResponseEntity<?> importCsv(ImportCsvRequest request) {
    try {
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      List<Product> products = ProductUtil.parseCsvToProducts(request.getCsvData(), username);
      if (request.getImportType() == ImportType.REPLACE) {
        productAccessor.deleteAll(username); // Delete all products created by the user before importing
      }
      productAccessor.insertAll(products); // Insert all products from the CSV
      activityLogService.logActivity(username, "Product data imported using " + request.getImportType().name());
      return ResponseEntity.ok(new BaseResponse<>("Successfully Import Product Data by " + request.getImportType().name()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> exportCsv() {
    try {
      // Get the authenticated user's ID
      String username = SecurityContextHolder.getContext().getAuthentication().getName();

      // Fetch all products created by the authenticated user
      List<Product> allProducts = productAccessor.getAllItems().stream()
        .filter(product -> username.equals(product.getCreatedBy()))
        .toList();

      // Generate CSV data
      StringBuilder csvData = ProductUtil.parseProductsToCsv(allProducts);

      return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=products.csv")
        .body(csvData.toString());
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
      String username = SecurityContextHolder.getContext().getAuthentication().getName();

      Product product = Product.builder()
        .id(UUID.randomUUID().toString())
        .name(request.getName())
        .description(request.getDescription())
        .category(request.getCategory())
        .buyPrice(request.getBuyPrice())
        .sellPrice(request.getSellPrice())
        .quantity(request.getQuantity())
        .createdBy(username)
        .images(request.getImages())
        .createdDate(System.currentTimeMillis())
        .lut(System.currentTimeMillis())
        .build();
      productAccessor.saveItem(product);

      // Log changes
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
      changes.append("Changes on product ").append(product.getName()).append(": ");
      if (request.getName() != null && !request.getName().equals(product.getName())) {
        if (product.getName() == null) {
          changes.append("Name changed from 'null' to '").append(request.getName()).append("'. ");
        } else {
          changes.append("Name changed from '").append(product.getName())
            .append("' to '").append(request.getName()).append("'. ");
        }
      } else if (request.getName() == null && product.getName() != null) {
        changes.append("Name changed from '").append(product.getName()).append("' to 'null'. ");
      }

      if (request.getDescription() != null && !request.getDescription().equals(product.getDescription())) {
        if (product.getDescription() == null) {
          changes.append("Description changed from 'null' to '").append(request.getDescription()).append("'. ");
        } else {
          changes.append("Description changed from '").append(product.getDescription())
            .append("' to '").append(request.getDescription()).append("'. ");
        }
      } else if (request.getDescription() == null && product.getDescription() != null) {
        changes.append("Description changed from '").append(product.getDescription()).append("' to 'null'. ");
      }

      if (request.getCategory() != null && !request.getCategory().equals(product.getCategory())) {
        if (product.getCategory() == null) {
          changes.append("Category changed from 'null' to '").append(request.getCategory()).append("'. ");
        } else {
          changes.append("Category changed from '").append(product.getCategory())
            .append("' to '").append(request.getCategory()).append("'. ");
        }
      } else if (request.getCategory() == null && product.getCategory() != null) {
        changes.append("Category changed from '").append(product.getCategory()).append("' to 'null'. ");
      }
      if (request.getBuyPrice() != product.getBuyPrice()) {
        changes.append("Buy price changed from ").append(product.getBuyPrice())
          .append(" to ").append(request.getBuyPrice()).append(". ");
      }
      if (request.getSellPrice() != product.getSellPrice()) {
        changes.append("Sell price changed from ").append(product.getSellPrice())
          .append(" to ").append(request.getSellPrice()).append(". ");
      }
      if (request.getQuantity() != product.getQuantity()) {
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
      // Log changes
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      activityLogService.logActivity(username, "Product updated: " + changes);
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
      activityLogService.logActivity(
        SecurityContextHolder.getContext().getAuthentication().getName(),
        "Product deleted with ID: " + request.getProductId()
      );
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

      List<MapResponse> mapResponses = categorySummary.entrySet().stream()
        .map(entry -> new MapResponse(entry.getKey(), entry.getValue()))
        .toList();

      return ResponseEntity.ok(new BaseResponse<>("Successfully Getting Category Summary Data", mapResponses));
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

      List<MapResponse> mapResponses = nameSummary.entrySet().stream()
        .map(entry -> new MapResponse(entry.getKey(), entry.getValue()))
        .toList();

      return ResponseEntity.ok(new BaseResponse<>("Successfully Getting Name Summary Data", mapResponses));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }
}
