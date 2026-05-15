package sn.tafgestion.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean
    @Primary
    public TenantDataSource dataSource() {
        TenantDataSource routingDataSource = new TenantDataSource();

        // DataSource par défaut — schéma public
        HikariDataSource defaultDs = buildDataSource("public");

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("public", defaultDs);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(defaultDs);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }

    public HikariDataSource buildDataSource(String schema) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(dbUrl);
        ds.setUsername(dbUsername);
        ds.setPassword(dbPassword);
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setConnectionInitSql("SET search_path TO "
                + schema + ", public");
        ds.setMaximumPoolSize(5);
        ds.setMinimumIdle(1);
        ds.setPoolName("pool-" + schema);
        return ds;
    }
}