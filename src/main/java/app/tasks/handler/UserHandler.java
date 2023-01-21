package app.tasks.handler;

import app.tasks.model.User;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.UserRepository;
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

import static app.tasks.utils.AuthUtils.isAuthenticated;

@RestController
public class UserHandler {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    public UserHandler(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping(value = "/user")
    public User getUser(@RequestParam String userUuid, @RequestHeader("Authorization") String sessionToken) {
        isAuthenticated(sessionToken,sessionRepository);
        System.out.println("Fetching user details: "+userUuid);
        Optional<User> user = userRepository.findById(userUuid);

        if (user.isPresent()) {
            return user.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No such user");
    }

    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    @PostMapping(value = "/user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> createUser(@RequestBody User userInput, @RequestHeader("Authorization") String sessionToken) {
        isAuthenticated(sessionToken,sessionRepository);
        userInput.setId(UUID.randomUUID().toString());
        userRepository.save(userInput);
        return Map.of("id", userInput.getId());
    }

    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    @DeleteMapping(value = "/user")
    public void deleteUser(@RequestParam String userUuid, @RequestHeader("Authorization") String sessionToken) {
        isAuthenticated(sessionToken,sessionRepository);
        userRepository.deleteAllById(List.of(userUuid));
    }

    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    @PutMapping(value = "/user")
    public void updateUser(@RequestBody User userInput, @RequestHeader("Authorization") String sessionToken){
        isAuthenticated(sessionToken,sessionRepository);
        Optional<User> userObj = userRepository.findById(userInput.getId());
        if (userObj.isPresent()) {
            User user = userObj.get();
            user.setName(userInput.getName() == null ? user.getName() : userInput.getName());
            user.setPhotoUrl(userInput.getPhotoUrl() == null ? user.getPhotoUrl() : userInput.getPhotoUrl());
            userRepository.save(user);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No such user");
    }

    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping(value = "/users")
    public List<User> getAllUsers(@RequestHeader("Authorization") String sessionToken){
        isAuthenticated(sessionToken,sessionRepository);
        return userRepository.findAll();
    }

}
