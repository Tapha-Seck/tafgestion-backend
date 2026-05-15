package sn.tafgestion.backend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.tafgestion.backend.config.DataSourceConfig;
import sn.tafgestion.backend.config.TenantDataSource;
import sn.tafgestion.backend.dto.CreateTenantRequest;
import sn.tafgestion.backend.dto.TenantResponse;
import sn.tafgestion.backend.model.AppUser;
import sn.tafgestion.backend.model.Tenant;
import sn.tafgestion.backend.repository.AppUserRepository;
import sn.tafgestion.backend.repository.TenantRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSourceConfig dataSourceConfig;
    private final TenantDataSource tenantDataSource;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request) {

        if (tenantRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un tenant avec cet email existe déjà");
        }

        String schemaName = "schema_" + UUID.randomUUID()
                .toString().replace("-", "");

        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .country(request.getCountry() != null
                        ? request.getCountry() : "Sénégal")
                .currency(request.getCurrency() != null
                        ? request.getCurrency() : "FCFA")
                .tvaRate(request.getTvaRate() != null
                        ? request.getTvaRate() : 18.0)
                .invoicePrefix(request.getInvoicePrefix() != null
                        ? request.getInvoicePrefix() : "FAC")
                .schemaName(schemaName)
                .planId(request.getPlanId())
                .status("ACTIVE")
                .build();

        tenant = tenantRepository.save(tenant);

        // Créer le schéma PostgreSQL
        entityManager.createNativeQuery(
                        "SELECT create_tenant_schema(:schemaName)")
                .setParameter("schemaName", schemaName)
                .getSingleResult();

        // Enregistrer le DataSource avec l'UUID du tenant comme clé
        var newDs = dataSourceConfig.buildDataSource(schemaName);
        Map<Object, Object> sources = new HashMap<>(
                tenantDataSource.getResolvedDataSources()
        );
        sources.put(tenant.getId().toString(), newDs);
        tenantDataSource.setTargetDataSources(sources);
        tenantDataSource.afterPropertiesSet();

        // Créer le premier utilisateur ADMIN
        AppUser adminUser = AppUser.builder()
                .tenantId(tenant.getId())
                .email(request.getAdminEmail() != null
                        ? request.getAdminEmail() : request.getEmail())
                .password(passwordEncoder.encode(
                        request.getAdminPassword() != null
                                ? request.getAdminPassword() : "TafGestion2025!"))
                .firstName(request.getAdminFirstName())
                .lastName(request.getAdminLastName())
                .role("ADMIN")
                .active(true)
                .build();

        userRepository.save(adminUser);

        return TenantResponse.fromTenant(tenant);
    }

    // Charger tous les tenants au démarrage
    public void loadAllTenants() {
        tenantRepository.findAll().forEach(tenant -> {
            try {
                var ds = dataSourceConfig.buildDataSource(tenant.getSchemaName());
                Map<Object, Object> sources = new HashMap<>(
                        tenantDataSource.getResolvedDataSources()
                );
                // Clé = UUID du tenant (correspond au tenantId dans le JWT)
                sources.put(tenant.getId().toString(), ds);
                tenantDataSource.setTargetDataSources(sources);
            } catch (Exception e) {
                System.err.println("Erreur chargement tenant: "
                        + tenant.getSchemaName());
            }
        });
        tenantDataSource.afterPropertiesSet();
    }

    public List<TenantResponse> getAllTenants() {
        return tenantRepository.findAll()
                .stream()
                .map(TenantResponse::fromTenant)
                .collect(Collectors.toList());
    }

    public TenantResponse getTenantById(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant introuvable"));
        return TenantResponse.fromTenant(tenant);
    }

    @Transactional
    public TenantResponse updateStatus(UUID id, String status) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant introuvable"));
        tenant.setStatus(status);
        return TenantResponse.fromTenant(tenantRepository.save(tenant));
    }
}