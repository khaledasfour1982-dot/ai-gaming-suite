package be.kdg.prog6.ordering.ports.out;

import be.kdg.prog6.ordering.domain.Basket;
import be.kdg.prog6.ordering.domain.Order;

import java.util.UUID;

public interface SaveOrderPort {
    void save(Order order);

    Order loadById(UUID orderId);
}
