package com.paymybuddy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.paymybuddy.model.User;

/**
 * Repository pour l'entité User
 * Fournit les méthodes d'accès aux données pour les utilisateurs
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son email
     * @param email l'email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un utilisateur existe avec cet email
     * @param email l'email à vérifier
     * @return true si l'utilisateur existe
     */
    boolean existsByEmail(String email);

    /**
     * Trouve tous les amis d'un utilisateur
     * @param userId l'ID de l'utilisateur
     * @return liste des amis
     */
    @Query("SELECT c.friend FROM Connection c WHERE c.user.id = :userId")
    List<User> findFriendsByUserId(@Param("userId") Long userId);

    /**
     * Trouve tous les utilisateurs sauf celui spécifié
     * @param userId l'ID de l'utilisateur à exclure
     * @return liste des autres utilisateurs
     */
    @Query("SELECT u FROM User u WHERE u.id != :userId")
    List<User> findAllExceptUser(@Param("userId") Long userId);

    /**
     * Trouve les utilisateurs par prénom et nom (recherche insensible à la casse)
     * @param firstName le prénom
     * @param lastName le nom
     * @return liste des utilisateurs correspondants
     */
    List<User> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
            String firstName, String lastName);

    /**
     * Trouve les utilisateurs par email contenant le texte spécifié
     * @param email le texte à rechercher dans l'email
     * @return liste des utilisateurs correspondants
     */
    List<User> findByEmailContainingIgnoreCase(String email);
}
