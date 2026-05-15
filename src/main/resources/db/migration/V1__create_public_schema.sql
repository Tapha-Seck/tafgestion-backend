-- Table Plan (les offres TafGestion)
CREATE TABLE plan (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50)    NOT NULL,
    price       DECIMAL(12,2)  NOT NULL,
    billing_cycle VARCHAR(20)  NOT NULL,
    max_users   INTEGER        DEFAULT 5,
    max_products INTEGER       DEFAULT 500,
    max_clients  INTEGER       DEFAULT 1000,
    created_at  TIMESTAMP      DEFAULT NOW()
);

-- Insertion des 3 plans TafGestion
INSERT INTO plan (name, price, billing_cycle, max_users, max_products, max_clients)
VALUES
    ('MONTHLY', 10000.00, 'MONTHLY', 5, 500, 1000),
    ('ANNUAL',  100000.00, 'YEARLY', 10, 2000, 5000),
    ('LOCAL',   250000.00, 'ONE_TIME', 999, 999999, 999999);

-- Table Tenant (chaque entreprise cliente)
CREATE TABLE tenant (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200)  NOT NULL,
    email           VARCHAR(200)  NOT NULL UNIQUE,
    phone           VARCHAR(50),
    address         TEXT,
    country         VARCHAR(100)  DEFAULT 'Sénégal',
    currency        VARCHAR(10)   DEFAULT 'FCFA',
    tva_rate        DECIMAL(5,2)  DEFAULT 18.00,
    invoice_prefix  VARCHAR(20)   DEFAULT 'FAC',
    invoice_footer  TEXT,
    logo_url        VARCHAR(500),
    primary_color   VARCHAR(7)    DEFAULT '#1565C0',
    schema_name     VARCHAR(100)  NOT NULL UNIQUE,
    plan_id         BIGINT        REFERENCES plan(id),
    status          VARCHAR(20)   DEFAULT 'ACTIVE',
    created_at      TIMESTAMP     DEFAULT NOW()
);

-- Table Subscription (abonnements)
CREATE TABLE subscription (
    id          BIGSERIAL     PRIMARY KEY,
    tenant_id   UUID          NOT NULL REFERENCES tenant(id),
    plan_id     BIGINT        NOT NULL REFERENCES plan(id),
    start_date  DATE          NOT NULL,
    end_date    DATE,
    status      VARCHAR(20)   DEFAULT 'ACTIVE',
    amount      DECIMAL(12,2) NOT NULL,
    created_at  TIMESTAMP     DEFAULT NOW()
);

-- Table User public (Super Admin)
CREATE TABLE app_user (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         REFERENCES tenant(id),
    email       VARCHAR(200) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    role        VARCHAR(20)  DEFAULT 'ADMIN',
    active      BOOLEAN      DEFAULT true,
    created_at  TIMESTAMP    DEFAULT NOW()
);

-- Table Refresh Token
CREATE TABLE refresh_token (
    id          BIGSERIAL    PRIMARY KEY,
    token       VARCHAR(500) NOT NULL UNIQUE,
    user_id     UUID         NOT NULL REFERENCES app_user(id),
    expiry_date TIMESTAMP    NOT NULL,
    created_at  TIMESTAMP    DEFAULT NOW()
);