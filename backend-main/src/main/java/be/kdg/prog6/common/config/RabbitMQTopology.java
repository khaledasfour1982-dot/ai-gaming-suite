package be.kdg.prog6.common.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    public static final String TOPIC_EXCHANGE_NAME = "kdg.events";
    public static final String ORDER_ACCEPTED_QUEUE = "order.accepted.queue";
    public static final String ORDER_READY_QUEUE = "order.ready.queue";
    public static final String PICKED_UP_QUEUE = "order.pickedup.queue";
    public static final String DELIVERED_QUEUE = "order.delivered.queue";
    public static final String LOCATION_QUEUE = "courier.location.queue";
    public static final String DISH_OUT_OF_STOCK_QUEUE = "dish.outofstock.queue";
    public static final String DISH_PUBLISHED_QUEUE = "dish.published.queue";

    @Bean
    public Queue dishOutOfStockQueue() {
        return QueueBuilder.durable(DISH_OUT_OF_STOCK_QUEUE).build();
    }

    @Bean
    public Binding dishOutOfStockBinding(Queue dishOutOfStockQueue, TopicExchange kdgEventsExchange) {
        return BindingBuilder.bind(dishOutOfStockQueue).to(kdgEventsExchange).with("dish.outofstock.v1");
    }

    @Bean
    public Queue dishPublishedQueue() {
        return QueueBuilder.durable(DISH_PUBLISHED_QUEUE).build();
    }

    @Bean
    public Binding dishPublishedBinding(Queue dishPublishedQueue, TopicExchange kdgEventsExchange) {
        return BindingBuilder
                .bind(dishPublishedQueue)
                .to(kdgEventsExchange)
                .with("dish.published.v1");
    }

    @Bean
    public TopicExchange kdgEventsExchange()
    {
        return ExchangeBuilder
                .topicExchange(TOPIC_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Queue orderAcceptedQueue() {
        return QueueBuilder
                .durable(ORDER_ACCEPTED_QUEUE)
                .build();
    }

    @Bean
    public Queue orderReadyQueue() {
        return QueueBuilder
                .durable(ORDER_READY_QUEUE)
                .build();
    }

    @Bean
    public Queue pickedUpQueue() {
        return QueueBuilder
                .durable(PICKED_UP_QUEUE)
                .build();
    }
    @Bean
    public Queue deliveredQueue() {
        return QueueBuilder
                .durable(DELIVERED_QUEUE)
                .build();
    }
    @Bean
    public Queue locationQueue() {
        return QueueBuilder
                .durable(LOCATION_QUEUE)
                .build();
    }

    @Bean
    public Binding orderAcceptedBinding(Queue orderAcceptedQueue, TopicExchange kdgEventsExchange) {
        return BindingBuilder
                .bind(orderAcceptedQueue)
                .to(kdgEventsExchange)
                .with("restaurant.*.order.accepted.v1");
    }

    @Bean
    public Binding orderReadyBinding(Queue orderReadyQueue, TopicExchange kdgEventsExchange) {
        return BindingBuilder
                .bind(orderReadyQueue)
                .to(kdgEventsExchange)
                .with("restaurant.*.order.ready.v1");
    }

    @Bean
    public Binding pickedUpBinding(Queue pickedUpQueue, TopicExchange kdgEventsExchange) {
        return BindingBuilder
                .bind(pickedUpQueue)
                .to(kdgEventsExchange)
                .with("delivery.*.order.pickedup.v1");
    }


    @Bean
    public Binding deliveredBinding(Queue deliveredQueue, TopicExchange kdgEventsExchange) {
        return BindingBuilder
                .bind(deliveredQueue)
                .to(kdgEventsExchange)
                .with("delivery.*.order.delivered.v1");
    }

}
