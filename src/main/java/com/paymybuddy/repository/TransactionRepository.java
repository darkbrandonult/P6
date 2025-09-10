package com.paymybuddy.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.TransactionStatus;
import com.paymybuddy.model.User;

/**
 * Repository pour l'entité Transaction
 * Fournit les méthodes d'accès aux données pour les transactions
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Trouve toutes les transactions d'un utilisateur (envoyées et reçues)
     * @param user l'utilisateur
     * @return liste des transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.sender = :user OR t.receiver = :user ORDER BY t.transactionDate DESC")
    List<Transaction> findByUser(@Param("user") User user);

    /**
     * Trouve toutes les transactions envoyées par un utilisateur
     * @param sender l'expéditeur
     * @return liste des transactions envoyées
     */
    List<Transaction> findBySenderOrderByTransactionDateDesc(User sender);

    /**
     * Trouve toutes les transactions reçues par un utilisateur
     * @param receiver le destinataire
     * @return liste des transactions reçues
     */
    List<Transaction> findByReceiverOrderByTransactionDateDesc(User receiver);

    /**
     * Trouve les transactions par statut
     * @param status le statut des transactions
     * @return liste des transactions avec ce statut
     */
    List<Transaction> findByStatusOrderByTransactionDateDesc(TransactionStatus status);

    /**
     * Trouve les transactions entre deux utilisateurs
     * @param user1 premier utilisateur
     * @param user2 deuxième utilisateur
     * @return liste des transactions entre ces utilisateurs
     */
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.sender = :user1 AND t.receiver = :user2) OR " +
           "(t.sender = :user2 AND t.receiver = :user1) " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findTransactionsBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    /**
     * Trouve les transactions d'un utilisateur sur une période
     * @param user l'utilisateur
     * @param startDate date de début
     * @param endDate date de fin
     * @return liste des transactions dans cette période
     */
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.sender = :user OR t.receiver = :user) " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserAndDateRange(@Param("user") User user,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Trouve les transactions en attente d'un utilisateur
     * @param user l'utilisateur
     * @return liste des transactions en attente
     */
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.sender = :user OR t.receiver = :user) " +
           "AND t.status = 'PENDING' " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findPendingTransactionsByUser(@Param("user") User user);

    /**
     * Trouve les transactions récentes d'un utilisateur
     * @param user l'utilisateur
     * @param pageable pour limiter les résultats
     * @return liste des transactions récentes
     */
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.sender = :user OR t.receiver = :user) " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactionsByUser(@Param("user") User user, Pageable pageable);
}
