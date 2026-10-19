package be.kdg.prog6.restaurantManagement.ports.in;

import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.domain.WeeklySchedule;

public interface SetOpeningHoursPort {
    void setOpeningHours(RestaurantId restaurantId, WeeklySchedule openingHours);
}
