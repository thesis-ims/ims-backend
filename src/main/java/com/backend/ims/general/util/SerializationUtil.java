package com.backend.ims.general.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializationUtil {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static <T> String objectToJson(T object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static <T> T jsonToObject(String json, Class<T> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }
}
