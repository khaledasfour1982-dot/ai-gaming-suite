package be.kdg.prog6.restaurantManagement.core;

import be.kdg.prog6.restaurantManagement.domain.Dish;
import be.kdg.prog6.restaurantManagement.domain.DishDraft;
import be.kdg.prog6.restaurantManagement.domain.Restaurant;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.ports.in.AddDishPort;
import be.kdg.prog6.restaurantManagement.ports.out.LoadRestaurantsPort;
import be.kdg.prog6.restaurantManagement.ports.out.SaveRestaurantPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateDishImp implements AddDishPort {
    private final Logger logger = LoggerFactory.getLogger(CreateRestaurantImp.class);
    private final SaveRestaurantPort saveRestaurantPort;
    private final LoadRestaurantsPort loadRestaurantsPort;

    public CreateDishImp(SaveRestaurantPort saveRestaurantPort, LoadRestaurantsPort loadRestaurantsPort) {
        this.saveRestaurantPort = saveRestaurantPort;
        this.loadRestaurantsPort = loadRestaurantsPort;
    }

    @Transactional
    @Override
    public Dish addDish(RestaurantId restaurantId, DishDraft draft) {
        logger.info("Loading restaurant with id: {}", restaurantId);
        Restaurant restaurant = loadRestaurantsPort.loadById(restaurantId).orElseThrow();

        logger.info("Current dishes count before add: {}", restaurant.getDishes().size());
        for (Dish existingDish : restaurant.getDishes()) {
            logger.info("Existing dish: {} - Status: {}", existingDish.getName(), existingDish.getStatus());
        }

        restaurant.addDish(draft);

        logger.info("Dishes count after add: {}", restaurant.getDishes().size());
        for (Dish updatedDish : restaurant.getDishes()) {
            logger.info("Dish after adding: {} - Status: {}", updatedDish.getName(), updatedDish.getStatus());
        }

        saveRestaurantPort.saveRestaurant(restaurant);

        Dish addedDish = restaurant.getDishes().getLast();
        logger.info("Added dish returned: {} - Status: {}", addedDish.getName(), addedDish.getStatus());

        return addedDish;
    }

}
