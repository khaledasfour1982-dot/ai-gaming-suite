package be.kdg.prog6.restaurantManagement.adapters.out;

import be.kdg.prog6.common.events.DomainEvent;
import be.kdg.prog6.restaurantManagement.core.PublishDishImp;
import be.kdg.prog6.restaurantManagement.domain.Restaurant;
import be.kdg.prog6.restaurantManagement.ports.out.SaveRestaurantPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class RestaurantEventPublisher implements SaveRestaurantPort {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantEventPublisher.class);

    private final SaveRestaurantPort delegate;
    private final ApplicationEventPublisher applicationEventPublisher;

    public RestaurantEventPublisher(@Qualifier("restaurantJpaAdapter") SaveRestaurantPort delegate, ApplicationEventPublisher applicationEventPublisher) {
        this.delegate = delegate;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        Restaurant saved = delegate.saveRestaurant(restaurant);
        for (DomainEvent event : restaurant.getDomainEvents()) {
            logger.info("Publishing event: {}", event);
            applicationEventPublisher.publishEvent(event);
        }
        restaurant.clearDomainEvents();

        return saved;
    }
}
