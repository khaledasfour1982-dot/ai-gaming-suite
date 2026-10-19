package be.kdg.prog6.restaurantManagement.core;

import be.kdg.prog6.restaurantManagement.domain.RestaurantDetails;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.ports.in.ViewRestaurantDetailsPort;
import be.kdg.prog6.restaurantManagement.ports.out.LoadRestaurantsPort;
import org.springframework.stereotype.Service;

@Service
public class ViewRestaurantDetailsImp implements ViewRestaurantDetailsPort {
    private final LoadRestaurantsPort loadRestaurantsPort;

    public ViewRestaurantDetailsImp(LoadRestaurantsPort loadRestaurantsPort) {
        this.loadRestaurantsPort = loadRestaurantsPort;
    }

    @Override
    public RestaurantDetails viewDetails(RestaurantId restaurantId) {
        return loadRestaurantsPort.loadDetailsById(restaurantId);
    }

}
