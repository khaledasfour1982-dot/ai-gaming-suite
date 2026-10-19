package be.kdg.prog6.ordering.domain;

import java.util.UUID;

public record DishId(UUID id) {

    public static DishId of(UUID id) {
        return new DishId(id);
    }

    public static DishId fromString(String id) {
        return new DishId(UUID.fromString(id));
    }
}
