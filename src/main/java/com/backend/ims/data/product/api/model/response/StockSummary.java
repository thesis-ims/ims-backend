package com.backend.ims.data.product.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSummary {
  private int available;
  private int lowStock;
  private int emptyStock;
}
