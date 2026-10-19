package be.kdg.prog6.restaurantManagement.ports.out;

import be.kdg.prog6.restaurantManagement.domain.*;

import java.util.List;
import java.util.Optional;

public interface LoadRestaurantsPort {
    List<Restaurant> loadAll();
    Optional<Restaurant> loadById(RestaurantId id);
    RestaurantDetails loadDetailsById(RestaurantId id);
    List<Restaurant> loadByOwner(String ownerName);

}
