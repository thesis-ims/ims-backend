package com.backend.ims.data.user.api.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {
  private int page;
  private int size;
}
