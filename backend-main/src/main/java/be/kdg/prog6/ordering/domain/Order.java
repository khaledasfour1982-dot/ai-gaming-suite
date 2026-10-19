package be.kdg.prog6.ordering.domain;

import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final Customer customer;
    private final List<DishId> dishIds;
    private final UUID restaurantId;
    private OrderStatus status;

    public Order(UUID id, Customer customer, List<DishId> dishIds,UUID restaurantId, OrderStatus status) {
        this.id = id;
        this.customer = customer;
        this.dishIds = dishIds;
        this.restaurantId = restaurantId;
        this.status = status;
    }

    public UUID getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<DishId> getDishIds() { return dishIds; }
    public OrderStatus getStatus() { return status; }
    public UUID getRestaurantId() {
        return restaurantId;
    }


    public void markReady() {
        if (status != OrderStatus.ACCEPTED) throw new IllegalStateException("Order is not Accepted yet!");
        status = OrderStatus.READY;
    }

}


