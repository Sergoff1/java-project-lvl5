package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.model.User;

public interface UserService {

    User createUser(UserCreateDto registrationData);
}
