CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          login VARCHAR(100) NOT NULL UNIQUE,
                          senha VARCHAR(255) NOT NULL,
                          role VARCHAR(50) NOT NULL
);

-- O INSERT correto deve ser apenas os valores entre aspas:
INSERT INTO usuarios (login, senha, role)
VALUES ('nicolas', '{noop}nicolas123', 'ADMIN');