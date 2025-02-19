package com.backend.ims.data.user.api.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
  @NotNull
  @Email
  private String email;
  @NotNull
  private String username;
  @NotNull
  private String password;
  private String gender;
  private String phoneNumber;
  private long dob;
}
