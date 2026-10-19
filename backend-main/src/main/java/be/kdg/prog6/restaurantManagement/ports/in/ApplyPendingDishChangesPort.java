package be.kdg.prog6.restaurantManagement.ports.in;

import be.kdg.prog6.common.events.DishPublishedEvent;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;

import java.util.List;

public interface ApplyPendingDishChangesPort {
    List<DishPublishedEvent> applyPendingDishChanges(RestaurantId restaurantId);
}
