package sn.tafgestion.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    // Période sélectionnée
    private String period;
    private String dateFrom;
    private String dateTo;

    // Chiffre d'affaires
    private BigDecimal caPeriode;
    private BigDecimal caAnnee;

    // Encaissements
    private BigDecimal encaissePeriode;
    private BigDecimal enAttentePeriode;

    // Compteurs
    private Long totalClients;
    private Long totalProduits;
    private Long facturesPeriode;
    private Long facturesPayeesPeriode;
    private Long facturesEnAttente;

    // Alertes
    private Long produitsStockBas;

    // Graphique CA par jour/semaine/mois
    private List<Map<String, Object>> graphiqueCA;
}