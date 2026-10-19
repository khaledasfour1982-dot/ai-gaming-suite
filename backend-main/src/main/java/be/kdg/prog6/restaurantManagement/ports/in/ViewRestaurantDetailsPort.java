package be.kdg.prog6.restaurantManagement.ports.in;

import be.kdg.prog6.restaurantManagement.domain.RestaurantDetails;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;

public interface ViewRestaurantDetailsPort {
    RestaurantDetails viewDetails(RestaurantId restaurantId);
}
