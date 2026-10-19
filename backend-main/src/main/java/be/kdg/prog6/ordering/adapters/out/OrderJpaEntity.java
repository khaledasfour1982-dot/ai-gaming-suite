package be.kdg.prog6.ordering.adapters.out;

import be.kdg.prog6.ordering.domain.CustomerEmbeddable;
import be.kdg.prog6.ordering.domain.DishId;
import be.kdg.prog6.ordering.domain.Order;
import be.kdg.prog6.ordering.domain.OrderStatus;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "order",schema = "kdg")
public class OrderJpaEntity {

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID id;

    @Embedded
    private CustomerEmbeddable customer;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_dishes", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "dish_id")
    private List<UUID> dishIds;
    @Column()
    private String restaurantId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // Constructors
    public OrderJpaEntity() {
    }

    public static OrderJpaEntity fromDomain(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(order.getId());
        entity.setCustomer(CustomerEmbeddable.fromDomain(order.getCustomer()));
        entity.setDishIds(
                order.getDishIds().stream()
                        .map(DishId::id)
                        .collect(Collectors.toList())
        );
        entity.setRestaurantId(order.getRestaurantId().toString());
        entity.setStatus(order.getStatus());
        return entity;
    }

    public Order toDomain() {
        List<DishId> domainDishIds = this.getDishIds() == null ? List.of() :
                this.getDishIds().stream()
                        .map(DishId::of)
                        .collect(Collectors.toList());
        UUID restaurantId = UUID.fromString(this.getRestaurantId());
        return new Order(
                this.getId(),
                this.getCustomer() != null ? this.getCustomer().toDomain() : null,
                this.toDomain().getDishIds(),
                restaurantId,
                this.getStatus()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CustomerEmbeddable getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEmbeddable customer) {
        this.customer = customer;
    }

    public List<UUID> getDishIds() {
        return dishIds;
    }

    public void setDishIds(List<UUID> dishIds) {
        this.dishIds = dishIds;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}


