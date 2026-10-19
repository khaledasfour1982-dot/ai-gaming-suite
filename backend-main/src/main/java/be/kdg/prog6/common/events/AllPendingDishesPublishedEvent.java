package be.kdg.prog6.common.events;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AllPendingDishesPublishedEvent implements DomainEvent {
    private final UUID restaurantId;
    private final List<UUID> publishedDishIds;
    private final LocalDateTime eventTime;

    public AllPendingDishesPublishedEvent(UUID restaurantId, List<UUID> publishedDishIds) {
        this.restaurantId = restaurantId;
        this.publishedDishIds = publishedDishIds;
        this.eventTime = LocalDateTime.now();
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public List<UUID> getPublishedDishIds() {
        return publishedDishIds;
    }

    @Override
    public LocalDateTime eventTime() {
        return eventTime;
    }
}
