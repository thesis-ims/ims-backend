package com.backend.ims.general.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {
  private int page;
  private int size;
}
