package app.tasks.handler;

import app.tasks.model.User;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.UserRepository;
import app.tasks.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class UserHandler {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final AuthUtils authUtils;

    public UserHandler(UserRepository userRepository, SessionRepository sessionRepository, AuthUtils authUtils) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.authUtils = authUtils;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/user/{userUuid}")
    public User getUser(@PathVariable String userUuid, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        System.out.println("Fetching user details: " + userUuid);
        Optional<User> user = userRepository.findById(userUuid);

        if (user.isPresent()) {
            return user.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user");
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/user",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, String> createUser(@RequestBody User userInput, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        userInput.setId(UUID.randomUUID().toString());
        userRepository.save(userInput);
        return Map.of("id", userInput.getId());
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/user")
    public void deleteUser(@RequestParam String userUuid, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        userRepository.deleteAllById(List.of(userUuid));
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void updateUser(@RequestBody User userInput, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        Optional<User> userObj = userRepository.findById(userInput.getId());
        if (userObj.isPresent()) {
            User user = userObj.get();
            user.setName(userInput.getName() == null ? user.getName() : userInput.getName());
            user.setPhotoUrl(userInput.getPhotoUrl() == null ? user.getPhotoUrl() : userInput.getPhotoUrl());
            userRepository.save(user);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user");
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/users")
    public List<User> getAllUsers(@RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        return userRepository.findAll();
    }

}
