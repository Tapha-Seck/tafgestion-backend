package sn.tafgestion.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.tafgestion.backend.dto.CreateInvoiceRequest;
import sn.tafgestion.backend.dto.InvoiceResponse;
import sn.tafgestion.backend.model.Client;
import sn.tafgestion.backend.model.Invoice;
import sn.tafgestion.backend.model.InvoiceLine;
import sn.tafgestion.backend.repository.ClientRepository;
import sn.tafgestion.backend.repository.InvoiceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;

    // ── CRÉER ─────────────────────────────────────────────
    @Transactional
    public InvoiceResponse create(CreateInvoiceRequest request) {

        // 1. Vérifier le client
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        // 2. Générer numéro de facture auto
        String number = generateInvoiceNumber();

        // 3. Calculer les totaux CÔTÉ SERVEUR
        BigDecimal subtotalHt = BigDecimal.ZERO;
        List<InvoiceLine> lines = request.getLines().stream()
                .map(l -> {
                    BigDecimal totalHt = l.getUnitPrice()
                            .multiply(l.getQuantity())
                            .setScale(2, RoundingMode.HALF_UP);
                    return InvoiceLine.builder()
                            .productId(l.getProductId())
                            .description(l.getDescription())
                            .quantity(l.getQuantity())
                            .unitPrice(l.getUnitPrice())
                            .totalHt(totalHt)
                            .build();
                })
                .collect(Collectors.toList());

        for (InvoiceLine line : lines) {
            subtotalHt = subtotalHt.add(line.getTotalHt());
        }

        // 4. Calculer TVA et TTC
        BigDecimal tvaRate = request.getTvaRate() != null
                ? request.getTvaRate() : new BigDecimal("18");
        BigDecimal tvaAmount = subtotalHt
                .multiply(tvaRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal totalTtc = subtotalHt.add(tvaAmount);

        // 5. Créer la facture
        Invoice invoice = Invoice.builder()
                .number(number)
                .clientId(client.getId())
                .date(request.getDate())
                .dueDate(request.getDueDate())
                .status("PENDING")
                .subtotalHt(subtotalHt)
                .tvaRate(tvaRate)
                .tvaAmount(tvaAmount)
                .totalTtc(totalTtc)
                .notes(request.getNotes())
                .build();

        invoice = invoiceRepository.save(invoice);

        // 6. Sauvegarder les lignes avec l'id de la facture
        final Long invoiceId = invoice.getId();
        lines.forEach(l -> l.setInvoiceId(invoiceId));
        invoice.setLines(lines);
        invoice = invoiceRepository.save(invoice);

        // 7. Enrichir avec le nom du client
        InvoiceResponse response = InvoiceResponse.fromInvoice(invoice);
        response.setClientName(client.getName());
        return response;
    }

    // ── LISTE PAGINÉE ─────────────────────────────────────
    public Page<InvoiceResponse> getAll(
            String status, LocalDate start,
            LocalDate end, Pageable pageable) {

        Page<Invoice> invoices;

        if (status != null && start != null && end != null) {
            invoices = invoiceRepository.findByStatusAndDateBetween(
                    status, start, end, pageable);
        } else if (status != null) {
            invoices = invoiceRepository.findByStatus(status, pageable);
        } else if (start != null && end != null) {
            invoices = invoiceRepository.findByDateBetween(
                    start, end, pageable);
        } else {
            invoices = invoiceRepository.findAll(pageable);
        }

        return invoices.map(invoice -> {
            InvoiceResponse r = InvoiceResponse.fromInvoice(invoice);
            clientRepository.findById(invoice.getClientId())
                    .ifPresent(c -> r.setClientName(c.getName()));
            return r;
        });
    }

    // ── DÉTAIL ────────────────────────────────────────────
    public InvoiceResponse getById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture introuvable"));
        InvoiceResponse response = InvoiceResponse.fromInvoice(invoice);
        clientRepository.findById(invoice.getClientId())
                .ifPresent(c -> response.setClientName(c.getName()));
        return response;
    }

    // ── MARQUER PAYÉE ─────────────────────────────────────
    @Transactional
    public InvoiceResponse markAsPaid(Long id, String paymentMode) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture introuvable"));

        if ("PAID".equals(invoice.getStatus())) {
            throw new RuntimeException("Facture déjà payée");
        }

        invoice.setStatus("PAID");
        invoice.setPaymentMode(paymentMode);
        invoice.setPaidAt(LocalDateTime.now());

        return InvoiceResponse.fromInvoice(invoiceRepository.save(invoice));
    }

    // ── ANNULER ───────────────────────────────────────────
    @Transactional
    public InvoiceResponse cancel(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture introuvable"));

        if ("PAID".equals(invoice.getStatus())) {
            throw new RuntimeException("Impossible d'annuler une facture payée");
        }

        invoice.setStatus("CANCELLED");
        return InvoiceResponse.fromInvoice(invoiceRepository.save(invoice));
    }

    // ── NUMÉROTATION AUTO ─────────────────────────────────
    private String generateInvoiceNumber() {
        int year = Year.now().getValue();
        Long maxId = invoiceRepository.findMaxId();
        long nextId = (maxId == null ? 0 : maxId) + 1;
        return String.format("FAC-%d-%03d", year, nextId);
    }

    // ── RÉSUMÉ FINANCIER ──────────────────────────────────
    public java.util.Map<String, BigDecimal> getSummary(
            LocalDate start, LocalDate end) {
        BigDecimal paid = invoiceRepository
                .sumPaidBetween(start, end);
        BigDecimal pending = invoiceRepository
                .sumPendingBetween(start, end);
        return java.util.Map.of(
                "paid", paid,
                "pending", pending,
                "total", paid.add(pending)
        );
    }
}