CREATE TABLE paciente (
                          id         BIGSERIAL PRIMARY KEY,
                          nome       VARCHAR(150)        NOT NULL,
                          cpf        VARCHAR(14) UNIQUE  NOT NULL,
                          telefone   VARCHAR(20)         NOT NULL,
                          email      VARCHAR(150) UNIQUE NOT NULL,
                          criado_em  TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE TABLE nutricionista (
           id           BIGSERIAL PRIMARY KEY,
            nome         VARCHAR(150)        NOT NULL,
            email        VARCHAR(150) UNIQUE NOT NULL,
            telefone     VARCHAR(20)         NOT NULL,
            crn          VARCHAR(20) UNIQUE  NOT NULL,
            especialidade VARCHAR(100)       NOT NULL,
            criado_em    TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE TABLE disponibilidade_nutricionista (
            id                  BIGSERIAL PRIMARY KEY,
            nutricionista_id    BIGINT      NOT NULL REFERENCES nutricionista(id),
            dia_semana          VARCHAR(15) NOT NULL,
            hora_inicio         TIME        NOT NULL,
            hora_fim            TIME        NOT NULL,
            CONSTRAINT chk_horario CHECK (hora_fim > hora_inicio)
);

CREATE TABLE servico (
                         id               BIGSERIAL PRIMARY KEY,
                         nome             VARCHAR(100) NOT NULL,
                         descricao        TEXT,
                         duracao_minutos  INTEGER      NOT NULL,
                         CONSTRAINT chk_duracao CHECK (duracao_minutos > 0)
);

CREATE TYPE status_agendamento AS ENUM (
    'AGENDADO',
    'CANCELADO',
    'CONCLUIDO',
    'NAO_COMPARECEU'
);

CREATE TABLE agendamento (
                             id               BIGSERIAL PRIMARY KEY,
                             paciente_id      BIGINT               NOT NULL REFERENCES paciente(id),
                             nutricionista_id BIGINT               NOT NULL REFERENCES nutricionista(id),
                             servico_id       BIGINT               NOT NULL REFERENCES servico(id),
                             data             DATE                 NOT NULL,
                             hora_inicio      TIME                 NOT NULL,
                             hora_fim         TIME                 NOT NULL,
                             status           status_agendamento   NOT NULL DEFAULT 'AGENDADO',
                             criado_em        TIMESTAMP            NOT NULL DEFAULT NOW(),
                             CONSTRAINT chk_horario_agendamento CHECK (hora_fim > hora_inicio)
);