package com.paymybuddy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.paymybuddy.model.Connection;
import com.paymybuddy.model.User;

/**
 * Repository pour l'entité Connection
 * Fournit les méthodes d'accès aux données pour les connexions/amitiés
 */
@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    /**
     * Trouve toutes les connexions d'un utilisateur
     * @param user l'utilisateur
     * @return liste des connexions
     */
    List<Connection> findByUser(User user);

    /**
     * Trouve une connexion spécifique entre deux utilisateurs
     * @param user l'utilisateur
     * @param friend l'ami
     * @return Optional contenant la connexion si elle existe
     */
    Optional<Connection> findByUserAndFriend(User user, User friend);

    /**
     * Vérifie si une connexion existe entre deux utilisateurs
     * @param user l'utilisateur
     * @param friend l'ami
     * @return true si la connexion existe
     */
    boolean existsByUserAndFriend(User user, User friend);

    /**
     * Trouve toutes les connexions bidirectionnelles d'un utilisateur
     * @param userId l'ID de l'utilisateur
     * @return liste des amis
     */
    @Query("SELECT c.friend FROM Connection c WHERE c.user.id = :userId " +
           "AND EXISTS (SELECT c2 FROM Connection c2 WHERE c2.user = c.friend AND c2.friend = c.user)")
    List<User> findMutualFriends(@Param("userId") Long userId);

    /**
     * Supprime une connexion entre deux utilisateurs
     * @param user l'utilisateur
     * @param friend l'ami
     */
    void deleteByUserAndFriend(User user, User friend);

    /**
     * Compte le nombre d'amis d'un utilisateur
     * @param user l'utilisateur
     * @return nombre d'amis
     */
    @Query("SELECT COUNT(c) FROM Connection c WHERE c.user = :user")
    long countByUser(@Param("user") User user);

    /**
     * Trouve les utilisateurs qui ne sont pas encore amis avec l'utilisateur donné
     * @param userId l'ID de l'utilisateur
     * @return liste des utilisateurs non-amis
     */
    @Query("SELECT u FROM User u WHERE u.id != :userId " +
           "AND u.id NOT IN (SELECT c.friend.id FROM Connection c WHERE c.user.id = :userId)")
    List<User> findNonFriends(@Param("userId") Long userId);
}
