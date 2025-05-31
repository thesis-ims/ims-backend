package com.backend.ims.data.activitylog.api.service;

import com.backend.ims.general.model.request.PaginationRequest;
import org.springframework.http.ResponseEntity;

public interface ActivityLogService {
  /**
   * This method is used to store activity history
   * @param username username
   * @param activity activity
   */
  void logActivity(String username, String activity);

  /**
   * This method is used to get all activity log data
   *
   * @param request request will contains page and size per page to shown
   * @return all activity log data
   */
  ResponseEntity<?> getAllActivity(PaginationRequest request);
}
