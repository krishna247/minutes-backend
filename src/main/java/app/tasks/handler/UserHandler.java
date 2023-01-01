package app.tasks.handler;

import app.tasks.model.User;
import app.tasks.repository.UserRepository;
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
    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/user")
    public User getUser(@RequestParam String userUuid) {
        System.out.println("Fetching user details: "+userUuid);
        Optional<User> user = userRepository.findById(userUuid);

        if (user.isPresent()) {
            return user.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No such user");
    }

    @PostMapping(value = "/user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> createUser(@RequestBody User userInput) {
        userInput.setId(UUID.randomUUID().toString());
        userRepository.save(userInput);
        return Map.of("id", userInput.getId());
    }

    @DeleteMapping(value = "/user")
    public void deleteUser(@RequestParam String userUuid) {
        userRepository.deleteAllById(List.of(userUuid));
    }

    @PutMapping(value = "/user")
    public void updateUser(@RequestBody User userInput){
        Optional<User> userObj = userRepository.findById(userInput.getId());
        if (userObj.isPresent()) {
            User user = userObj.get();
            user.setName(userInput.getName() == null ? user.getName() : userInput.getName());
            user.setPhotoUrl(userInput.getPhotoUrl() == null ? user.getPhotoUrl() : userInput.getPhotoUrl());
            userRepository.save(user);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No such user");
    }

    @GetMapping(value = "/users")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

}