-- ============================================================
-- V1: базовая схема
--   scans     — запуски сканирований (заполняется парсером)
--   companies — seen-лист компаний для дедупликации
--   users     — получатели уведомлений и их роли
-- ============================================================

CREATE TABLE scans
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    scan_type       VARCHAR(20) NOT NULL
                    CHECK (scan_type IN ('FULL', 'CHINESE')),
    status          VARCHAR(20) NOT NULL DEFAULT 'RUNNING'
                    CHECK (status IN ('RUNNING', 'SUCCESS', 'PARTIAL', 'FAILED')),
    started_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    finished_at     TIMESTAMPTZ,
    companies_found INT         NOT NULL DEFAULT 0,
    new_companies   INT         NOT NULL DEFAULT 0,
    error_text      TEXT
);

CREATE TABLE companies
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    link               TEXT         NOT NULL,
    first_seen_scan_id BIGINT       REFERENCES scans (id),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_companies_link UNIQUE (link)
);

CREATE INDEX idx_companies_created_at ON companies (created_at);

CREATE TABLE users
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    telegram_id  BIGINT       UNIQUE,
    email        VARCHAR(255) UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    role         VARCHAR(20)  NOT NULL DEFAULT 'USER'
                 CHECK (role IN ('ADMIN', 'USER')),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);
