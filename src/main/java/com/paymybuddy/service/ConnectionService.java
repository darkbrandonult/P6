package com.paymybuddy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.paymybuddy.model.Connection;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ConnectionRepository;
import com.paymybuddy.repository.UserRepository;

import jakarta.transaction.Transactional;

/**
 * Service gérant la logique métier des connexions/amitiés
 */
@Service
@Transactional
public class ConnectionService {

    private static final String USER_NOT_FOUND = "Utilisateur non trouvé";

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Ajoute une connexion bidirectionnelle entre deux utilisateurs
     * @param userEmail email de l'utilisateur
     * @param friendEmail email de l'ami à ajouter
     * @throws IllegalArgumentException si l'utilisateur ou l'ami n'existe pas, ou si la connexion existe déjà
     */
    public void addConnection(String userEmail, String friendEmail) {
        if (userEmail.equals(friendEmail)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même comme ami");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        User friend = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new IllegalArgumentException("L'utilisateur à ajouter n'existe pas"));

        // Vérifier si la connexion existe déjà
        if (connectionRepository.existsByUserAndFriend(user, friend)) {
            throw new IllegalArgumentException("Cette personne est déjà dans votre liste d'amis");
        }

        // Créer la connexion bidirectionnelle
        connectionRepository.save(new Connection(user, friend));
        connectionRepository.save(new Connection(friend, user));
    }

    /**
     * Supprime une connexion bidirectionnelle entre deux utilisateurs
     * @param userEmail email de l'utilisateur
     * @param friendEmail email de l'ami à supprimer
     */
    public void removeConnection(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        User friend = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new IllegalArgumentException("Ami non trouvé"));

        connectionRepository.deleteByUserAndFriend(user, friend);
        connectionRepository.deleteByUserAndFriend(friend, user);
    }

    /**
     * Récupère tous les amis d'un utilisateur
     * @param userEmail email de l'utilisateur
     * @return liste des amis
     */
    public List<User> getFriends(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return connectionRepository.findMutualFriends(user.getId());
    }

    /**
     * Récupère les utilisateurs qui ne sont pas encore amis
     * @param userEmail email de l'utilisateur
     * @return liste des non-amis
     */
    public List<User> getNonFriends(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return connectionRepository.findNonFriends(user.getId());
    }

    /**
     * Vérifie si deux utilisateurs sont amis
     * @param userEmail email du premier utilisateur
     * @param friendEmail email du second utilisateur
     * @return true s'ils sont amis
     */
    public boolean areConnected(String userEmail, String friendEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        Optional<User> friend = userRepository.findByEmail(friendEmail);
        
        if (user.isEmpty() || friend.isEmpty()) {
            return false;
        }
        
        return connectionRepository.existsByUserAndFriend(user.get(), friend.get());
    }

    /**
     * Compte le nombre d'amis d'un utilisateur
     * @param userEmail email de l'utilisateur
     * @return nombre d'amis
     */
    public long countFriends(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return connectionRepository.countByUser(user);
    }

    /**
     * Ajoute une connexion bidirectionnelle entre deux utilisateurs par ID
     * @param userId ID de l'utilisateur
     * @param friendId ID de l'ami à ajouter
     */
    public void addConnection(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
        
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Ami non trouvé"));

        addConnection(user.getEmail(), friend.getEmail());
    }
}
