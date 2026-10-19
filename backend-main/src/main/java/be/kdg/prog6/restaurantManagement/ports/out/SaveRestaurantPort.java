package be.kdg.prog6.restaurantManagement.ports.out;


import be.kdg.prog6.restaurantManagement.domain.Restaurant;

public interface SaveRestaurantPort {
    Restaurant saveRestaurant(Restaurant restaurant);
}
