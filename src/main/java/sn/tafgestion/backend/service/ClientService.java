package sn.tafgestion.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.tafgestion.backend.dto.ClientRequest;
import sn.tafgestion.backend.dto.ClientResponse;
import sn.tafgestion.backend.model.Client;
import sn.tafgestion.backend.repository.ClientRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    // ── CRÉER ─────────────────────────────────────────────
    @Transactional
    public ClientResponse create(ClientRequest request) {

        // Générer code auto si non fourni
        String code = request.getCode();
        if (code == null || code.isBlank()) {
            Long maxId = clientRepository.findMaxId();
            long nextId = (maxId == null ? 0 : maxId) + 1;
            code = String.format("CLI-%03d", nextId);
        }

        if (clientRepository.existsByCode(code)) {
            throw new RuntimeException("Code déjà utilisé : " + code);
        }

        Client client = Client.builder()
                .code(code)
                .type(request.getType() != null
                        ? request.getType() : "INDIVIDUAL")
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .city(request.getCity())
                .address(request.getAddress())
                .creditLimit(request.getCreditLimit() != null
                        ? request.getCreditLimit() : BigDecimal.ZERO)
                .active(true)
                .build();

        return ClientResponse.fromClient(clientRepository.save(client));
    }

    // ── LISTE PAGINÉE ─────────────────────────────────────
    public Page<ClientResponse> getAll(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return clientRepository
                    .findByActiveTrueAndNameContainingIgnoreCase(
                            search, pageable)
                    .map(ClientResponse::fromClient);
        }
        return clientRepository.findByActiveTrue(pageable)
                .map(ClientResponse::fromClient);
    }

    // ── DÉTAIL ────────────────────────────────────────────
    public ClientResponse getById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        return ClientResponse.fromClient(client);
    }

    // ── MODIFIER ──────────────────────────────────────────
    @Transactional
    public ClientResponse update(Long id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        client.setType(request.getType());
        client.setName(request.getName());
        client.setPhone(request.getPhone());
        client.setEmail(request.getEmail());
        client.setCity(request.getCity());
        client.setAddress(request.getAddress());
        if (request.getCreditLimit() != null) {
            client.setCreditLimit(request.getCreditLimit());
        }

        return ClientResponse.fromClient(clientRepository.save(client));
    }

    // ── SUPPRIMER (soft delete) ───────────────────────────
    @Transactional
    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        client.setActive(false);
        clientRepository.save(client);
    }

    // ── RECHERCHE PAR CODE ────────────────────────────────
    public ClientResponse getByCode(String code) {
        Client client = clientRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        return ClientResponse.fromClient(client);
    }
}