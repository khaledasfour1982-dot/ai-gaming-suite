package be.kdg.prog6.restaurantManagement.core;

import be.kdg.prog6.restaurantManagement.domain.Restaurant;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.domain.WeeklySchedule;
import be.kdg.prog6.restaurantManagement.ports.in.SetOpeningHoursPort;
import be.kdg.prog6.restaurantManagement.ports.out.LoadRestaurantsPort;
import be.kdg.prog6.restaurantManagement.ports.out.SaveRestaurantPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetOpeningHoursImp implements SetOpeningHoursPort {
    private final LoadRestaurantsPort loadRestaurantsPort;
    private final SaveRestaurantPort saveRestaurantPort;

    public SetOpeningHoursImp(LoadRestaurantsPort loadRestaurantsPort, SaveRestaurantPort saveRestaurantPort) {
        this.loadRestaurantsPort = loadRestaurantsPort;
        this.saveRestaurantPort = saveRestaurantPort;
    }

    @Transactional
    @Override
    public void setOpeningHours(RestaurantId restaurantId, WeeklySchedule openingHours) {
        Restaurant restaurant = loadRestaurantsPort.loadById(restaurantId).orElseThrow();
        restaurant.setOpeningHours(openingHours);
        saveRestaurantPort.saveRestaurant(restaurant);

    }
}
