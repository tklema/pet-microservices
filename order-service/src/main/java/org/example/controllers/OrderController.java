package org.example.controllers;

import org.example.model.Order;
import org.example.model.OrderDTO;
import org.example.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController extends ExceptionController {

    @Autowired
    private OrderService service;

    @GetMapping("/order/{orderId}")
    public Order getOrderByOrderId(@PathVariable Long orderId) {
        return service.getOrderByOrderId(orderId);
    }

    @GetMapping("/all/{userId}")
    public List<Order> getAllOrdersByUserId(@PathVariable Long userId) {
        return service.getAllOrdersByUserId(userId);
    }

    @DeleteMapping("/{orderId}")
    public void deleteOrderByOrderId(@PathVariable Long orderId) {
        service.deleteOrderByOrderId(orderId);
    }

    @PostMapping("/{userId}")
    public Order createOrder(@PathVariable Long userId,
                             @RequestBody OrderDTO orderDTO) {
        return service.createOrder(userId, orderDTO);
    }

}
