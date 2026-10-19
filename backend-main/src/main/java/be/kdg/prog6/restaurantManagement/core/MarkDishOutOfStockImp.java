package be.kdg.prog6.restaurantManagement.core;

import be.kdg.prog6.common.events.DishOutOfStockEvent;
import be.kdg.prog6.restaurantManagement.domain.Dish;
import be.kdg.prog6.restaurantManagement.domain.DishId;
import be.kdg.prog6.restaurantManagement.domain.Restaurant;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.ports.in.MarkDishOutOfStockPort;
import be.kdg.prog6.restaurantManagement.ports.out.LoadRestaurantsPort;
import be.kdg.prog6.restaurantManagement.ports.out.SaveRestaurantPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.core.AmqpTemplate;

import java.time.LocalDateTime;

@Service
public class MarkDishOutOfStockImp implements MarkDishOutOfStockPort {
    private final LoadRestaurantsPort loadRestaurantsPort;
    private final SaveRestaurantPort saveRestaurantPort;
    private final AmqpTemplate amqpTemplate;

    public MarkDishOutOfStockImp(LoadRestaurantsPort loadRestaurantsPort, SaveRestaurantPort saveRestaurantPort, AmqpTemplate amqpTemplate) {
        this.loadRestaurantsPort = loadRestaurantsPort;
        this.saveRestaurantPort = saveRestaurantPort;
        this.amqpTemplate = amqpTemplate;
    }

    @Transactional
    @Override
    public Dish markOutOfStock(DishId dishId, RestaurantId restaurantId) {
        Restaurant restaurant = loadRestaurantsPort.loadById(restaurantId).orElseThrow();
        restaurant.markDishOutOfStock(dishId);
        saveRestaurantPort.saveRestaurant(restaurant);

        DishOutOfStockEvent event = new DishOutOfStockEvent(
                LocalDateTime.now(),
                dishId.uuid(),
                restaurantId.uuid()
        );
        amqpTemplate.convertAndSend("kdg.events", "dish.outofstock.v1", event);

        return restaurant.getDishById(dishId);
    }

}
