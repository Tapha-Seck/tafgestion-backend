package sn.tafgestion.backend.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import sn.tafgestion.backend.security.TenantContext;

public class TenantDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getTenantId();
        // Si pas de tenantId ou "public" → schéma public
        if (tenantId == null || tenantId.isBlank()
                || tenantId.equals("public")) {
            return "public";
        }
        return tenantId;
    }
}