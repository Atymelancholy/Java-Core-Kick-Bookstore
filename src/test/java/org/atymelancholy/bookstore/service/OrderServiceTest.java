package org.atymelancholy.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.dao.OrderDao;
import org.atymelancholy.bookstore.model.OrderLine;
import org.atymelancholy.bookstore.model.OrderSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private DaoFactory daoFactory;
    @Mock
    private OrderDao orderDao;

    @Test
    void listForUserDelegatesToDao() {
        when(daoFactory.orders()).thenReturn(orderDao);
        var svc = new OrderService(daoFactory);
        var sample = List.of(new OrderSummary(1, 9, "PLACED", java.time.Instant.EPOCH, Collections.<OrderSummary.OrderLineView>emptyList()));
        when(orderDao.listByUser(9L, OrderService.PAGE_SIZE, 0)).thenReturn(sample);
        assertEquals(sample, svc.listForUser(9, 0));
    }

    @Test
    void placeOrderDelegates() {
        when(daoFactory.orders()).thenReturn(orderDao);
        when(orderDao.createPlacedOrder(2L, List.of(new OrderLine(3, 1)))).thenReturn(99L);
        var svc = new OrderService(daoFactory);
        assertEquals(99L, svc.placeOrder(2, List.of(new OrderLine(3, 1))));
    }
}
