package com.backend.ims.data.activitylog.impl.service;

import com.backend.ims.data.activitylog.api.model.ActivityLog;
import com.backend.ims.data.activitylog.api.service.ActivityLogService;
import com.backend.ims.data.activitylog.impl.accessor.ActivityLogAccessor;
import com.backend.ims.general.model.BaseResponse;
import com.backend.ims.general.model.request.PaginationRequest;
import com.backend.ims.general.model.response.PaginatedResponse;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class ActivityLogServiceImplTest {

  @Mock
  private ActivityLogAccessor logAccessor;

  private ActivityLogService logService;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    logService = new ActivityLogServiceImpl(logAccessor);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Authentication authentication = Mockito.mock(Authentication.class);

    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    Mockito.when(authentication.getName()).thenReturn("mockedUser");
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  public void testGetAllActivity_RequestNull() {
    ResponseEntity<?> response = logService.getAllActivity(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testGetAllActivity_Success() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    ActivityLog activityLog = ActivityLog.builder()
      .activity("Log 1")
      .username("mockedUser")
      .build();
    ActivityLog activityLog1 = ActivityLog.builder()
      .activity("Log 2")
      .username("mockedUser")
      .build();
    Mockito.when(logAccessor.getAllItems()).thenReturn(List.of(activityLog, activityLog1));
    ResponseEntity<?> response = logService.getAllActivity(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All Activity Data");
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 2);
  }

  @Test
  public void testGetAllActivity_Exception() {
    Mockito.when(logAccessor.getAllItems()).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = logService.getAllActivity(new PaginationRequest());
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testLogActivity_RequestNull() {
    logService.logActivity("mockedUser", "log");
  }
}
