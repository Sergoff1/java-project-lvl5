package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserCreateDto registrationData) {
        final User user = new User();
        user.setEmail(registrationData.getEmail());
        user.setFirstName(registrationData.getFirstName());
        user.setLastName(registrationData.getLastName());
        user.setPassword(passwordEncoder.encode(registrationData.getPassword()));
        return userRepository.save(user);
    }
}
