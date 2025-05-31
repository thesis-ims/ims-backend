package com.backend.ims.data.activitylog.impl.controller;

import com.backend.ims.data.activitylog.api.service.ActivityLogService;
import com.backend.ims.general.model.request.PaginationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity")
public class ActivityLogController {
  private final ActivityLogService activityLogService;

  @Autowired
  public ActivityLogController(ActivityLogService activityLogService) {
    this.activityLogService = activityLogService;
  }

  @PostMapping("/get-all-activity")
  public ResponseEntity<?> getAllProduct(@RequestBody PaginationRequest request) {
    return activityLogService.getAllActivity(request);
  }
}
