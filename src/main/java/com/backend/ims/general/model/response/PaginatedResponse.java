package com.backend.ims.general.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaginatedResponse<T> {
  private List<T> object;
  private int total;
  private int page;
  private int size;
  private int totalPages;
  private Object otherInfo;
}
