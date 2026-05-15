package sn.tafgestion.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.tafgestion.backend.dto.CreateInvoiceRequest;
import sn.tafgestion.backend.dto.InvoiceResponse;
import sn.tafgestion.backend.service.InvoiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // GET /api/invoices
    @GetMapping
    public ResponseEntity<Page<InvoiceResponse>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                invoiceService.getAll(status, start, end, pageable));
    }

    // GET /api/invoices/{id}
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    // POST /api/invoices
    @PostMapping
    public ResponseEntity<InvoiceResponse> create(
            @Valid @RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.create(request));
    }

    // PUT /api/invoices/{id}/pay
    @PutMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponse> markAsPaid(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                invoiceService.markAsPaid(id, body.get("paymentMode")));
    }

    // PUT /api/invoices/{id}/cancel
    @PutMapping("/{id}/cancel")
    public ResponseEntity<InvoiceResponse> cancel(
            @PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.cancel(id));
    }

    // GET /api/invoices/summary
    @GetMapping("/summary")
    public ResponseEntity<Map<String, BigDecimal>> getSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        if (start == null) start = LocalDate.now().withDayOfMonth(1);
        if (end == null) end = LocalDate.now();
        return ResponseEntity.ok(invoiceService.getSummary(start, end));
    }
}
