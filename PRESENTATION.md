# Guide de Présentation - PayMyBuddy

## 🎯 Objectif du Projet

**PayMyBuddy** est une application web de transfert d'argent entre amis développée dans le cadre du **Projet P6 - Formation Développeur d'Application Java**. L'objectif est de créer une plateforme sécurisée permettant aux utilisateurs d'effectuer des transactions financières avec leurs amis.

## 📋 Cahier des Charges Réalisé

### Fonctionnalités Principales Implémentées ✅

1. **Gestion des Utilisateurs**
   - Inscription avec email unique
   - Connexion sécurisée
   - Gestion de profil
   - Chiffrement des mots de passe

2. **Système d'Amitié**
   - Ajout d'amis par email
   - Visualisation des connexions
   - Gestion bidirectionnelle des relations

3. **Transferts d'Argent**
   - Transactions entre utilisateurs connectés
   - Vérification des soldes
   - Historique des transactions
   - Validation des montants

4. **Interface Utilisateur**
   - Design responsive avec Bootstrap
   - Interface en français
   - Navigation intuitive

### Technologies Maîtrisées

- **Backend**: Java 21, Spring Boot 3.2.0, Spring Security
- **Persistance**: JPA/Hibernate, MySQL
- **Frontend**: Thymeleaf, Bootstrap 5
- **Architecture**: MVC, Couches séparées (Controller, Service, Repository)

## 🏗️ Architecture Technique

### Modèle de Données
```
USERS (utilisateurs)
├── Identifiants (email unique, mot de passe chiffré)
├── Informations personnelles (nom, prénom)
├── Solde du compte
└── Horodatage (création, modification)

CONNECTIONS (relations d'amitié)
├── Référence utilisateur principal
├── Référence ami
└── Date de création

TRANSACTIONS (transferts)
├── Expéditeur et destinataire
├── Montant et description
├── Date de transaction
└── Statut (COMPLETED, PENDING, FAILED)
```

### Sécurité Implémentée
- **Spring Security** avec authentification personnalisée
- **BCrypt** pour le chiffrement des mots de passe
- **Protection CSRF** sur tous les formulaires
- **Validation des données** côté serveur
- **Contraintes de base de données**

## 🧪 Démonstration des Fonctionnalités

### Comptes de Test Disponibles
- **john.doe@email.com** / `password123`
- **jane.smith@email.com** / `password123`

### Scénarios de Test
1. **Inscription et connexion** d'un nouvel utilisateur
2. **Ajout d'amis** par recherche d'email
3. **Transfert d'argent** entre utilisateurs connectés
4. **Consultation de l'historique** des transactions
5. **Gestion du profil** utilisateur

## 📊 Points Techniques Démontrés

### Architecture MVC Respectée
```
Controllers → Services → Repositories → Database
     ↓
   Thymeleaf Templates (Views)
```

### Gestion des Erreurs et Validations
- Messages d'erreur utilisateur appropriés
- Validation des contraintes métier
- Gestion des cas d'exception

### Bonnes Pratiques Appliquées
- **Séparation des responsabilités** (SoC)
- **Injection de dépendances** avec Spring
- **Gestion des transactions** avec `@Transactional`
- **Code clean** et commenté
- **Nomenclature française** pour l'interface

## 🎯 Défis Techniques Relevés

1. **Gestion des relations bidirectionnelles** entre utilisateurs
2. **Transactions atomiques** pour les transferts d'argent
3. **Sécurisation complète** de l'application
4. **Interface responsive** et accessible
5. **Architecture évolutive** pour futures fonctionnalités

## 🚀 Points de Présentation Clés

### Démarrage de l'Application
```bash
mvn clean package -DskipTests
java -Xmx128m -XX:+UseSerialGC -jar target/paymybuddy-0.0.1-SNAPSHOT.jar
```

### URL d'Accès
- **Application**: http://localhost:8080
- **Page de connexion**: http://localhost:8080/login
- **Inscription**: http://localhost:8080/register

### Démonstration Suggérée
1. **Connexion** avec compte de test
2. **Navigation** dans le tableau de bord
3. **Ajout d'un ami** par email
4. **Effectuer un transfert** d'argent
5. **Vérification** de l'historique des transactions

## 📈 Extensions Possibles (V2)

- Intégration bancaire (dépôts/retraits)
- Système de commissions
- Notifications email
- API REST pour applications mobiles
- Panel d'administration

---

## 🎓 Compétences Démontrées

✅ **Maîtrise de Spring Boot** et de l'écosystème Spring  
✅ **Architecture MVC** bien structurée  
✅ **Sécurité applicative** avec Spring Security  
✅ **Persistance de données** avec JPA/Hibernate  
✅ **Développement full-stack** Java  
✅ **Bonnes pratiques** de développement  
✅ **Interface utilisateur** moderne et responsive

Ce projet démontre une compréhension solide du développement d'applications Java entreprise avec Spring Boot.
