package net.journalApp.controller;

import net.journalApp.entity.User;
import net.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        List<User> allUsers = userService.getAll();
        if (allUsers != null && !allUsers.isEmpty()) {
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }
        return new ResponseEntity<>("No users found.", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-admin-user")
    public ResponseEntity<String> createAdminUser(@RequestBody User user) {
        try {
            userService.saveAdmin(user);
            return new ResponseEntity<>("Admin user created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Admin user already exist", HttpStatus.BAD_REQUEST);
        }
    }
}
