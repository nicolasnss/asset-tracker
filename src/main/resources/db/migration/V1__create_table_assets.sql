-- Criação da tabela assets para o sistema de gestão de ativos
CREATE TABLE assets (
    id BIGSERIAL PRIMARY KEY,
    tag_patrimonio VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_aquisicao DATE,
    descricao TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índice para melhorar performance de buscas por tag_patrimonio
CREATE INDEX idx_assets_tag_patrimonio ON assets(tag_patrimonio);

-- Índice para melhorar performance de buscas por status
CREATE INDEX idx_assets_status ON assets(status);

-- Função para atualizar o campo updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para atualizar o updated_at quando um registro é modificado
CREATE TRIGGER trigger_update_assets_updated_at
BEFORE UPDATE ON assets
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
