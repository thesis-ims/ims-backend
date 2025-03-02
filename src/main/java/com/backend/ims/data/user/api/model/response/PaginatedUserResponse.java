package com.backend.ims.data.user.api.model.response;

import com.backend.ims.data.user.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaginatedUserResponse {
  private List<User> users;
  private int total;
  private int page;
  private int size;
  private int totalPages;
}
