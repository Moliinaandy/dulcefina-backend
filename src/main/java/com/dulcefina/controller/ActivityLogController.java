package com.dulcefina.controller;

import com.dulcefina.dto.ActivityLogRequest;
import com.dulcefina.entity.ActivityLog;
import com.dulcefina.entity.UserAccount;
import com.dulcefina.repository.UserAccountRepository;
import com.dulcefina.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final UserAccountRepository userRepo;

    public ActivityLogController(ActivityLogService activityLogService,
                                 UserAccountRepository userRepo) {
        this.activityLogService = activityLogService;
        this.userRepo = userRepo;
    }

    @GetMapping
    public ResponseEntity<List<ActivityLog>> all() {
        return ResponseEntity.ok(activityLogService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return activityLogService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ActivityLogRequest req) {
        ActivityLog log = new ActivityLog();
        if (req.getUserId() != null) {
            UserAccount user = userRepo.findById(req.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + req.getUserId()));
            log.setUser(user);
        }
        log.setAction(req.getAction());
        log.setDetails(req.getDetails());
        log.setCreatedAt(LocalDateTime.now());

        ActivityLog saved = activityLogService.create(log);
        return ResponseEntity.created(URI.create("/api/logs/" + saved.getLogId())).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            activityLogService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
