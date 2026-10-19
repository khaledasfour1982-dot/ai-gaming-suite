package be.kdg.prog6.ordering.adapters.in.event;

import be.kdg.prog6.common.config.RabbitMQTopology;
import be.kdg.prog6.common.events.DishOutOfStockEvent;
import be.kdg.prog6.ordering.adapters.in.BasketController;
import be.kdg.prog6.ordering.core.BuildBasketImp;
import be.kdg.prog6.ordering.core.OutOfStockDishProjectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
public class DishOutOfStockEventListener {
    private final Logger logger = LoggerFactory.getLogger(DishOutOfStockEventListener.class);
    private final OutOfStockDishProjectionService projectionService;
    public DishOutOfStockEventListener(OutOfStockDishProjectionService projectionService) {
        this.projectionService = projectionService;
    }

    @RabbitListener(queues = RabbitMQTopology.DISH_OUT_OF_STOCK_QUEUE)
    @Transactional
    public void handleDishOutOfStockEvent(DishOutOfStockEvent event) {
        logger.info("Handling DishOutOfStockEvent for dishId: {}", event.getDishId());
        projectionService.markOutOfStock(event.getDishId());
    }
}
