package com.paymybuddy.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.ConnectionService;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;

/**
 * Contrôleur pour le tableau de bord et les fonctionnalités principales
 */
@Controller
public class DashboardController {

    private static final String USER_NOT_FOUND = "Utilisateur non trouvé";
    private static final String FRIENDS_ATTR = "friends";
    private static final String SUCCESS_ATTR = "success";
    private static final String ERROR_ATTR = "error";
    private static final String ERROR_PREFIX = "Erreur: ";
    private static final String USER_ATTR = "user";

    private final UserService userService;
    private final TransactionService transactionService;
    private final ConnectionService connectionService;

    public DashboardController(UserService userService, TransactionService transactionService, 
                             ConnectionService connectionService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.connectionService = connectionService;
    }

    /**
     * Affiche le tableau de bord principal
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        
        // Récupérer les amis de l'utilisateur
        List<User> friends = userService.findFriends(user.getId());
        
        // Récupérer les transactions récentes
        List<Transaction> recentTransactions = transactionService.findRecentTransactions(user.getId(), 10);
        
        model.addAttribute(USER_ATTR, user);
        model.addAttribute(FRIENDS_ATTR, friends);
        model.addAttribute("recentTransactions", recentTransactions);
        
        return "dashboard/index";
    }

    /**
     * Affiche la page de transfert d'argent
     */
    @GetMapping("/transfer")
    public String transfer(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        
        List<User> friends = userService.findFriends(user.getId());
        
        model.addAttribute(USER_ATTR, user);
        model.addAttribute(FRIENDS_ATTR, friends);
        
        return "dashboard/transfer";
    }

    /**
     * Affiche la page des transactions
     */
    @GetMapping("/transactions")
    public String transactions(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        
        List<Transaction> transactions = transactionService.findUserTransactions(user.getId());
        List<User> friends = userService.findFriends(user.getId());
        
        model.addAttribute(USER_ATTR, user);
        model.addAttribute("transactions", transactions);
        model.addAttribute(FRIENDS_ATTR, friends);
        
        return "dashboard/transactions";
    }

    /**
     * Traite l'envoi d'argent
     */
    @PostMapping("/send-money")
    public String sendMoney(@RequestParam Long receiverId, 
                           @RequestParam BigDecimal amount,
                           @RequestParam String description,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            User sender = userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
            
            transactionService.transferMoney(sender.getId(), receiverId, amount, description);
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, "Transfert réussi!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Erreur lors du transfert: " + e.getMessage());
        }
        
        return "redirect:/transactions";
    }

    /**
     * Affiche la page des amis
     */
    @GetMapping("/friends")
    public String friends(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        
        List<User> friends = userService.findFriends(user.getId());
        
        model.addAttribute(USER_ATTR, user);
        model.addAttribute(FRIENDS_ATTR, friends);
        
        return "dashboard/friends";
    }

    /**
     * Recherche d'utilisateurs pour ajouter des amis
     */
    @PostMapping("/add-friend")
    public String addFriend(@RequestParam String email,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
            
            User friend = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND + " avec cet email"));
            
            connectionService.addConnection(user.getId(), friend.getId());
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, "Ami ajouté avec succès!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, ERROR_PREFIX + e.getMessage());
        }
        
        return "redirect:/friends";
    }

    /**
     * Affiche le profil utilisateur
     */
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        
        model.addAttribute(USER_ATTR, user);
        
        return "dashboard/profile";
    }

    /**
     * Met à jour le profil utilisateur
     */
    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam String firstName,
                               @RequestParam String lastName,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
            
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, "Profil mis à jour avec succès!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, ERROR_PREFIX + e.getMessage());
        }
        
        return "redirect:/profile";
    }

    /**
     * Crédite le compte de l'utilisateur
     */
    @PostMapping("/add-money")
    public String addMoney(@RequestParam BigDecimal amount,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
            
            userService.creditAccount(user.getId(), amount);
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, "Compte crédité de " + amount + "€");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, ERROR_PREFIX + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
}
