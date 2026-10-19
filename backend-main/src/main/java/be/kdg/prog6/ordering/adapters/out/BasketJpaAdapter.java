package be.kdg.prog6.ordering.adapters.out;

import be.kdg.prog6.ordering.domain.Basket;
import be.kdg.prog6.ordering.domain.Customer;
import be.kdg.prog6.ordering.domain.CustomerEmbeddable;
import be.kdg.prog6.ordering.domain.DishId;
import be.kdg.prog6.ordering.ports.out.FindBasketPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class BasketJpaAdapter implements FindBasketPort {

    private final BasketJpaRepository basketJpaRepository;

    public BasketJpaAdapter(BasketJpaRepository basketJpaRepository) {
        this.basketJpaRepository = basketJpaRepository;
    }

    @Override
    public void save(Basket basket) {
        BasketJpaEntity entity = toEntity(basket);
        basketJpaRepository.save(entity);
    }

    @Override
    public Basket findById(UUID basketId) {
        return basketJpaRepository.findById(basketId)
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Basket not found: " + basketId));
    }

    private BasketJpaEntity toEntity(Basket basket) {
        BasketJpaEntity entity = new BasketJpaEntity();
        entity.setId(basket.getId());
        entity.setCustomer(CustomerEmbeddable.fromDomain(basket.getCustomer()));  // convert domain to embeddable
        entity.setDishIds(basket.getDishIds().stream()
                .map(DishId::id)
                .collect(Collectors.toList()));
        entity.setRestaurantId(basket.getRestaurantId().toString());
        return entity;
    }

    private Basket toDomain(BasketJpaEntity entity) {
        Customer customer = entity.getCustomer().toDomain();  // convert embeddable to domain
        List<DishId> dishIds = entity.getDishIds().stream()
                .map(DishId::new)
                .collect(Collectors.toList());
        UUID restaurantID = UUID.fromString(entity.getRestaurantId());
        return new Basket(entity.getId(), customer, dishIds, restaurantID);
    }

}
