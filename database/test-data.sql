-- Données de test pour PayMyBuddy
-- Insertion d'utilisateurs de test et de quelques transactions

-- Utilisateurs de test (mots de passe: password123)
INSERT INTO users (email, password, first_name, last_name, balance) VALUES
('john.doe@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'John', 'Doe', 500.00),
('jane.smith@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Jane', 'Smith', 750.50),
('bob.wilson@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Bob', 'Wilson', 300.25),
('alice.brown@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Alice', 'Brown', 1000.00);

-- Connexions d'amis
INSERT INTO connections (user_id, friend_id) VALUES
(1, 2), -- John et Jane sont amis
(2, 1), -- Relation bidirectionnelle
(1, 3), -- John et Bob sont amis  
(3, 1), -- Relation bidirectionnelle
(2, 4), -- Jane et Alice sont amis
(4, 2), -- Relation bidirectionnelle
(3, 4), -- Bob et Alice sont amis
(4, 3); -- Relation bidirectionnelle

-- Transactions de test
INSERT INTO transactions (sender_id, receiver_id, amount, description, status) VALUES
(1, 2, 50.00, 'Remboursement restaurant', 'COMPLETED'),
(2, 1, 25.00, 'Partage Uber', 'COMPLETED'),
(1, 3, 100.00, 'Cadeau anniversaire', 'COMPLETED'),
(4, 2, 75.50, 'Courses partagées', 'COMPLETED'),
(3, 4, 30.00, 'Cinéma', 'PENDING');
