package com.backend.ims.data.product.impl.util;

import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.general.model.request.SpecFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
      default:
        break;
    }

    return mutableProducts;
  }
}