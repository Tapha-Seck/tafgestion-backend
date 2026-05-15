package sn.tafgestion.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.tafgestion.backend.dto.DashboardResponse;
import sn.tafgestion.backend.repository.ClientRepository;
import sn.tafgestion.backend.repository.InvoiceRepository;
import sn.tafgestion.backend.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    public DashboardResponse getStats() {

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate startOfYear = now.withDayOfYear(1);

        // CA mois
        BigDecimal caMois = invoiceRepository
                .sumPaidBetween(startOfMonth, now);
        if (caMois == null) caMois = BigDecimal.ZERO;

        // CA année
        BigDecimal caAnnee = invoiceRepository
                .sumPaidBetween(startOfYear, now);
        if (caAnnee == null) caAnnee = BigDecimal.ZERO;

        // Encaissé ce mois
        BigDecimal encaisseMois = invoiceRepository
                .sumPaidBetween(startOfMonth, now);
        if (encaisseMois == null) encaisseMois = BigDecimal.ZERO;

        // En attente ce mois
        BigDecimal enAttenteMois = invoiceRepository
                .sumPendingBetween(startOfMonth, now);
        if (enAttenteMois == null) enAttenteMois = BigDecimal.ZERO;

        // Compteurs clients et produits
        long totalClients = clientRepository.count();
        long totalProduits = productRepository.count();

        // Factures du mois
        long facturesMois = invoiceRepository
                .findByDateBetween(startOfMonth, now,
                        org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        // Factures payées du mois
        long facturesPayeesMois = invoiceRepository
                .findByStatusAndDateBetween("PAID", startOfMonth, now,
                        org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        // Factures en attente
        long facturesEnAttente = invoiceRepository
                .findByStatus("PENDING",
                        org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        // Produits stock bas (< 10)
        long produitsStockBas = productRepository
                .findByActiveTrueAndStockLessThan(10).size();

        return DashboardResponse.builder()
                .caMois(caMois)
                .caAnnee(caAnnee)
                .encaisseMois(encaisseMois)
                .enAttenteMois(enAttenteMois)
                .totalClients(totalClients)
                .totalProduits(totalProduits)
                .facturesMois(facturesMois)
                .facturesPayeesMois(facturesPayeesMois)
                .facturesEnAttente(facturesEnAttente)
                .produitsStockBas(produitsStockBas)
                .build();
    }
}
