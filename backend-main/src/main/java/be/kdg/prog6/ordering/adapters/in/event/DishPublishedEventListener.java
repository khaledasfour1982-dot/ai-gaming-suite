package be.kdg.prog6.ordering.adapters.in.event;

import be.kdg.prog6.common.config.RabbitMQTopology;
import be.kdg.prog6.common.events.DishPublishedEvent;
import be.kdg.prog6.ordering.core.OutOfStockDishProjectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;


@Component
public class DishPublishedEventListener {
    private final Logger logger = LoggerFactory.getLogger(DishPublishedEventListener.class);
    private final OutOfStockDishProjectionService projectionService;

    public DishPublishedEventListener(OutOfStockDishProjectionService projectionService) {
        this.projectionService = projectionService;
    }

    @RabbitListener(queues = RabbitMQTopology.DISH_PUBLISHED_QUEUE)
    @Transactional
    public void handleDishPublishedEvent(DishPublishedEvent event) {
        logger.info("Handling DishPublishedEvent for dishId: {}", event.getDishId());

        projectionService.updateDishInfo(event.getDishId(),event.getName(),event.getPrice());

        projectionService.markRestocked(event.getDishId());
    }
}
