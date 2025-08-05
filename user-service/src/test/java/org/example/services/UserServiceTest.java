package org.example.services;

import org.example.model.InvalidParametersException;
import org.example.model.User;
import org.example.model.UserDTO;
import org.example.model.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserService service;

    private final String name = "Alex";
    private final String email = "user@example.com";

    @Test
    void validateEmail_OK() {
        validateEmailFabric_OK_createUser("user@example.com");
        validateEmailFabric_OK_createUser("first.last@sub.domain.com");
        validateEmailFabric_OK_createUser("user+tag@example.org");
        validateEmailFabric_OK_createUser("1234567890@example.com");
        validateEmailFabric_OK_createUser("user+tag@example.com");
        validateEmailFabric_OK_createUser("user.name@example.com");
        validateEmailFabric_OK_createUser("user_name@example.com");
        validateEmailFabric_OK_createUser("user$name@example.com");
        validateEmailFabric_OK_createUser("user#name@example.com");
    }

    @Test
    void validateEmail_NullOrEmptyEmail() {
        validateEmailFabric_Error_createUser(null);
        validateEmailFabric_Error_createUser("");
        validateEmailFabric_Error_createUser("   ");
    }

    @Test
    void validateEmail_InvalidEmail() {
        validateEmailFabric_Error_createUser("plainstring");
        validateEmailFabric_Error_createUser("user@.com");
        validateEmailFabric_Error_createUser("@example.com");
        validateEmailFabric_Error_createUser("example.com");
        validateEmailFabric_Error_createUser("user@missingtld.");
        validateEmailFabric_Error_createUser("user@example..com");
        validateEmailFabric_Error_createUser("user@");
        validateEmailFabric_Error_createUser("user@domain_without_tld");
        validateEmailFabric_Error_createUser("user@-invalid.com");
        validateEmailFabric_Error_createUser("user@inv@lid.com");
        validateEmailFabric_Error_createUser("user@[invalid].com");
        validateEmailFabric_Error_createUser("missing@domain");
        validateEmailFabric_Error_createUser("invalid@domain-.com");
    }

    @Test
    void validateEmail_LengthConstraints() {
        String tooShortEmail1 = "a@";
        validateEmailFabric_Error_createUser(tooShortEmail1);

        validateEmailFabric_OK_createUser("a@b.c");

        String longValidEmail = "a@" + "b".repeat(250) + ".c";
        validateEmailFabric_OK_createUser(longValidEmail);

        String tooLongEmail = "a@" + "b".repeat(251) + ".c";
        validateEmailFabric_Error_createUser(tooLongEmail);

        String tooLongEmail2 = "a@" + "b".repeat(1000) + ".c";
        validateEmailFabric_Error_createUser(tooLongEmail2);

        String tooLongEmail3 = "a@" + "b" + ".c".repeat(251);
        validateEmailFabric_Error_createUser(tooLongEmail3);

        String tooLongEmail4 = "a".repeat(251) + "@b" + ".c";
        validateEmailFabric_Error_createUser(tooLongEmail4);
    }

    void validateEmailFabric_OK_createUser(String emailTest) {
        assertDoesNotThrow(() -> service.createUser(new UserDTO(name, emailTest)));
        verify(repository).save(argThat(userArg -> userArg.getName().equals(name) &&
                userArg.getEmail().equals(emailTest)));
    }

    void validateEmailFabric_Error_createUser(String emailTest) {
        assertThrows(InvalidParametersException.class, () -> service.createUser(new UserDTO(name, emailTest)));
    }

    @Test
    void validateName_OK() {
        validateNameFabric_OK_createUser("Alex");
        validateNameFabric_OK_createUser("Sabrina");
        validateNameFabric_OK_createUser("Alex Peterson");
    }

    @Test
    void validateName_NullOrEmptyEmail() {
        validateNameFabric_Error_createUser(null);
        validateNameFabric_Error_createUser("");
        validateNameFabric_Error_createUser("   ");
    }

    void validateNameFabric_OK_createUser(String nameTest) {
        assertDoesNotThrow(() -> service.createUser(new UserDTO(nameTest, email)));
        verify(repository).save(argThat(userArg -> userArg.getName().equals(nameTest) &&
                userArg.getEmail().equals(email)));
    }

    void validateNameFabric_Error_createUser(String nameTest) {
        assertThrows(InvalidParametersException.class, () -> service.createUser(new UserDTO(nameTest, email)));
    }

    @Test
    void validateUserId_OK() {
        validateUserIdFabric_OK_getUserByUserId(1L);
        validateUserIdFabric_OK_getUserByUserId(1000L);
        validateUserIdFabric_OK_getUserByUserId(1000000L);
    }

    @Test
    void validateUserId_NotPositiveOrNullId() {
        validateUserIdFabric_Error_getUserByUserId(0L);
        validateUserIdFabric_Error_getUserByUserId(-1L);
        validateUserIdFabric_Error_getUserByUserId(null);
    }

    void validateUserIdFabric_OK_getUserByUserId(Long userId) {
        when(repository.findById(anyLong())).thenReturn(Optional.of(new User(name, email)));
        User userAns = assertDoesNotThrow(() -> service.getUserByUserId(userId));
        verify(repository).findById(userId);
        assertEquals(userAns.getName(), name);
        assertEquals(userAns.getEmail(), email);
    }

    void validateUserIdFabric_Error_getUserByUserId(Long userId) {
        assertThrows(InvalidParametersException.class, () -> service.getUserByUserId(userId));
        verifyNoInteractions(repository);
    }

    @Test
    void deleteUserById_OK() {
        Long userId = 1L;
        doNothing().when(repository).deleteById(userId);
        assertDoesNotThrow(() -> service.deleteUserByUserId(userId));
        verify(repository).deleteById(userId);
    }

    @Test
    void deleteUserById_Error() {
        Long userId = -1L;
        assertThrows(InvalidParametersException.class, () -> service.deleteUserByUserId(userId));
        verifyNoInteractions(repository);
    }

}
