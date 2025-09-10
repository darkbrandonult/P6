# PayMyBuddy - Application de Transfert d'Argent entre Amis

## 📝 Description du Projet

PayMyBuddy est une application web de transfert d'argent entre amis développée avec Spring Boot dans le cadre du **Projet P6 - Formation Développeur d'Application Java**. Cette application permet aux utilisateurs de gérer facilement leurs transactions financières avec leurs amis de manière sécurisée.

## ✨ Fonctionnalités Implémentées

### 🔐 Gestion des Utilisateurs
- **Inscription** avec validation d'email unique
- **Connexion sécurisée** avec Spring Security
- **Gestion de profil** utilisateur
- **Chiffrement des mots de passe** avec BCrypt

### 👥 Gestion des Relations
- **Ajout d'amis** par adresse email
- **Visualisation de la liste d'amis**
- **Gestion des connexions** bidirectionnelles

### 💰 Système de Transactions
- **Transferts d'argent** entre utilisateurs connectés
- **Historique des transactions** complet
- **Gestion des soldes** automatique
- **Validation des montants** et vérification des fonds

### 🎨 Interface Utilisateur
- **Design responsive** avec Bootstrap 5
- **Interface multilingue** (français)
- **Conformité WCAG** pour l'accessibilité
- **Expérience utilisateur optimisée**

## 🏗️ Architecture Technique

### Stack Technologique
- **Backend**: Java 21, Spring Boot 3.2.0
- **Sécurité**: Spring Security avec authentification personnalisée
- **Persistance**: Spring Data JPA, Hibernate
- **Base de données**: MySQL 8.0+ (production), H2 (tests)
- **Frontend**: Thymeleaf, Bootstrap 5
- **Build**: Maven 3.6+

### Modèle Physique de Données (MPD)

```sql
-- Table des utilisateurs
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table des relations d'amitié
CREATE TABLE connections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_connection (user_id, friend_id)
);

-- Table des transactions
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    description VARCHAR(255),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'COMPLETED',
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);
```

### Structure du Projet
```
src/
├── main/
│   ├── java/com/paymybuddy/
│   │   ├── PayMyBuddyApplication.java  # Classe principale
│   │   ├── config/                     # Configuration Spring
│   │   │   ├── SecurityConfig.java     # Configuration sécurité
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── DataInitializer.java    # Données de test
│   │   ├── controller/                 # Contrôleurs MVC
│   │   │   ├── AuthController.java     # Authentification
│   │   │   └── DashboardController.java # Tableau de bord
│   │   ├── model/                      # Entités JPA
│   │   │   ├── User.java              # Entité utilisateur
│   │   │   ├── Connection.java        # Entité relation
│   │   │   ├── Transaction.java       # Entité transaction
│   │   │   └── TransactionStatus.java # Énumération statut
│   │   ├── repository/                # Couche d'accès données
│   │   │   ├── UserRepository.java
│   │   │   ├── ConnectionRepository.java
│   │   │   └── TransactionRepository.java
│   │   └── service/                   # Logique métier
│   │       ├── UserService.java
│   │       ├── ConnectionService.java
│   │       └── TransactionService.java
│   └── resources/
│       ├── templates/                 # Vues Thymeleaf
│       │   ├── index.html            # Page d'accueil
│       │   ├── auth/                 # Pages authentification
│       │   │   ├── login.html
│       │   │   └── register.html
│       │   └── dashboard/            # Pages tableau de bord
│       │       ├── index.html
│       │       ├── friends.html
│       │       ├── transactions.html
│       │       ├── transfer.html
│       │       └── profile.html
│       ├── static/css/               # Feuilles de style
│       │   └── style.css
│       └── application.properties    # Configuration application
└── test/                            # Tests unitaires et d'intégration
```

## 🛠️ Installation et Configuration

### Prérequis
- **Java 21** ou supérieur
- **Maven 3.6+**
- **MySQL 8.0+** (ou MariaDB 10.5+)

### Étapes d'Installation

1. **Cloner le projet**
   ```bash
   git clone [URL_DU_DEPOT]
   cd PayMyBuddy
   ```

2. **Configuration de la base de données**
   ```sql
   CREATE DATABASE paymybuddy CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Configuration application**
   ```properties
   # Configuration dans src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/paymybuddy
   spring.datasource.username=root
   spring.datasource.password=votre_mot_de_passe
   ```

4. **Initialisation des données**
   ```bash
   # Exécuter le script de schéma
   mysql -u root -p paymybuddy < database/schema.sql
   ```

5. **Compilation et lancement**
   ```bash
   mvn clean package
   java -jar target/paymybuddy-0.0.1-SNAPSHOT.jar
   ```

## 🚀 Démarrage Rapide

### Démarrage de l'Application
```bash
# Option 1: Avec Maven
mvn spring-boot:run

# Option 2: Avec JAR compilé
mvn clean package -DskipTests
java -Xmx128m -XX:+UseSerialGC -jar target/paymybuddy-0.0.1-SNAPSHOT.jar
```

### Accès à l'Application
- **URL**: http://localhost:8080
- **Port**: 8080 (configurable)

### Comptes de Test
L'application crée automatiquement des comptes de test au démarrage :
- **john.doe@email.com** / `password123`
- **jane.smith@email.com** / `password123`

## 🔒 Sécurité Implémentée

### Authentification et Autorisation
- **Spring Security** avec configuration personnalisée
- **Chiffrement BCrypt** pour les mots de passe
- **Sessions sécurisées** avec timeout automatique
- **Protection CSRF** sur tous les formulaires

### Validation des Données
- **Validation côté serveur** avec Bean Validation
- **Contraintes de base de données** (clés étrangères, contraintes CHECK)
- **Gestion des erreurs** avec messages utilisateur appropriés

## 🧪 Tests et Qualité

### Exécution des Tests
```bash
# Tests unitaires et d'intégration
mvn test

# Tests avec couverture
mvn test jacoco:report
```

### Fonctionnalités Testées
- ✅ Authentification utilisateur
- ✅ Création et gestion des comptes
- ✅ Ajout d'amis et gestion des relations
- ✅ Transferts d'argent et gestion des soldes
- ✅ Validation des contraintes métier

## 📊 Diagramme de Classes (Entités Principales)

```
User
├── id: Long
├── email: String (UNIQUE)
├── password: String (ENCRYPTED)
├── firstName: String
├── lastName: String
├── balance: BigDecimal
├── createdAt: LocalDateTime
└── updatedAt: LocalDateTime

Connection
├── id: Long
├── user: User (ManyToOne)
├── friend: User (ManyToOne)
└── createdAt: LocalDateTime

Transaction
├── id: Long
├── sender: User (ManyToOne)
├── receiver: User (ManyToOne)
├── amount: BigDecimal
├── description: String
├── transactionDate: LocalDateTime
└── status: TransactionStatus
```

## 🎯 Fonctionnalités Futures (V2)

- **Dépôts et retraits** bancaires
- **Commissions** sur les transactions (0,5%)
- **Notifications email** pour les transactions
- **Historique détaillé** avec filtres
- **API REST** pour intégrations externes
- **Application mobile** (React Native)

## 📞 Support et Contact

Pour toute question concernant ce projet académique :
- **Contexte**: Projet P6 - Formation Développeur d'Application Java
- **Technologies**: Spring Boot, JPA/Hibernate, MySQL, Bootstrap
- **Date**: Septembre 2025

---

## 📄 Licence

Ce projet est développé dans un cadre éducatif - Formation OpenClassrooms.
