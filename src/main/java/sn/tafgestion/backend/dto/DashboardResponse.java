package sn.tafgestion.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    // Chiffre d'affaires
    private BigDecimal caMois;
    private BigDecimal caAnnee;

    // Encaissements
    private BigDecimal encaisseMois;
    private BigDecimal enAttenteMois;

    // Compteurs
    private Long totalClients;
    private Long totalProduits;
    private Long facturesMois;
    private Long facturesPayeesMois;
    private Long facturesEnAttente;

    // Alertes
    private Long produitsStockBas;
}
