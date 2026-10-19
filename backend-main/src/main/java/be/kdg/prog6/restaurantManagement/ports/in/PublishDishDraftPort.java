package be.kdg.prog6.restaurantManagement.ports.in;

import be.kdg.prog6.restaurantManagement.domain.Dish;
import be.kdg.prog6.restaurantManagement.domain.DishId;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import org.springframework.transaction.annotation.Transactional;

public interface PublishDishDraftPort {
    Dish publishDish(RestaurantId restaurantId, DishId dishId);
}
