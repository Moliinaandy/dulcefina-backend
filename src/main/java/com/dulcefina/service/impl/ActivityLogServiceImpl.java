package com.dulcefina.service.impl;

import com.dulcefina.entity.ActivityLog;
import com.dulcefina.repository.ActivityLogRepository;
import com.dulcefina.service.ActivityLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public List<ActivityLog> findAll() {
        return activityLogRepository.findAll();
    }

    @Override
    public Optional<ActivityLog> findById(Long id) {
        return activityLogRepository.findById(id);
    }

    @Override
    public ActivityLog create(ActivityLog log) {
        return activityLogRepository.save(log);
    }

    @Override
    public void delete(Long id) {
        if (!activityLogRepository.existsById(id)) {
            throw new NoSuchElementException("ActivityLog not found: " + id);
        }
        activityLogRepository.deleteById(id);
    }
}
