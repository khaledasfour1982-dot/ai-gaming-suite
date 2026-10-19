package be.kdg.prog6.restaurantManagement.ports.in;

import be.kdg.prog6.restaurantManagement.domain.Restaurant;

import java.util.List;

public interface ExploreRestaurantsPort {
    List<Restaurant> findAll();
}
