package be.kdg.prog6.restaurantManagement.core;

import be.kdg.prog6.common.events.DishPublishedEvent;
import be.kdg.prog6.restaurantManagement.domain.Restaurant;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.ports.in.ApplyPendingDishChangesPort;
import be.kdg.prog6.restaurantManagement.ports.out.SaveRestaurantPort;
import be.kdg.prog6.restaurantManagement.ports.out.LoadRestaurantsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplyPendingDishChangesImp implements ApplyPendingDishChangesPort {
    private final LoadRestaurantsPort loadRestaurantPort;
    private final SaveRestaurantPort saveRestaurantPort;

    public ApplyPendingDishChangesImp(
            LoadRestaurantsPort loadRestaurantsPort,
            SaveRestaurantPort saveRestaurantPort) {
        this.loadRestaurantPort = loadRestaurantsPort;
        this.saveRestaurantPort = saveRestaurantPort;
    }
    @Transactional
    @Override
    public List<DishPublishedEvent> applyPendingDishChanges(RestaurantId restaurantId) {
        Restaurant restaurant = loadRestaurantPort.loadById(restaurantId).orElseThrow();
        List<DishPublishedEvent> batchEvents = restaurant.applyPendingDishChanges();
        saveRestaurantPort.saveRestaurant(restaurant);
        return batchEvents;
    }
}
