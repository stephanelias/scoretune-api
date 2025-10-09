INSERT INTO users (full_name, email, password)
VALUES ('Useradmin', 'admin@test.com', '$2a$12$JKa9e..ScfipubkjSFDgWOKWYgBi.X1qxmNcCFd91KChkRl9ZJ6SS');

WITH admin_user AS (
    SELECT id FROM users WHERE email = 'admin@test.com'
)
INSERT INTO user_roles (user_id, roles)
SELECT id, 'ROLE_ADMIN' FROM admin_user;

-- Insérer un modo
INSERT INTO users (full_name, email, password)
VALUES ('Modo User', 'modo@test.com', '$2a$12$JKa9e..ScfipubkjSFDgWOKWYgBi.X1qxmNcCFd91KChkRl9ZJ6SS');

-- Récupérer l'UUID du modo et lui assigner le rôle
WITH modo_user AS (
    SELECT id FROM users WHERE email = 'modo@test.com'
)
INSERT INTO user_roles (user_id, roles)
SELECT id, 'ROLE_MODO' FROM modo_user;