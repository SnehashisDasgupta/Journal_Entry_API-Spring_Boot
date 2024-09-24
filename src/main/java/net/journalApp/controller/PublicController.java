package net.journalApp.controller;

import net.journalApp.entity.User;
import net.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String healthCheck() { return "OK"; }

    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            if (user.getUsername().isEmpty() || user.getPassword().isEmpty())
                return new ResponseEntity<>("Enter both username and password", HttpStatus.NOT_ACCEPTABLE);

            if (userService.existsByUsername(user.getUsername())) return new ResponseEntity<>("Username already exist", HttpStatus.CONFLICT);

            userService.saveNewUser(user);
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}