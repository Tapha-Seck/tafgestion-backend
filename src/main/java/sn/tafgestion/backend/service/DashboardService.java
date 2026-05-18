package sn.tafgestion.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.tafgestion.backend.dto.DashboardResponse;
import sn.tafgestion.backend.repository.ClientRepository;
import sn.tafgestion.backend.repository.InvoiceRepository;
import sn.tafgestion.backend.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    public DashboardResponse getStats(String period) {

        LocalDate now = LocalDate.now();
        LocalDate start;
        LocalDate end = now;

        // Définir la période
        switch (period != null ? period.toUpperCase() : "MONTH") {
            case "DAY"   -> start = now;
            case "WEEK"  -> start = now.minusDays(6);
            case "YEAR"  -> start = now.withDayOfYear(1);
            default      -> start = now.withDayOfMonth(1); // MONTH
        }

        LocalDate startOfYear = now.withDayOfYear(1);

        // CA période
        BigDecimal caPeriode = invoiceRepository.sumPaidBetween(start, end);
        if (caPeriode == null) caPeriode = BigDecimal.ZERO;

        // CA année
        BigDecimal caAnnee = invoiceRepository.sumPaidBetween(startOfYear, end);
        if (caAnnee == null) caAnnee = BigDecimal.ZERO;

        // Encaissé période
        BigDecimal encaissePeriode = invoiceRepository.sumPaidBetween(start, end);
        if (encaissePeriode == null) encaissePeriode = BigDecimal.ZERO;

        // En attente période
        BigDecimal enAttentePeriode = invoiceRepository.sumPendingBetween(start, end);
        if (enAttentePeriode == null) enAttentePeriode = BigDecimal.ZERO;

        // Compteurs
        long totalClients  = clientRepository.count();
        long totalProduits = productRepository.count();

        // Factures période
        long facturesPeriode = invoiceRepository
                .findByDateBetween(start, end,
                        org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        // Factures payées période
        long facturesPayeesPeriode = invoiceRepository
                .findByStatusAndDateBetween("PAID", start, end,
                        org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        // Factures en attente total
        long facturesEnAttente = invoiceRepository
                .findByStatus("PENDING",
                        org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        // Produits stock bas
        long produitsStockBas = productRepository
                .findByActiveTrueAndStockLessThan(10).size();

        // Graphique CA
        List<Map<String, Object>> graphiqueCA =
                buildGraphique(period, start, end);

        return DashboardResponse.builder()
                .period(period != null ? period.toUpperCase() : "MONTH")
                .dateFrom(start.toString())
                .dateTo(end.toString())
                .caPeriode(caPeriode)
                .caAnnee(caAnnee)
                .encaissePeriode(encaissePeriode)
                .enAttentePeriode(enAttentePeriode)
                .totalClients(totalClients)
                .totalProduits(totalProduits)
                .facturesPeriode(facturesPeriode)
                .facturesPayeesPeriode(facturesPayeesPeriode)
                .facturesEnAttente(facturesEnAttente)
                .produitsStockBas(produitsStockBas)
                .graphiqueCA(graphiqueCA)
                .build();
    }

    // ── Construire les données du graphique ───────────────
    private List<Map<String, Object>> buildGraphique(
            String period, LocalDate start, LocalDate end) {

        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

        if ("YEAR".equalsIgnoreCase(period)) {
            // Grouper par mois
            for (int m = 1; m <= 12; m++) {
                LocalDate mStart = LocalDate.of(
                        end.getYear(), m, 1);
                LocalDate mEnd = mStart.withDayOfMonth(
                        mStart.lengthOfMonth());
                if (mStart.isAfter(end)) break;

                BigDecimal ca = invoiceRepository
                        .sumPaidBetween(mStart, mEnd);
                Map<String, Object> point = new HashMap<>();
                point.put("label", mStart.getMonth()
                        .getDisplayName(
                                java.time.format.TextStyle.SHORT,
                                java.util.Locale.FRENCH));
                point.put("ca", ca != null ? ca : BigDecimal.ZERO);
                result.add(point);
            }
        } else {
            // Grouper par jour
            LocalDate cursor = start;
            while (!cursor.isAfter(end)) {
                BigDecimal ca = invoiceRepository
                        .sumPaidBetween(cursor, cursor);
                Map<String, Object> point = new HashMap<>();
                point.put("label", cursor.format(fmt));
                point.put("ca", ca != null ? ca : BigDecimal.ZERO);
                result.add(point);
                cursor = cursor.plusDays(1);
            }
        }
        return result;
    }
}