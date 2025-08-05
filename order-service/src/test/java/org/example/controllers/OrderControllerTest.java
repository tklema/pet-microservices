package org.example.controllers;

import com.jayway.jsonpath.JsonPath;
import feign.FeignException;
import org.example.model.Order;
import org.example.model.OrderRepository;
import org.example.model.UserClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    OrderRepository repository;

    @MockitoBean
    UserClient userClient;

    private Order order;
    private final String name = "Order";
    private final Long count = 5L;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        order = repository.save(new Order(name, count, userId));
    }

    @Test
    void getOrderByOrderId_OK() throws Exception {
        assertTrue(repository.findById(order.getId()).isPresent());
        mockMvc.perform(get("/orders/order/{orderId}", order.getId()))
                .andExpect(status().isOk());
        assertTrue(repository.findById(order.getId()).isPresent());
    }

    @Test
    void getOrderByOrderId_NotFound() throws Exception {
        mockMvc.perform(get("/orders/order/{orderId}", 5L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderByOrderId_InvalidId() throws Exception {
        mockMvc.perform(get("/orders/order/{orderId}", 0L))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/orders/order/{orderId}", -1L))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getAllOrdersByUserId_OK() throws Exception {
        String name1 = "name1";
        String name2 = "name2";
        Order order1 = repository.save(new Order(name1, count, userId));
        Order order2 = repository.save(new Order(name2, count, userId));

        doNothing().when(userClient).getUserById(any(Long.class));

        assertTrue(repository.findById(order.getId()).isPresent());
        assertTrue(repository.findById(order1.getId()).isPresent());
        assertTrue(repository.findById(order2.getId()).isPresent());

        mockMvc.perform(get("/orders/all/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(order.getId()))
                .andExpect(jsonPath("$[0].name").value(order.getName()))
                .andExpect(jsonPath("$[0].count").value(order.getCount()))
                .andExpect(jsonPath("$[0].userId").value(order.getUserId()))
                .andExpect(jsonPath("$[1].id").value(order1.getId()))
                .andExpect(jsonPath("$[1].name").value(order1.getName()))
                .andExpect(jsonPath("$[1].count").value(order1.getCount()))
                .andExpect(jsonPath("$[1].userId").value(order1.getUserId()))
                .andExpect(jsonPath("$[2].id").value(order2.getId()))
                .andExpect(jsonPath("$[2].name").value(order2.getName()))
                .andExpect(jsonPath("$[2].count").value(order2.getCount()))
                .andExpect(jsonPath("$[2].userId").value(order2.getUserId()));

        assertTrue(repository.findById(order.getId()).isPresent());
        assertTrue(repository.findById(order1.getId()).isPresent());
        assertTrue(repository.findById(order2.getId()).isPresent());
    }

    @Test
    void getAllOrdersByUserId_OK_EmptyList() throws Exception {
        repository.deleteById(order.getId());

        mockMvc.perform(get("/orders/all/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllOrdersByUserId_UserNotFound() throws Exception {
        doThrow(FeignException.FeignClientException.class).when(userClient).getUserById(any(Long.class));

        mockMvc.perform(get("/orders/all/{userId}", 10L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOrdersByUserId_InvalidId() throws Exception {
        mockMvc.perform(get("/orders/all/{userId}", 0L))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/orders/all/{userId}", -1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteOrderByOrderId_OK() throws Exception {
        assertTrue(repository.findById(order.getId()).isPresent());
        mockMvc.perform(delete("/orders/{orderId}", order.getId()))
                .andExpect(status().isOk());
        assertFalse(repository.findById(order.getId()).isPresent());

        assertFalse(repository.findById(5L).isPresent());
        mockMvc.perform(delete("/orders/{orderId}", 5L))
                .andExpect(status().isOk());
        assertFalse(repository.findById(5L).isPresent());
    }

    @Test
    void deleteOrderByOrderId_InvalidId() throws Exception {
        mockMvc.perform(delete("/orders/{orderId}", 0L))
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/orders/{orderId}", -1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_OK() throws Exception {
        doNothing().when(userClient).getUserById(any(Long.class));

        MvcResult mvcResult = mockMvc.perform(post("/orders/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\":\"%s\",\"count\":\"%d\"}", name, count)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.count").value(count))
                .andReturn();

        int orderIdTest = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");

        assertTrue(repository.findById((long) orderIdTest).isPresent());
    }

    @Test
    void createOrder_InvalidUserId() throws Exception {
        mockMvc.perform(post("/orders/{userId}", 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\":\"%s\",\"count\":\"%d\"}", name, count)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/orders/{userId}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\":\"%s\",\"count\":\"%d\"}", name, count)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_UserNotFound() throws Exception {
        doThrow(FeignException.FeignClientException.class).when(userClient).getUserById(any(Long.class));

        mockMvc.perform(post("/orders/{userId}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\":\"%s\",\"count\":\"%d\"}", name, count)))
                .andExpect(status().isBadRequest());
    }
}
