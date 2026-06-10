INSERT INTO category (id, name, description) VALUES (1, 'Sopas y Sancochos', 'Caldos, sopas y sancochos tradicionales') ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
INSERT INTO category (id, name, description) VALUES (2, 'Carnes y Asados', 'Cortes de carne, parrillas y asados') ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
INSERT INTO category (id, name, description) VALUES (3, 'Mariscos', 'Pescados, mariscos y frutos del mar') ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
INSERT INTO category (id, name, description) VALUES (4, 'Pastas y Arroces', 'Pastas italianas, arroces y risottos') ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
INSERT INTO category (id, name, description) VALUES (5, 'Comida Rápida', 'Hamburguesas, perros calientes y snacks') ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
INSERT INTO category (id, name, description) VALUES (6, 'Vegetariano', 'Platos sin carne para dietas vegetarianas y veganas') ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
INSERT INTO category (id, name, description) VALUES (7, 'Postres', 'Dulces, tortas, helados y postres') ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
INSERT INTO category (id, name, description) VALUES (8, 'Bebidas', 'Jugos naturales, gaseosas, café y bebidas frías') ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
