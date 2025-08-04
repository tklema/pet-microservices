package org.example.services;

import feign.FeignException;
import org.example.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private UserClient userClient;

    public Order getOrderByOrderId(Long orderId) {
        validateOrderId(orderId);
        return repository.findById(orderId).orElseThrow(() -> new NotFoundException("order not found"));
    }

    public List<Order> getAllOrdersByUserId(Long userId) {
        validateUserId(userId);
        return repository.findAllByUserId(userId);
    }

    public void deleteOrderByOrderId(Long orderId) {
        validateOrderId(orderId);
        repository.deleteById(orderId);
    }

    public Order createOrder(Long userId, OrderDTO orderDTO) {
        validateUserId(userId);

        Order order = new Order(orderDTO.getName(), orderDTO.getCount(), userId);
        repository.save(order);
        return order;
    }

    private void validateUserId(Long userId) {
        validateId(userId);
        try {
            userClient.getUserById(userId);
        } catch (FeignException.FeignClientException e) {
            throw new InvalidParametersException("this user doesn't exist");
        }
    }

    private void validateOrderId(Long orderId) {
        validateId(orderId);
    }

    private void validateId(Long id) {
        if (id == null || id < 1) {
            throw new InvalidParametersException("id can't be null or less than 1");
        }
    }
}
