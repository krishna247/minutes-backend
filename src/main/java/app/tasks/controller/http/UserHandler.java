package app.tasks.controller.http;

import app.tasks.model.User;
import app.tasks.repository.UserRepository;
import app.tasks.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserHandler {
    private final UserRepository userRepository;
    private final AuthService authService;

    public UserHandler(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/user/{userUuid}")
    public User getUser(@PathVariable String userUuid, @RequestHeader("Authorization") String sessionToken) {
        authService.isAuthenticated(sessionToken);
        System.out.println("Fetching user details: " + userUuid);
        Optional<User> user = userRepository.findById(userUuid);

        if (user.isPresent()) {
            return user.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user");
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/user")
    public void deleteUser(@RequestBody String userUuid, @RequestHeader("Authorization") String sessionToken) {
        authService.isAuthenticated(sessionToken);
        userRepository.deleteAllById(List.of(userUuid));
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@RequestBody User userInput, @RequestHeader("Authorization") String sessionToken) {
        authService.isAuthenticated(sessionToken);
        Optional<User> userObj = userRepository.findById(userInput.getId());
        if (userObj.isPresent()) {
            User user = userObj.get();
            user.setName(userInput.getName() == null ? user.getName() : userInput.getName());
            user.setPhotoUrl(userInput.getPhotoUrl() == null ? user.getPhotoUrl() : userInput.getPhotoUrl());
            user.setUsername(userInput.getUsername() == null ? user.getUsername() : userInput.getUsername());
            userRepository.save(user);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user");
        }
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/users")
    public List<User> getAllUsers(@RequestHeader("Authorization") String sessionToken) {
        authService.isAuthenticated(sessionToken);
        return userRepository.findAll();
    }

    @GetMapping(value = "/username-check")
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userRepository.existsByUsername(username);
        return Map.of("usernameExists", exists);
    }

}
