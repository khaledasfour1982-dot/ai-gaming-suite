package be.kdg.prog6.ordering.adapters.out;

import be.kdg.prog6.ordering.domain.Order;
import be.kdg.prog6.ordering.ports.out.LoadOrderPort;
import be.kdg.prog6.ordering.ports.out.SaveOrderPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class OrderJpaAdapter implements SaveOrderPort, LoadOrderPort {
    private final OrderJpaRepository orderJpaRepository;
    public OrderJpaAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public void save(Order order) {
        OrderJpaEntity entity = OrderJpaEntity.fromDomain(order);
        orderJpaRepository.save(entity);
    }

    @Override
    public Order loadById(UUID orderId) {
        return orderJpaRepository.findById(orderId)
                .map(OrderJpaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public List<Order> loadAllOrders() {
        return orderJpaRepository.findAll()
                .stream().map(OrderJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Order loadOrder(Order order) {
        return orderJpaRepository.findById(order.getId())
                .map(OrderJpaEntity::toDomain)
                .orElse(null);
    }
}
