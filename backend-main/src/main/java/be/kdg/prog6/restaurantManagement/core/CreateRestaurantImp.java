package be.kdg.prog6.restaurantManagement.core;

import be.kdg.prog6.restaurantManagement.domain.Restaurant;
import be.kdg.prog6.restaurantManagement.ports.in.CreateRestaurantPort;
import be.kdg.prog6.restaurantManagement.ports.out.SaveRestaurantPort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateRestaurantImp implements CreateRestaurantPort {
    private final Logger logger = LoggerFactory.getLogger(CreateRestaurantImp.class);
    private final SaveRestaurantPort saveRestaurantPort;
    public CreateRestaurantImp(SaveRestaurantPort saveRestaurantPort) {
        this.saveRestaurantPort = saveRestaurantPort;
    }

    @Transactional
    public Restaurant create(Restaurant restaurant) {
        logger.info("Restaurant created with name : {}", restaurant.getName());
        return this.saveRestaurantPort.saveRestaurant(restaurant);
    }

    @Override
    @Transactional
    public Restaurant update(Restaurant restaurant) {
        logger.info("Updating restaurant with id: {}", restaurant.getId());
        return saveRestaurantPort.saveRestaurant(restaurant);
    }


}
