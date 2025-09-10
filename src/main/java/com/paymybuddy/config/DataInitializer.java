package com.paymybuddy.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;

/**
 * Initialize test data when the application starts
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if test user already exists
        if (!userService.existsByEmail("john.doe@email.com")) {
            // Create test user
            User testUser = new User();
            testUser.setEmail("john.doe@email.com");
            testUser.setPassword("password123"); // Will be encoded by the service
            testUser.setFirstName("John");
            testUser.setLastName("Doe");
            
            userService.createUser(testUser);
            System.out.println("Test user created: john.doe@email.com with password: password123");
        } else {
            System.out.println("Test user already exists: john.doe@email.com");
        }
        
        // Create another test user
        if (!userService.existsByEmail("jane.smith@email.com")) {
            User testUser2 = new User();
            testUser2.setEmail("jane.smith@email.com");
            testUser2.setPassword("password123");
            testUser2.setFirstName("Jane");
            testUser2.setLastName("Smith");
            
            userService.createUser(testUser2);
            System.out.println("Test user created: jane.smith@email.com with password: password123");
        } else {
            System.out.println("Test user already exists: jane.smith@email.com");
        }
    }
}
