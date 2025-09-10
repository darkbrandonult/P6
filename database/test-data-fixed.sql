-- Données de test pour PayMyBuddy avec mots de passe BCrypt corrects
-- Insertion d'utilisateurs de test et de quelques transactions
-- TOUS LES MOTS DE PASSE SONT: password123

-- Nettoyage des données existantes
DELETE FROM transactions;
DELETE FROM connections;
DELETE FROM users;

-- Réinitialiser l'auto-increment
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE transactions AUTO_INCREMENT = 1;
ALTER TABLE connections AUTO_INCREMENT = 1;

-- Utilisateurs de test (mots de passe: password123)
-- Hash BCrypt pour "password123": $2a$10$e0MYzXyjpJS7Pd0RVvHwHe.CV6qwu0pV.hwDJ8CQPJgT3Q7J9Y3E6
INSERT INTO users (email, password, first_name, last_name, balance) VALUES
('john.doe@email.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe.CV6qwu0pV.hwDJ8CQPJgT3Q7J9Y3E6', 'John', 'Doe', 500.00),
('jane.smith@email.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe.CV6qwu0pV.hwDJ8CQPJgT3Q7J9Y3E6', 'Jane', 'Smith', 750.50),
('bob.wilson@email.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe.CV6qwu0pV.hwDJ8CQPJgT3Q7J9Y3E6', 'Bob', 'Wilson', 300.25),
('alice.brown@email.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe.CV6qwu0pV.hwDJ8CQPJgT3Q7J9Y3E6', 'Alice', 'Brown', 1000.00),
('test@test.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe.CV6qwu0pV.hwDJ8CQPJgT3Q7J9Y3E6', 'Test', 'User', 250.00);

-- Connexions d'amis
INSERT INTO connections (user_id, friend_id) VALUES
(1, 2), -- John et Jane sont amis
(2, 1), -- Relation bidirectionnelle
(1, 3), -- John et Bob sont amis  
(3, 1), -- Relation bidirectionnelle
(2, 4), -- Jane et Alice sont amis
(4, 2), -- Relation bidirectionnelle
(3, 4), -- Bob et Alice sont amis
(4, 3), -- Relation bidirectionnelle
(5, 1), -- Test user et John sont amis
(1, 5); -- Relation bidirectionnelle

-- Transactions de test
INSERT INTO transactions (sender_id, receiver_id, amount, description, status) VALUES
(1, 2, 50.00, 'Remboursement restaurant', 'COMPLETED'),
(2, 1, 25.00, 'Partage Uber', 'COMPLETED'),
(1, 3, 100.00, 'Cadeau anniversaire', 'COMPLETED'),
(4, 2, 75.50, 'Courses partagées', 'COMPLETED'),
(3, 4, 30.00, 'Cinéma', 'PENDING'),
(5, 1, 15.00, 'Café', 'COMPLETED');

-- Vérification des données insérées
SELECT 'Users count:' as info, COUNT(*) as count FROM users
UNION ALL
SELECT 'Connections count:' as info, COUNT(*) as count FROM connections
UNION ALL
SELECT 'Transactions count:' as info, COUNT(*) as count FROM transactions;

-- Affichage des utilisateurs avec leurs emails pour vérification
SELECT id, email, first_name, last_name, balance FROM users ORDER BY id;
