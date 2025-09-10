package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Contrôleur pour la gestion de l'authentification
 */
@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Affiche la page d'accueil
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Affiche la page de connexion
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    /**
     * Affiche la page d'inscription
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    /**
     * Traite l'inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        
        // Vérification des erreurs de validation
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            // Vérifier si l'email existe déjà
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Un compte existe déjà avec cet email");
                return "auth/register";
            }

            // Créer l'utilisateur
            userService.createUser(user);
            
            redirectAttributes.addFlashAttribute("success", 
                "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            return "auth/register";
        }
    }


}
