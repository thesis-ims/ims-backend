package com.backend.ims.data.activitylog.impl.service;

import com.backend.ims.data.activitylog.api.model.ActivityLog;
import com.backend.ims.data.activitylog.api.service.ActivityLogService;
import com.backend.ims.data.activitylog.impl.accessor.ActivityLogAccessor;
import com.backend.ims.general.model.BaseResponse;
import com.backend.ims.general.model.request.PaginationRequest;
import com.backend.ims.general.model.response.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

  private final ActivityLogAccessor activityLogAccessor;

  @Autowired
  public ActivityLogServiceImpl(ActivityLogAccessor activityLogAccessor) {
    this.activityLogAccessor = activityLogAccessor;
  }

  @Override
  public void logActivity(String username, String activity) {
    ActivityLog activityLog = ActivityLog.builder()
      .id(UUID.randomUUID().toString())
      .username(username)
      .date(System.currentTimeMillis())
      .activity(activity)
      .build();
    activityLogAccessor.saveItem(activityLog);
  }

  @Override
  public ResponseEntity<?> getAllActivity(PaginationRequest request) {
    try {
      if (request == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }

      // Get the authenticated user's ID
      String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();

      // Fetch all activities created by the authenticated user
      List<ActivityLog> allActivity = activityLogAccessor.getAllItems().stream()
        .filter(product -> authenticatedUserId.equals(product.getUsername()))
        .toList();

      int size = request.getSize();
      int page = request.getPage();
      int start = Math.max(0, (page - 1) * size);
      int end = Math.min(start + size, allActivity.size());

      List<ActivityLog> paginatedActivity = allActivity.subList(start, end);

      PaginatedResponse<ActivityLog> response = PaginatedResponse.<ActivityLog>builder()
        .object(paginatedActivity)
        .total(allActivity.size())
        .page(page)
        .size(size)
        .totalPages((int) Math.ceil((double) allActivity.size() / size))
        .build();

      return ResponseEntity.ok(new BaseResponse<>("Successfully Getting All Activity Data", response));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }
}
