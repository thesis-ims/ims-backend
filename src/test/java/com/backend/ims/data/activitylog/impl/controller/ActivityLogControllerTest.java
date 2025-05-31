package com.backend.ims.data.activitylog.impl.controller;

import com.backend.ims.data.activitylog.api.service.ActivityLogService;
import com.backend.ims.general.model.request.PaginationRequest;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class ActivityLogControllerTest {

  @Mock
  private ActivityLogService logService;

  private ActivityLogController logController;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    logController = new ActivityLogController(logService);
  }

  @Test
  public void testGetAllProduct() {
    Mockito.when(logService.getAllActivity(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = logController.getAllProduct(new PaginationRequest());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }
}
