ALTER TABLE assets ADD COLUMN funcionario_id BIGINT;
ALTER TABLE assets ADD CONSTRAINT fk_assets_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id);