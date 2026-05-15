package sn.tafgestion.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sn.tafgestion.backend.service.TenantService;

@SpringBootApplication
@RequiredArgsConstructor
public class BackendApplication implements CommandLineRunner {

    private final TenantService tenantService;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Charger tous les tenants existants au démarrage
        tenantService.loadAllTenants();
        System.out.println("✅ Tous les tenants chargés !");
    }
}
