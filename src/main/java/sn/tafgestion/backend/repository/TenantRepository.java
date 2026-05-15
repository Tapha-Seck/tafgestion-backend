package sn.tafgestion.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.tafgestion.backend.model.Tenant;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByEmail(String email);
    Optional<Tenant> findBySchemaName(String schemaName);
    boolean existsByEmail(String email);
    boolean existsBySchemaName(String schemaName);
}