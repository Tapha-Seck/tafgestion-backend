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

    // GET /api/dashboard/stats?period=DAY
    // GET /api/dashboard/stats?period=WEEK
    // GET /api/dashboard/stats?period=MONTH
    // GET /api/dashboard/stats?period=YEAR
    @GetMapping("/stats")
    public ResponseEntity<DashboardResponse> getStats(
            @RequestParam(defaultValue = "MONTH") String period) {
        return ResponseEntity.ok(dashboardService.getStats(period));
    }
}