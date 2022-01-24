package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserDto registrationData) {
        final User user = new User();
        user.setEmail(registrationData.getEmail());
        user.setFirstName(registrationData.getFirstName());
        user.setLastName(registrationData.getLastName());
        user.setPassword(passwordEncoder.encode(registrationData.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserDto newData) {
        final User user = userRepository.getById(id);
        user.setEmail(newData.getEmail());
        user.setFirstName(newData.getFirstName());
        user.setLastName(newData.getLastName());
        user.setPassword(passwordEncoder.encode(newData.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUserName()).get();
    }
}
