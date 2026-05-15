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
import sn.tafgestion.backend.dto.ClientRequest;
import sn.tafgestion.backend.dto.ClientResponse;
import sn.tafgestion.backend.service.ClientService;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientService clientService;

    // GET /api/clients?search=xxx&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<ClientResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return ResponseEntity.ok(clientService.getAll(search, pageable));
    }

    // GET /api/clients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    // GET /api/clients/code/{code}
    @GetMapping("/code/{code}")
    public ResponseEntity<ClientResponse> getByCode(
            @PathVariable String code) {
        return ResponseEntity.ok(clientService.getByCode(code));
    }

    // POST /api/clients
    @PostMapping
    public ResponseEntity<ClientResponse> create(
            @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.create(request));
    }

    // PUT /api/clients/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.update(id, request));
    }

    // DELETE /api/clients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
