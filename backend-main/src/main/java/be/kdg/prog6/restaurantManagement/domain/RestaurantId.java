package be.kdg.prog6.restaurantManagement.domain;

import java.util.UUID;

public record RestaurantId(UUID uuid) {

    public static RestaurantId create() {
        return new RestaurantId(UUID.randomUUID());
    }
    public static RestaurantId of(UUID uuid) {
        return new RestaurantId(uuid);
    }

}
