package hexlet.code.service;

import hexlet.code.model.User;

import java.util.Optional;

public interface AuthenticationService {

    String login(String username, String password);

    Optional<User> findByToken(String token);
}
