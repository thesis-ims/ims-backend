package com.backend.ims.general.util;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.ArrayList;

@Test
public class SerializationUtilTest {

  @Test
  public void testObjectToJson_Success() {
    record TestData(String name, int age) {}
    String json = SerializationUtil.objectToJson(new TestData("John", 30));
    Assert.assertEquals(json, "{\"name\":\"John\",\"age\":30}");
  }

  @Test
  public void testObjectToJson_Failure() {
    ArrayList<Object> recursiveList = new ArrayList<>();
    recursiveList.add(recursiveList);
    Assert.assertNull(SerializationUtil.objectToJson(recursiveList));
  }

  @Test
  public void testJsonToObject_Success() {
    String json = "{\"name\":\"Alice\",\"age\":25}";
    record TestData(String name, int age) {}
    TestData result = SerializationUtil.jsonToObject(json, TestData.class);
    Assert.assertEquals(result.name(), "Alice");
    Assert.assertEquals(result.age(), 25);
  }

  @Test
  public void testJsonToObject_Failure() {
    Assert.assertNull(SerializationUtil.jsonToObject("{invalid", String.class));
    Assert.assertNull(SerializationUtil.jsonToObject("123", ArrayList.class));
  }
}
