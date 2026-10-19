package be.kdg.prog6.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public class DishPublishedEvent implements DomainEvent {
    private final UUID dishId;
    private final UUID restaurantId;
    private final String name;
    private final double price;
    private final LocalDateTime eventTime;

    public DishPublishedEvent(LocalDateTime eventTime, UUID dishId, UUID restaurantId, String name, double price) {
        this.dishId = dishId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.eventTime = eventTime;
    }

    public DishPublishedEvent(UUID dishId, UUID restaurantId, String name, double price) {
        this(LocalDateTime.now(), dishId, restaurantId, name, price);
    }

    @Override
    public LocalDateTime eventTime() {
        return eventTime;
    }

    public UUID getDishId() {
        return dishId;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
