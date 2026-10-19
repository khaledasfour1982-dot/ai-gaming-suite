package be.kdg.prog6.restaurantManagement.ports.in;

import be.kdg.prog6.restaurantManagement.domain.Dish;
import be.kdg.prog6.restaurantManagement.domain.DishId;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;

public interface MarkDishOutOfStockPort {
    Dish markOutOfStock(DishId dishId, RestaurantId restaurantId);
}

