package be.kdg.prog6.restaurantManagement.ports.out;

import be.kdg.prog6.restaurantManagement.domain.Dish;
import be.kdg.prog6.restaurantManagement.domain.DishId;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;

import java.util.List;
import java.util.Optional;

public interface LoadDishPort {
    List<Dish> loadDishes(RestaurantId restaurantId);
    Optional<Dish> loadDish(RestaurantId restaurantId, DishId dishId);
}