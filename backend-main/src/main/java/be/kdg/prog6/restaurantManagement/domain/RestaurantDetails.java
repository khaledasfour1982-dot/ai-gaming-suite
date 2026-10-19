package be.kdg.prog6.restaurantManagement.domain;
import java.util.List;

public record RestaurantDetails(
        Restaurant restaurant,
        List<Dish> dishes
) {}
