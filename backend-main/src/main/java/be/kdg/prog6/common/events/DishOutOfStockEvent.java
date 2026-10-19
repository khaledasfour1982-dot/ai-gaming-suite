package be.kdg.prog6.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public class DishOutOfStockEvent implements DomainEvent {
    private final UUID dishId;
    private final UUID restaurantId;

    private final LocalDateTime eventTime;

    public DishOutOfStockEvent(LocalDateTime eventTime, UUID dishId, UUID restaurantId) {
        this.dishId = dishId;
        this.restaurantId = restaurantId;
        this.eventTime = eventTime;
    }

    public UUID getDishId() {
        return dishId;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    @Override
    public LocalDateTime eventTime() {
        return eventTime;
    }
}
