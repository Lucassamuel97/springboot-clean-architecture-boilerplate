-- Seed inicial de dados para desenvolvimento

-- Inserir usuário ADMIN
-- Senha: admin123 (bcrypt hash)
INSERT INTO users (id, username, email, password, active, created_at, updated_at, deleted_at)
VALUES (
    'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d',
    'admin',
    'admin@example.com',
    '$2a$10$zbFqVpnvJmEbLIalYsS2P.oHlL4y6QT5MiM8dS9wupteiefFHkxiK',
    true,
    NOW(6),
    NOW(6),
    NULL
);

-- Inserir role ADMIN para o usuário admin
INSERT INTO user_roles (user_id, role)
VALUES ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'ADMIN');

-- Inserir usuário USER
-- Senha: user123 (bcrypt hash)
INSERT INTO users (id, username, email, password, active, created_at, updated_at, deleted_at)
VALUES (
    'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e',
    'user',
    'user@example.com',
    '$2a$10$08y9cvFLywH25z3sy0yx1O1vh8iClNHHoAcA24C4s98/X9jiY9NU.',
    true,
    NOW(6),
    NOW(6),
    NULL
);

-- Inserir role USER para o usuário user
INSERT INTO user_roles (user_id, role)
VALUES ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'USER');

-- Inserir item de teste
INSERT INTO items (id, name, description, price, created_at, updated_at, deleted_at)
VALUES (
    'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f',
    'Produto Teste',
    'Este é um item de teste criado pelo seeder para demonstração do sistema',
    99.99,
    NOW(6),
    NOW(6),
    NULL
);
