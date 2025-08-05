package org.example.services;

import org.example.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    OrderRepository repository;

    @Mock
    UserClient userClient;

    @InjectMocks
    OrderService service;

    private final String name = "Name";
    private final Long count = 5L;
    private final Long userId = 1L;

    @Test
    void validateOrderId_OK() {
        validateOrderId_OK_getOrderByOrderId(1L);
        validateOrderId_OK_getOrderByOrderId(1000L);
        validateOrderId_OK_getOrderByOrderId(1000000L);
    }

    @Test
    void validateOrderId_NotPositiveOrNullId() {
        validateOrderIdFabric_Error_getOrderByOrderId(0L);
        validateOrderIdFabric_Error_getOrderByOrderId(-1L);
        validateOrderIdFabric_Error_getOrderByOrderId(null);
    }

    void validateOrderId_OK_getOrderByOrderId(Long orderId) {
        when(repository.findById(anyLong())).thenReturn(Optional.of(new Order(name, count, userId)));
        Order orderAns = assertDoesNotThrow(() -> service.getOrderByOrderId(orderId));
        verify(repository).findById(orderId);
        assertEquals(orderAns.getName(), name);
        assertEquals(orderAns.getCount(), count);
        assertEquals(orderAns.getUserId(), userId);
    }

    void validateOrderIdFabric_Error_getOrderByOrderId(Long orderId) {
        assertThrows(InvalidParametersException.class, () -> service.getOrderByOrderId(orderId));
        verifyNoInteractions(repository);
    }

    @Test
    void validateUserId_OK() {
        validateUserId_OK_createOrder(1L);
        validateUserId_OK_createOrder(1000L);
        validateUserId_OK_createOrder(1000000L);
    }

    @Test
    void validateUserId_NotPositiveOrNullId() {
        validateUserIdFabric_Error_createOrder(0L);
        validateUserIdFabric_Error_createOrder(-1L);
        validateUserIdFabric_Error_createOrder(null);
    }

    void validateUserId_OK_createOrder(Long userIdTest) {
        doNothing().when(userClient).getUserById(anyLong());
        assertDoesNotThrow(() -> service.createOrder(userIdTest, new OrderDTO(name, count)));
        verify(repository).save(argThat(orderArg -> orderArg.getName().equals(name) &&
                orderArg.getCount().equals(count) && orderArg.getUserId().equals(userIdTest)));
    }

    void validateUserIdFabric_Error_createOrder(Long userIdTest) {
        assertThrows(InvalidParametersException.class, () -> service.createOrder(userIdTest, new OrderDTO(name, count)));
        verifyNoInteractions(repository);
    }

    @Test
    void deleteOrderById_OK() {
        Long orderId = 1L;
        doNothing().when(repository).deleteById(orderId);
        assertDoesNotThrow(() -> service.deleteOrderByOrderId(orderId));
        verify(repository).deleteById(orderId);
    }

    @Test
    void deleteOrderById_Error() {
        Long orderId = -1L;
        assertThrows(InvalidParametersException.class, () -> service.deleteOrderByOrderId(orderId));
        verifyNoInteractions(repository);
    }

    @Test
    void getAllOrdersByUserId_OK() {
        Long userIdTest = 2L;
        String name1 = name + "1";
        String name2 = name + "2";
        Order order1 = new Order(name1, count, userIdTest);
        Order order2 = new Order(name2, count, userIdTest);

        doNothing().when(userClient).getUserById(anyLong());
        when(repository.findAllByUserId(userIdTest)).thenReturn(List.of(order1, order2));
        List<Order> orderList = assertDoesNotThrow(() -> service.getAllOrdersByUserId(userIdTest));
        verify(repository).findAllByUserId(userIdTest);
        assertEquals(orderList.get(0), order1);
        assertEquals(orderList.get(1), order2);
    }

    @Test
    void getAllOrdersByUserId_Error() {
        Long userIdTest = -1L;
        assertThrows(InvalidParametersException.class, () -> service.getAllOrdersByUserId(userIdTest));
        verifyNoInteractions(repository);
    }
}
