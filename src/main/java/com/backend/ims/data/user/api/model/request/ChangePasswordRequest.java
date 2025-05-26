package com.backend.ims.data.user.api.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
  @NotNull
  private String userId;
  private String currentPassword;
  private String newPassword;
}
