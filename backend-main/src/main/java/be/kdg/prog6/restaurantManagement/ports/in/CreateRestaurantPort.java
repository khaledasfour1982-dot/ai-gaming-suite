package be.kdg.prog6.restaurantManagement.ports.in;

import be.kdg.prog6.restaurantManagement.domain.Dish;
import be.kdg.prog6.restaurantManagement.domain.DishDraft;
import be.kdg.prog6.restaurantManagement.domain.Restaurant;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;

public interface CreateRestaurantPort {
    Restaurant create(Restaurant restaurant);

    Restaurant update(Restaurant restaurant);
}
