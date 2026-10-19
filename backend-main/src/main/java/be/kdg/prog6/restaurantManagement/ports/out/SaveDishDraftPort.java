package be.kdg.prog6.restaurantManagement.ports.out;

import be.kdg.prog6.restaurantManagement.domain.Dish;

import be.kdg.prog6.restaurantManagement.domain.RestaurantId;

public interface SaveDishDraftPort {
    Dish saveDraft(RestaurantId restaurantId, Dish dish);
}

