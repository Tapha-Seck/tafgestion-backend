-- Cette migration crée une fonction qui sera appelée
-- pour chaque nouveau tenant créé

-- Fonction de création du schéma tenant
CREATE OR REPLACE FUNCTION create_tenant_schema(schema_name TEXT)
RETURNS void AS $$
BEGIN
    EXECUTE format('CREATE SCHEMA IF NOT EXISTS %I', schema_name);

    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I.product (
            id          BIGSERIAL PRIMARY KEY,
            reference   VARCHAR(50),
            name        VARCHAR(200) NOT NULL,
            category    VARCHAR(100),
            price_ht    DECIMAL(15,2) NOT NULL DEFAULT 0,
            stock       INTEGER DEFAULT 0,
            description TEXT,
            active      BOOLEAN DEFAULT true,
            created_at  TIMESTAMP DEFAULT NOW()
        )', schema_name);

    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I.client (
            id           BIGSERIAL PRIMARY KEY,
            code         VARCHAR(50),
            type         VARCHAR(20) DEFAULT ''INDIVIDUAL'',
            name         VARCHAR(200) NOT NULL,
            phone        VARCHAR(50),
            email        VARCHAR(200),
            city         VARCHAR(100),
            address      TEXT,
            credit_limit DECIMAL(15,2) DEFAULT 0,
            active       BOOLEAN DEFAULT true,
            created_at   TIMESTAMP DEFAULT NOW()
        )', schema_name);

    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I.invoice (
            id           BIGSERIAL PRIMARY KEY,
            number       VARCHAR(50) NOT NULL UNIQUE,
            client_id    BIGINT REFERENCES %I.client(id),
            date         DATE NOT NULL,
            due_date     DATE,
            status       VARCHAR(20) DEFAULT ''PENDING'',
            payment_mode VARCHAR(50),
            subtotal_ht  DECIMAL(15,2) DEFAULT 0,
            tva_rate     DECIMAL(5,2) DEFAULT 18,
            tva_amount   DECIMAL(15,2) DEFAULT 0,
            total_ttc    DECIMAL(15,2) DEFAULT 0,
            notes        TEXT,
            paid_at      TIMESTAMP,
            created_at   TIMESTAMP DEFAULT NOW()
        )', schema_name, schema_name);

    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I.invoice_line (
            id          BIGSERIAL PRIMARY KEY,
            invoice_id  BIGINT REFERENCES %I.invoice(id) ON DELETE CASCADE,
            product_id  BIGINT,
            description VARCHAR(500),
            quantity    DECIMAL(10,2) DEFAULT 1,
            unit_price  DECIMAL(15,2) DEFAULT 0,
            total_ht    DECIMAL(15,2) DEFAULT 0
        )', schema_name, schema_name);

    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I.setting (
            key   VARCHAR(100) PRIMARY KEY,
            value TEXT,
            type  VARCHAR(50) DEFAULT ''STRING''
        )', schema_name);

    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I.app_user (
            id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
            email       VARCHAR(200) NOT NULL UNIQUE,
            password    VARCHAR(255) NOT NULL,
            first_name  VARCHAR(100),
            last_name   VARCHAR(100),
            role        VARCHAR(20) DEFAULT ''USER'',
            active      BOOLEAN DEFAULT true,
            created_at  TIMESTAMP DEFAULT NOW()
        )', schema_name);

END;
$$ LANGUAGE plpgsql;