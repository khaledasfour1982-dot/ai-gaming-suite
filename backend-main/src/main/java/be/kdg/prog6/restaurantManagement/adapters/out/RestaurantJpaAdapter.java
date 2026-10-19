package be.kdg.prog6.restaurantManagement.adapters.out;

import be.kdg.prog6.restaurantManagement.domain.*;
import be.kdg.prog6.restaurantManagement.ports.out.LoadRestaurantsPort;
import be.kdg.prog6.restaurantManagement.ports.out.SaveDishDraftPort;
import be.kdg.prog6.restaurantManagement.ports.out.SaveRestaurantPort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RestaurantJpaAdapter implements SaveRestaurantPort, LoadRestaurantsPort, SaveDishDraftPort {
    private final RestaurantJpaRepository restaurantJpaRepository;

    public RestaurantJpaAdapter(RestaurantJpaRepository restaurantJpaRepository)
    {
        this.restaurantJpaRepository = restaurantJpaRepository;
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        UUID restaurantId = restaurant.getId().uuid();
        RestaurantJpaEntity entity = restaurantJpaRepository.findById(restaurantId)
                .orElseGet(() -> new RestaurantJpaEntity(
                        restaurantId,
                        restaurant.getName(),
                        new AddressEmbeddable(restaurant.getFullAddress()),
                        restaurant.getContactEmail(),
                        restaurant.getPicture(),
                        restaurant.getTypeOfCuisine(),
                        restaurant.getPreparationTime(),
                        restaurant.getOpeningHours(),
                        restaurant.getOwner()
                ));

        entity.setName(restaurant.getName());
        entity.setOpeningHours(restaurant.getOpeningHours());
        List<DishJpaEntity> existingDishes = entity.getDishes();

        for (Dish dish : restaurant.getDishes()) {
            DishJpaEntity existingDishEntity = existingDishes.stream()
                    .filter(d -> d.getId().equals(dish.getId().uuid()))
                    .findFirst()
                    .orElse(null);

            if (existingDishEntity != null) {
                existingDishEntity.setName(dish.getName());
                existingDishEntity.setType(dish.getType());
                existingDishEntity.setFoodTags(dish.getFoodTags().toString());
                existingDishEntity.setDescription(dish.getDescription());
                existingDishEntity.setPrice(dish.getPrice());
                existingDishEntity.setPicture(dish.getPicture());
                existingDishEntity.setStatus(dish.getStatus());
                existingDishEntity.setDraft(dish.getDraft());

            } else {
                DishJpaEntity newDishEntity = mapDishDomainToEntity(dish);
                newDishEntity.setRestaurant(entity);
                existingDishes.add(newDishEntity);
            }
        }

        RestaurantJpaEntity saved = restaurantJpaRepository.save(entity);
        return mapRestaurantEntityToDomain(saved);
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

        if (dish.getDraft() != null) {
            entity.setDraft(dish.getDraft());
        } else {
            entity.setDraft(null);
        }

        return entity;
    }


    @Override
    public List<Restaurant> loadAll() {
        return restaurantJpaRepository.findAll()
                .stream()
                .map(entity -> {
                    List<Dish> dishes = entity.getDishes().stream()
                            .map(this::mapDishEntityToDomain)
                            .collect(Collectors.toList());
                    return new Restaurant(
                            RestaurantId.of(entity.getId()),
                            entity.getName(),
                            entity.getFullAddress().toDomain(),
                            entity.getContactEmail(),
                            entity.getPicture(),
                            entity.getTypeOfCuisine(),
                            entity.getPreparationTime(),
                            entity.getOpeningHours(),
                            entity.getOwner(),
                            dishes
                    );
                })
                .toList();
    }

    @Override
    public Optional<Restaurant> loadById(RestaurantId id) {
        return restaurantJpaRepository.findById(id.uuid())
                .map(entity -> {
                    List<Dish> dishes = entity.getDishes().stream()
                            .map(this::mapDishEntityToDomain)
                            .toList();

                    return new Restaurant(
                            RestaurantId.of(entity.getId()),
                            entity.getName(),
                            entity.getFullAddress().toDomain(),
                            entity.getContactEmail(),
                            entity.getPicture(),
                            entity.getTypeOfCuisine(),
                            entity.getPreparationTime(),
                            entity.getOpeningHours(),
                            entity.getOwner(),
                            dishes);
                });
    }



    @Override
    public RestaurantDetails loadDetailsById(RestaurantId id) {
        RestaurantJpaEntity entity = restaurantJpaRepository.findById(id.uuid())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + id));

        List<Dish> publishedDishes = entity.getDishes().stream()
                .filter(dishEntity -> dishEntity.getStatus() == DishStatus.PUBLISHED ||
                        dishEntity.getStatus() == DishStatus.OUT_OF_STOCK)
                .map(this::mapDishEntityToDomain)
                .collect(Collectors.toList());

        Restaurant restaurantDomain = mapRestaurantEntityToDomain(entity);

        return new RestaurantDetails(restaurantDomain, publishedDishes);
    }

    @Override
    public List<Restaurant> loadByOwner(String ownerName) {
        System.out.println("Adapter: loading by owner: " + ownerName);
        List<RestaurantJpaEntity> restaurantEntity = restaurantJpaRepository.findByOwner(ownerName);
        System.out.println("Adapter: found " + restaurantEntity.size() + " entities");
        return restaurantEntity
                .stream()
                .map(this::mapRestaurantEntityToDomain)
                .toList();
    }


    private Restaurant mapRestaurantEntityToDomain(RestaurantJpaEntity entity) {
        List<Dish> dishes = entity.getDishes()
                .stream()
                .map(this::mapDishEntityToDomain)
                .collect(Collectors.toList());

        return new Restaurant(
                RestaurantId.of(entity.getId()),
                entity.getName(),
                entity.getFullAddress().toDomain(),
                entity.getContactEmail(),
                entity.getPicture(),
                entity.getTypeOfCuisine(),
                entity.getPreparationTime(),
                entity.getOpeningHours(),
                entity.getOwner(),
                dishes
        );
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
                dishEntity.getStatus()
        );

        if (dishEntity.getDraft() != null) {
            dish.setDraft(dishEntity.getDraft());
        }

        return dish;
    }


    public Dish saveDraft(RestaurantId restaurantId, Dish dish) {
        Restaurant restaurant = loadById(restaurantId).orElseThrow();

        Dish dishToUpdate = restaurant.getDishById(dish.getId());
        dishToUpdate.editDraft(
                dish.getName(),
                dish.getType(),
                dish.getFoodTags(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getPicture(),
                dish.getStatus()
        );

        saveRestaurant(restaurant);
        System.out.println("Saving draft for dish: " + dish.getName());
        System.out.println("Draft: " + dish.getDraft());

        return dishToUpdate;
    }


}
