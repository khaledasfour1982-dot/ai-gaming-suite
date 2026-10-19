package be.kdg.prog6.restaurantManagement.core;

import be.kdg.prog6.restaurantManagement.domain.Dish;
import be.kdg.prog6.restaurantManagement.domain.DishId;
import be.kdg.prog6.restaurantManagement.domain.Restaurant;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.ports.in.PublishDishDraftPort;
import be.kdg.prog6.restaurantManagement.ports.in.UnpublishDishDraftPort;
import be.kdg.prog6.restaurantManagement.ports.out.LoadRestaurantsPort;
import be.kdg.prog6.restaurantManagement.ports.out.SaveRestaurantPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishDishImp implements PublishDishDraftPort, UnpublishDishDraftPort {
    private static final Logger logger = LoggerFactory.getLogger(PublishDishImp.class);

    private final LoadRestaurantsPort loadRestaurantsPort;
    private final SaveRestaurantPort saveRestaurantPort;
    public PublishDishImp(LoadRestaurantsPort loadRestaurantsPort, SaveRestaurantPort saveRestaurantPort) {
        this.loadRestaurantsPort = loadRestaurantsPort;
        this.saveRestaurantPort = saveRestaurantPort;
    }

    @Transactional
    @Override
    public Dish publishDish(RestaurantId restaurantId, DishId dishId){
        Restaurant restaurant = loadRestaurantsPort.loadById(restaurantId).orElseThrow();
        Dish dish = restaurant.getDishById(dishId);
        if (!dish.hasDraft()) {
            throw new IllegalStateException("No draft to publish");
        }
        restaurant.publishDish(dishId);
        saveRestaurantPort.saveRestaurant(restaurant);
        return restaurant.getDishById(dishId);
    }


    @Transactional
    @Override
    public Dish unpublishDish(DishId dishId, RestaurantId restaurantId) {
        Restaurant restaurant = loadRestaurantsPort.loadById(restaurantId).orElseThrow();
        restaurant.unpublishDish(dishId);
        saveRestaurantPort.saveRestaurant(restaurant);
        return restaurant.getDishById(dishId);
    }



}
