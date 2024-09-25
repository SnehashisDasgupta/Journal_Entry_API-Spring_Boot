package net.journalApp.controller;

import net.journalApp.api.response.WeatherResponse;
import net.journalApp.entity.User;
import net.journalApp.repository.UserRepository;
import net.journalApp.service.UserService;
import net.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User newUser) {
        // fetch the username of the user from 'SecurityContextHolder' which stores the user details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);

        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        userService.saveNewUser(user);

        return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        userRepository.deleteByUsername(username);

        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<String> getWeather() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        WeatherResponse weatherResponse = weatherService.getWeather("Kolkata");
        String greeting = "";
        if (weatherResponse != null) {
            greeting = ", Weather feels like " + weatherResponse.getCurrent().getFeelslike() + " in";
        }
        return new ResponseEntity<>("Welcome " + username + greeting, HttpStatus.OK);
    }
}
