package be.kdg.prog6.ordering.ports.out;

import be.kdg.prog6.ordering.domain.Order;

import java.util.List;

public interface LoadOrderPort {
    List<Order>loadAllOrders();
    Order loadOrder(Order order);
}

