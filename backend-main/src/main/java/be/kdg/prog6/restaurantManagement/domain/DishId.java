package be.kdg.prog6.restaurantManagement.domain;

import java.util.UUID;

public record DishId(UUID uuid) {

    public static DishId create() {
        return new DishId(UUID.randomUUID());
    }

    public static DishId of(UUID uuid) {
        return new DishId(uuid);
    }

}
