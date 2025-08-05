package org.example.controllers;

import com.jayway.jsonpath.JsonPath;
import org.example.model.User;
import org.example.model.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository repository;

    private User user;
    private final String name = "User";
    private final String email = "user@example.com";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        user = repository.save(new User(name, email));
    }

    @Test
    void getUserByUserId_OK() throws Exception {
        assertTrue(repository.findById(user.getId()).isPresent());
        mockMvc.perform(get("/users/{userId}", user.getId()))
                .andExpect(status().isOk());
        assertTrue(repository.findById(user.getId()).isPresent());
    }

    @Test
    void getUserByUserId_NotFound() throws Exception {
        mockMvc.perform(get("/users/{userId}", 5L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByUserId_InvalidId() throws Exception {
        mockMvc.perform(get("/users/{userId}", 0L))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/users/{userId}", -1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_OK() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\":\"%s\",\"email\":\"%s\"}", name, email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andReturn();

        int userIdTest = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");

        assertTrue(repository.findById((long) userIdTest).isPresent());
    }

    @Test
    void createUser_InvalidName() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"name\":\"%s\",\"email\":\"%s\"}", "", email)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\":\"%s\",\"email\":\"%s\"}", "   ", email)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_InvalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\":\"%s\",\"email\":\"%s\"}", name, "")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\":\"%s\",\"email\":\"%s\"}", name, "  ")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\":\"%s\",\"email\":\"%s\"}", name, "plainstring")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\":\"%s\",\"email\":\"%s\"}", name, "user@.com")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsers_OK() throws Exception {
        String name1 = "name1";
        String name2 = "name2";
        User user1 = repository.save(new User(name1, email));
        User user2 = repository.save(new User(name2, email));

        assertTrue(repository.findById(user.getId()).isPresent());
        assertTrue(repository.findById(user1.getId()).isPresent());
        assertTrue(repository.findById(user2.getId()).isPresent());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].name").value(user.getName()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$[1].id").value(user1.getId()))
                .andExpect(jsonPath("$[1].name").value(user1.getName()))
                .andExpect(jsonPath("$[1].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[2].id").value(user2.getId()))
                .andExpect(jsonPath("$[2].name").value(user2.getName()))
                .andExpect(jsonPath("$[2].email").value(user2.getEmail()));

        assertTrue(repository.findById(user.getId()).isPresent());
        assertTrue(repository.findById(user1.getId()).isPresent());
        assertTrue(repository.findById(user2.getId()).isPresent());
    }

    @Test
    void getAllUsers_OK_EmptyList() throws Exception {
        repository.deleteById(user.getId());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deleteUserByUserId_OK() throws Exception {
        assertTrue(repository.findById(user.getId()).isPresent());
        mockMvc.perform(delete("/users/{userId}", user.getId()))
                .andExpect(status().isOk());
        assertFalse(repository.findById(user.getId()).isPresent());

        assertFalse(repository.findById(5L).isPresent());
        mockMvc.perform(delete("/users/{userId}", 5L))
                .andExpect(status().isOk());
        assertFalse(repository.findById(5L).isPresent());
    }

    @Test
    void deleteUserByUserId_InvalidId() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 0L))
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/users/{userId}", -1L))
                .andExpect(status().isBadRequest());
    }
}
