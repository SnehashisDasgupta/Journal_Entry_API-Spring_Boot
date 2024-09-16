package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> allUsers = userService.getAll();
        if (allUsers!=null) return new ResponseEntity<>(allUsers, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) return new ResponseEntity<>("Enter both username and password", HttpStatus.NOT_ACCEPTABLE);

            userService.saveEntry(user);
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Username already exist ", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@RequestBody User newUser, @PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            user.setUsername(newUser.getUsername());
            user.setPassword(newUser.getPassword());
            userService.saveEntry(user);
        }
        return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    }
}
