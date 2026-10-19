//package be.kdg.prog6.delivery.adapters.in;
//
//import be.kdg.prog6.common.config.RabbitMQTopology;
//import be.kdg.prog6.common.events.delivery.OrderAcceptedEvent;
//import be.kdg.prog6.common.events.delivery.OrderDeliveredEvent;
//import be.kdg.prog6.common.events.delivery.OrderPickedUpEvent;
//import be.kdg.prog6.common.events.delivery.OrderReadyEvent;
//import be.kdg.prog6.delivery.domain.Delivery;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//@Component
//public class DeliveryEventListener {
//    private final Map<UUID, Delivery> deliveries = new HashMap<>();
//
//
//    @RabbitListener(queues = RabbitMQTopology.ORDER_ACCEPTED_QUEUE)
//    public void handleOrderAcceptedEvent(OrderAcceptedEvent event) {
//        Delivery delivery = deliveries.get(event.getOrderId());
//        if (delivery != null) {
//            delivery.markReady();
//            System.out.println("Order ready for delivery: " + event.getOrderId());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMQTopology.ORDER_READY_QUEUE)
//    public void handleOrderReadyEvent(OrderReadyEvent event) {
//        Delivery delivery = new Delivery(event.getOrderId());
//        deliveries.put(event.getOrderId(), delivery);
//        System.out.println("Created delivery for order: " + event.getOrderId());
//    }
//
//    @RabbitListener(queues = RabbitMQTopology.PICKED_UP_QUEUE)
//    public void onOrderPickedUp(OrderPickedUpEvent event) {
//        Delivery delivery = deliveries.get(event.getOrderId());
//        if (delivery != null) {
//            delivery.pickUp();
//            System.out.println("Order picked up: " + event.getOrderId());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMQTopology.DELIVERED_QUEUE)
//    public void onOrderDelivered(OrderDeliveredEvent event) {
//        Delivery delivery = deliveries.get(event.getOrderId());
//        if (delivery != null) {
//            delivery.deliver();
//            System.out.println("Order delivered: " + event.getOrderId());
//        }
//
//
//    }
//}
