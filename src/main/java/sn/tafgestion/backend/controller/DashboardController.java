package sn.tafgestion.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.tafgestion.backend.dto.DashboardResponse;
import sn.tafgestion.backend.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    // GET /api/dashboard/stats
    @GetMapping("/stats")
    public ResponseEntity<DashboardResponse> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
