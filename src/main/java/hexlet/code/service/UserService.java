package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

public interface UserService {

    User createUser(UserDto registrationData);

    User updateUser(Long id, UserDto newData);

    String getCurrentUserName();

    User getCurrentUser();
}
