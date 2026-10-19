package be.kdg.prog6.ordering.core;

import be.kdg.prog6.common.events.delivery.OrderAcceptedEvent;
import be.kdg.prog6.ordering.adapters.out.OrderJpaEntity;
import be.kdg.prog6.ordering.adapters.out.OrderJpaRepository;
import be.kdg.prog6.ordering.adapters.out.BasketJpaEntity;
import be.kdg.prog6.ordering.adapters.out.BasketJpaRepository;
import be.kdg.prog6.ordering.domain.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CheckoutOrderImp {
    private final BasketJpaRepository basketJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final Logger logger = LoggerFactory.getLogger(CheckoutOrderImp.class);
    private final ApplicationEventPublisher publisher;

    public CheckoutOrderImp(BasketJpaRepository basketJpaRepository, OrderJpaRepository orderJpaRepository, ApplicationEventPublisher publisher) {
        this.basketJpaRepository = basketJpaRepository;
        this.orderJpaRepository = orderJpaRepository;
        this.publisher = publisher;
    }

    public Order checkout(UUID basketId) {
        BasketJpaEntity basketEntity = basketJpaRepository.findById(basketId)
                .orElseThrow(() -> new IllegalArgumentException("Basket not found: " + basketId));

        // Create Order entity based on basket data
        OrderJpaEntity orderEntity = new OrderJpaEntity();
        orderEntity.setId(UUID.randomUUID());
        orderEntity.setCustomer(basketEntity.getCustomer());
        orderEntity.setDishIds(basketEntity.getDishIds());
        orderEntity.setStatus(OrderStatus.ACCEPTED);
        orderEntity.setRestaurantId(basketEntity.getRestaurantId());
        logger.info("Checking out basket with restaurantId {}", orderEntity.getRestaurantId()   );
        orderEntity = orderJpaRepository.save(orderEntity);

        basketJpaRepository.delete(basketEntity);
        Order order = toDomain(orderEntity);
        UUID restaurantId = order.getRestaurantId();

        publisher.publishEvent(new OrderAcceptedEvent(order.getId(), restaurantId, Instant.now()));


        return order;
    }

    private Order toDomain(OrderJpaEntity entity) {
        Customer customer = mapCustomerEntityToDomain(entity.getCustomer());
        List<DishId> dishIds = mapDishEntitiesToDomain(entity.getDishIds());
        UUID restaurantId = UUID.fromString(entity.getRestaurantId());
        return new Order(entity.getId(), customer, dishIds, restaurantId,entity.getStatus());
    }

    private Customer mapCustomerEntityToDomain(CustomerEmbeddable customerEmbeddable) {
        if (customerEmbeddable == null) return null;
        return new Customer(
                customerEmbeddable.getName(),
                customerEmbeddable.getDeliveryAddress().toDomain(),
                customerEmbeddable.getContactEmail()
        );
    }

    private List<DishId> mapDishEntitiesToDomain(List<UUID> dishIds) {
        if (dishIds == null || dishIds.isEmpty()) {
            return List.of();
        }
        return dishIds.stream().map(DishId::new).toList();
    }
}
