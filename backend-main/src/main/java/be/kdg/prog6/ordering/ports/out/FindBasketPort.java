package be.kdg.prog6.ordering.ports.out;

import be.kdg.prog6.ordering.domain.Basket;

import java.util.UUID;

public interface FindBasketPort {
    void save(Basket basket);

    Basket findById(UUID basketId);
}
