package org.example.services;

import lombok.AllArgsConstructor;
import org.example.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public User getUserByUserId(Long userId) {
        validateUserId(userId);
        return repository.findById(userId).orElseThrow(() -> new NotFoundException(("user not found")));
    }

    public void deleteUserByUserId(Long userId) {
        validateUserId(userId);
        repository.deleteById(userId);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User createUser(UserDTO userDTO) {
        validateName(userDTO.getName());
        validateEmail(userDTO.getEmail());

        User user = new User(userDTO.getName(), userDTO.getEmail());
        repository.save(user);
        return user;
    }

    private void validateUserId(Long userId) {
        validateId(userId);
    }

    private void validateId(Long id) {
        if (id == null || id < 1) {
            throw new InvalidParametersException("id can't be null or less than 1");
        }
    }

    private void validateEmail(String email) {
        final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.]+$";
        final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        final int MAX_EMAIL_LENGTH = 254;
        final int MIN_EMAIL_LENGTH = 5;

        if (email == null || email.isBlank()) {
            throw new InvalidParametersException("Email cannot be null or empty");
        }

        email = email.trim();

        if (email.length() < MIN_EMAIL_LENGTH || email.length() > MAX_EMAIL_LENGTH) {
            throw new InvalidParametersException("Email length must be between " + MIN_EMAIL_LENGTH +
                    " and " + MAX_EMAIL_LENGTH + " characters");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidParametersException("Invalid email format");
        }

        String[] parts = email.split("@", -1);
        String domain = parts[1];

        if (!domain.contains(".")) {
            throw new InvalidParametersException("Domain should contain a dot");
        }

        if (domain.startsWith(".") || domain.endsWith(".")) {
            throw new InvalidParametersException("Domain cannot start or end with a dot");
        }

        if (domain.contains("..")) {
            throw new InvalidParametersException("Domain cannot contain consecutive dots");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidParametersException("Email cannot be null or empty");
        }
    }
}
