package com.paymybuddy.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.TransactionStatus;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service gérant la logique métier des transactions
 */
@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ConnectionService connectionService;

    public TransactionService(TransactionRepository transactionRepository, 
                            UserRepository userRepository,
                            ConnectionService connectionService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.connectionService = connectionService;
    }

    /**
     * Effectue un transfert d'argent entre deux utilisateurs
     * @param senderEmail email de l'expéditeur
     * @param receiverEmail email du destinataire
     * @param amount montant à transférer
     * @param description description de la transaction
     * @return la transaction créée
     * @throws IllegalArgumentException si les conditions ne sont pas remplies
     */
    public Transaction transferMoney(String senderEmail, String receiverEmail, 
                                   BigDecimal amount, String description) {
        // Validations
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        if (senderEmail.equals(receiverEmail)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous transférer de l'argent à vous-même");
        }

        // Récupération des utilisateurs
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Expéditeur non trouvé"));
        
        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé"));

        // Vérifier que les utilisateurs sont amis
        if (!connectionService.areConnected(senderEmail, receiverEmail)) {
            throw new IllegalArgumentException("Vous ne pouvez transférer de l'argent qu'à vos amis");
        }

        // Vérifier le solde suffisant
        if (!sender.hasSufficientBalance(amount)) {
            throw new IllegalArgumentException("Solde insuffisant");
        }

        try {
            // Effectuer la transaction
            sender.debit(amount);
            receiver.credit(amount);

            // Sauvegarder les utilisateurs
            userRepository.save(sender);
            userRepository.save(receiver);

            // Créer et sauvegarder la transaction
            Transaction transaction = new Transaction(sender, receiver, amount, description);
            transaction.setStatus(TransactionStatus.COMPLETED);
            
            return transactionRepository.save(transaction);

        } catch (Exception e) {
            // En cas d'erreur, créer une transaction échouée
            Transaction failedTransaction = new Transaction(sender, receiver, amount, description);
            failedTransaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(failedTransaction);
            
            throw new RuntimeException("Erreur lors du transfert : " + e.getMessage(), e);
        }
    }

    /**
     * Récupère l'historique des transactions d'un utilisateur
     * @param userEmail email de l'utilisateur
     * @return liste des transactions
     */
    public List<Transaction> getTransactionHistory(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return transactionRepository.findByUser(user);
    }

    /**
     * Récupère les transactions envoyées par un utilisateur
     * @param userEmail email de l'utilisateur
     * @return liste des transactions envoyées
     */
    public List<Transaction> getSentTransactions(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return transactionRepository.findBySenderOrderByTransactionDateDesc(user);
    }

    /**
     * Récupère les transactions reçues par un utilisateur
     * @param userEmail email de l'utilisateur
     * @return liste des transactions reçues
     */
    public List<Transaction> getReceivedTransactions(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return transactionRepository.findByReceiverOrderByTransactionDateDesc(user);
    }

    /**
     * Récupère les transactions en attente d'un utilisateur
     * @param userEmail email de l'utilisateur
     * @return liste des transactions en attente
     */
    public List<Transaction> getPendingTransactions(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return transactionRepository.findPendingTransactionsByUser(user);
    }

    /**
     * Récupère les transactions entre deux utilisateurs
     * @param userEmail1 email du premier utilisateur
     * @param userEmail2 email du second utilisateur
     * @return liste des transactions entre ces utilisateurs
     */
    public List<Transaction> getTransactionsBetweenUsers(String userEmail1, String userEmail2) {
        User user1 = userRepository.findByEmail(userEmail1)
                .orElseThrow(() -> new IllegalArgumentException("Premier utilisateur non trouvé"));
        
        User user2 = userRepository.findByEmail(userEmail2)
                .orElseThrow(() -> new IllegalArgumentException("Second utilisateur non trouvé"));
        
        return transactionRepository.findTransactionsBetweenUsers(user1, user2);
    }

    /**
     * Récupère les transactions d'un utilisateur sur une période
     * @param userEmail email de l'utilisateur
     * @param startDate date de début
     * @param endDate date de fin
     * @return liste des transactions dans cette période
     */
    public List<Transaction> getTransactionsByDateRange(String userEmail, 
                                                       LocalDateTime startDate, 
                                                       LocalDateTime endDate) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return transactionRepository.findByUserAndDateRange(user, startDate, endDate);
    }

    /**
     * Effectue un transfert d'argent entre deux utilisateurs par ID
     * @param senderId ID de l'expéditeur
     * @param receiverId ID du destinataire
     * @param amount montant à transférer
     * @param description description de la transaction
     * @return la transaction créée
     */
    public Transaction transferMoney(Long senderId, Long receiverId, 
                                   BigDecimal amount, String description) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Expéditeur non trouvé"));
        
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé"));

        return transferMoney(sender.getEmail(), receiver.getEmail(), amount, description);
    }

    /**
     * Récupère les transactions récentes d'un utilisateur
     * @param userId ID de l'utilisateur
     * @param limit nombre maximum de transactions à récupérer
     * @return liste des transactions récentes
     */
    public List<Transaction> findRecentTransactions(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        Pageable pageable = PageRequest.of(0, limit);
        return transactionRepository.findRecentTransactionsByUser(user, pageable);
    }

    /**
     * Récupère toutes les transactions d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return liste de toutes les transactions
     */
    public List<Transaction> findUserTransactions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return transactionRepository.findByUser(user);
    }
}
