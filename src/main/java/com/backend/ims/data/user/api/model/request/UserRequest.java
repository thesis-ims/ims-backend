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
public class UserRequest {
  @NotNull
  private String userId;
  private String email;
  private String username;
  private String gender;
  private String phoneNumber;
  private long dob; // Date of birth (timestamp)
  private byte[] image;
}
