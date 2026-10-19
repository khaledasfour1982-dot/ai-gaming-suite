package be.kdg.prog6.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public class DishUnpublishedEvent implements DomainEvent {
    private final UUID dishId;
    private final UUID restaurantId;
    private final LocalDateTime eventTime;

    public DishUnpublishedEvent(LocalDateTime eventTime,UUID dishId, UUID restaurantId) {
        this.dishId = dishId;
        this.restaurantId = restaurantId;
        this.eventTime = eventTime;
    }

    public DishUnpublishedEvent(UUID dishId, UUID restaurantId) {
        this(LocalDateTime.now(), dishId, restaurantId);
    }

    public UUID getDishId() {
        return dishId;
    }

    @Override
    public LocalDateTime eventTime() {
        return eventTime;
    }
}
