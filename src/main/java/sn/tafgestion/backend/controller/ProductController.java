package sn.tafgestion.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.tafgestion.backend.dto.ProductRequest;
import sn.tafgestion.backend.dto.ProductResponse;
import sn.tafgestion.backend.service.ProductService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    // GET /api/products?search=xxx&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.getAll(search, pageable));
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    // POST /api/products
    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(request));
    }

    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    // PATCH /api/products/{id}/stock
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(
                productService.updateStock(id, body.get("quantity")));
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/products/low-stock?threshold=10
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponse>> getLowStock(
            @RequestParam(required = false) Integer threshold) {
        return ResponseEntity.ok(productService.getLowStock(threshold));
    }
}
