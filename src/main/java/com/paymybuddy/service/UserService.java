package com.paymybuddy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

import jakarta.transaction.Transactional;

/**
 * Service gérant la logique métier des utilisateurs
 */
@Service
@Transactional
public class UserService {

    private static final String USER_NOT_FOUND = "Utilisateur non trouvé";
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crée un nouvel utilisateur
     * @param user l'utilisateur à créer
     * @return l'utilisateur créé
     * @throws IllegalArgumentException si l'email existe déjà
     */
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }
        
        // Chiffrement du mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    /**
     * Trouve un utilisateur par son email
     * @param email l'email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Trouve un utilisateur par son ID
     * @param id l'ID de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Trouve tous les utilisateurs
     * @return liste de tous les utilisateurs
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Met à jour un utilisateur
     * @param user l'utilisateur à mettre à jour
     * @return l'utilisateur mis à jour
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Supprime un utilisateur
     * @param id l'ID de l'utilisateur à supprimer
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Vérifie si un utilisateur existe avec cet email
     * @param email l'email à vérifier
     * @return true si l'utilisateur existe
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Trouve les amis d'un utilisateur
     * @param userId l'ID de l'utilisateur
     * @return liste des amis
     */
    public List<User> findFriends(Long userId) {
        return userRepository.findFriendsByUserId(userId);
    }

    /**
     * Recherche d'utilisateurs par email
     * @param email le texte à rechercher
     * @return liste des utilisateurs correspondants
     */
    public List<User> searchByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email);
    }

    /**
     * Recherche d'utilisateurs par nom
     * @param firstName le prénom
     * @param lastName le nom
     * @return liste des utilisateurs correspondants
     */
    public List<User> searchByName(String firstName, String lastName) {
        return userRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                firstName, lastName);
    }

    /**
     * Crédite le compte d'un utilisateur
     * @param userId l'ID de l'utilisateur
     * @param amount le montant à créditer
     * @return l'utilisateur mis à jour
     * @throws IllegalArgumentException si l'utilisateur n'existe pas ou le montant est invalide
     */
    public User creditAccount(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        user.credit(amount);
        return userRepository.save(user);
    }

    /**
     * Débite le compte d'un utilisateur
     * @param userId l'ID de l'utilisateur
     * @param amount le montant à débiter
     * @return l'utilisateur mis à jour
     * @throws IllegalArgumentException si l'utilisateur n'existe pas, le montant est invalide ou le solde insuffisant
     */
    public User debitAccount(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        user.debit(amount);
        return userRepository.save(user);
    }

    /**
     * Vérifie le mot de passe d'un utilisateur
     * @param user l'utilisateur
     * @param rawPassword le mot de passe en clair
     * @return true si le mot de passe est correct
     */
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    /**
     * Change le mot de passe d'un utilisateur
     * @param userId l'ID de l'utilisateur
     * @param newPassword le nouveau mot de passe
     * @return l'utilisateur mis à jour
     */
    public User changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
