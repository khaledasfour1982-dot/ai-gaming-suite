package be.kdg.prog6.ordering.adapters.in.response;

public record DishStatusDto(
        String id,
        String name,
        double price,
        boolean outOfStock) {}
