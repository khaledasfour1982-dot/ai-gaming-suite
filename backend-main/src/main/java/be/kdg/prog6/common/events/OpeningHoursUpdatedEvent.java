package be.kdg.prog6.common.events;

import be.kdg.prog6.restaurantManagement.domain.WeeklySchedule;
import java.time.LocalDateTime;
import java.util.UUID;

public class OpeningHoursUpdatedEvent implements DomainEvent {
    private final UUID restaurantId;
    private final WeeklySchedule newSchedule;
    private final LocalDateTime eventTime;

    public OpeningHoursUpdatedEvent(UUID restaurantId, WeeklySchedule newSchedule) {
        this.restaurantId = restaurantId;
        this.newSchedule = newSchedule;
        this.eventTime = LocalDateTime.now();
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public WeeklySchedule getNewSchedule() {
        return newSchedule;
    }

    @Override
    public LocalDateTime eventTime() {
        return eventTime;
    }
}
