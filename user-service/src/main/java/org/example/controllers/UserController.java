package org.example.controllers;

import org.example.model.User;
import org.example.model.UserDTO;
import org.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends ExceptionController {

    @Autowired
    private UserService service;

    @GetMapping("/{userId}")
    public User getUserByUserId(@PathVariable Long userId) {
        return service.getUserByUserId(userId);
    }

    @PostMapping
    public User createUser(@RequestBody UserDTO userDTO) {
        return service.createUser(userDTO);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserByUserId(@PathVariable Long userId) {
        service.deleteUserByUserId(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }
}
