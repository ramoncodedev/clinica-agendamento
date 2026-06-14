-- Remove o DEFAULT que depende do tipo ENUM
ALTER TABLE agendamento ALTER COLUMN status DROP DEFAULT;

-- Converte a coluna de ENUM para VARCHAR
ALTER TABLE agendamento ALTER COLUMN status TYPE VARCHAR(20);

-- Agora pode remover o tipo ENUM sem dependências
DROP TYPE status_agendamento;

-- Restaura o DEFAULT como string simples
ALTER TABLE agendamento ALTER COLUMN status SET DEFAULT 'AGENDADO';