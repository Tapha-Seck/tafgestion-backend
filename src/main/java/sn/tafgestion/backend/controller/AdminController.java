package sn.tafgestion.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.tafgestion.backend.dto.CreateTenantRequest;
import sn.tafgestion.backend.dto.TenantResponse;
import sn.tafgestion.backend.service.TenantService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final TenantService tenantService;

    // GET /api/admin/tenants
    @GetMapping("/tenants")
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    // GET /api/admin/tenants/{id}
    @GetMapping("/tenants/{id}")
    public ResponseEntity<TenantResponse> getTenant(
            @PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    // POST /api/admin/tenants
    @PostMapping("/tenants")
    public ResponseEntity<TenantResponse> createTenant(
            @Valid @RequestBody CreateTenantRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tenantService.createTenant(request));
    }

    // PUT /api/admin/tenants/{id}/suspend
    @PutMapping("/tenants/{id}/suspend")
    public ResponseEntity<TenantResponse> suspendTenant(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                tenantService.updateStatus(id, "SUSPENDED"));
    }

    // PUT /api/admin/tenants/{id}/activate
    @PutMapping("/tenants/{id}/activate")
    public ResponseEntity<TenantResponse> activateTenant(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                tenantService.updateStatus(id, "ACTIVE"));
    }
}