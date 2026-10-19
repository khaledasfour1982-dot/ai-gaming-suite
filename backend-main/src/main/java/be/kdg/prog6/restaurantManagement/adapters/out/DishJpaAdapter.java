package be.kdg.prog6.restaurantManagement.adapters.out;

import be.kdg.prog6.restaurantManagement.domain.*;
import be.kdg.prog6.restaurantManagement.ports.out.LoadDishPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DishJpaAdapter implements LoadDishPort {
    private final DishJpaRepository dishJpaRepository;

    public DishJpaAdapter(DishJpaRepository dishJpaRepository) {
        this.dishJpaRepository = dishJpaRepository;
    }

    @Override
    public List<Dish> loadDishes(RestaurantId restaurantId) {
        List<DishJpaEntity> dishEntities = dishJpaRepository.findByRestaurant_Id(restaurantId.uuid());
        return dishEntities.stream()
                .map(this::mapDishEntityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Dish> loadDish(RestaurantId restaurantId, DishId dishId) {
        Optional<DishJpaEntity> dishEntityOpt = dishJpaRepository.findByRestaurant_IdAndId(
                restaurantId.uuid(),
                dishId.uuid());

        return dishEntityOpt.map(this::mapDishEntityToDomain);
    }

    private Dish mapDishEntityToDomain(DishJpaEntity dishEntity) {
        Dish dish = new Dish(
                DishId.of(dishEntity.getId()),
                dishEntity.getName(),
                dishEntity.getType(),
                dishEntity.getFoodTags(),
                dishEntity.getDescription(),
                dishEntity.getPrice(),
                dishEntity.getPicture(),
                dishEntity.getStatus());
        dish.setDraft(dishEntity.getDraft());
        return dish;

    }

    private DishJpaEntity mapDishDomainToEntity(Dish dish) {
        DishJpaEntity entity = new DishJpaEntity();
        entity.setId(dish.getId().uuid());
        entity.setName(dish.getName());
        entity.setType(dish.getType());
        entity.setFoodTags(dish.getFoodTags());
        entity.setDescription(dish.getDescription());
        entity.setPrice(dish.getPrice());
        entity.setPicture(dish.getPicture());
        entity.setStatus(dish.getStatus());

        entity.setDraft(dish.getDraft());
        return entity;
    }

}
