package com.dulcefina.service;

import com.dulcefina.entity.ActivityLog;

import java.util.List;
import java.util.Optional;

public interface ActivityLogService {
    List<ActivityLog> findAll();
    Optional<ActivityLog> findById(Long id);
    ActivityLog create(ActivityLog log);
    void delete(Long id);
}
