package com.backend.ims.general.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// This will exclude null values from JSON response
// In case there's exception no need to send data then it won't show as null
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
  private String message;
  private T data;

  public BaseResponse(String message) {
    this.message = message;
  }

  public BaseResponse(T data) {
    this.data = data;
  }
}
